package com.lut.grade.controller;

import com.lut.grade.model.Course;
import com.lut.grade.model.CourseGradeTemplate;
import com.lut.grade.model.GradeStats;
import com.lut.grade.model.StudentGrade;
import com.lut.grade.model.TemplateItem;
import com.lut.grade.service.CourseService;
import com.lut.grade.service.GradeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 教师端 - 成绩管理：录入、保存、统计、导出 Excel、批量导入。
 *
 * 总评和绩点现在由后端用课程模板重新算一遍，不再直接信任页面上传来的数字，
 * 免得改一下 DOM 就能随便造分。
 */
@WebServlet("/teacher/grade")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024L, maxRequestSize = 8 * 1024 * 1024L)
public class TeacherGradeServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final Pattern JSON_PAIR =
            Pattern.compile("\"([^\"]+)\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");

    private final GradeService gradeService = new GradeService();
    private final CourseService courseService = new CourseService();

    // ===================== 成绩录入页 =====================

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/teacher/course?method=list", "请先选择课程", "warning");
            return;
        }
        CourseGradeTemplate template = courseService.getTemplateByCourseId(courseId);
        if (template == null) {
            redirect(req, resp, "/teacher/course?method=list",
                    "该课程还没有配置成绩计算模板，请让管理员先配置", "warning");
            return;
        }

        List<StudentGrade> gradeList = gradeService.getStudentsByCourse(courseId);
        boolean isSubmitted = false;
        if (gradeList != null) {
            for (StudentGrade sg : gradeList) {
                if ("submitted".equals(sg.getStatus())) {
                    isSubmitted = true;
                    break;
                }
            }
        }

        req.setAttribute("gradeList", gradeList);
        req.setAttribute("template", template);
        req.setAttribute("templateItems", template.getItemList());
        req.setAttribute("courseId", courseId);
        req.setAttribute("course", courseService.getCourseById(courseId));
        req.setAttribute("isSubmitted", isSubmitted);
        forward(req, resp, "teacher/teacher_grade_list.jsp");
    }

    // ===================== 保存成绩 =====================

    public void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/teacher/course?method=list", "缺少课程参数", "danger");
            return;
        }
        String backTo = "/teacher/grade?method=list&courseId=" + courseId;

        String status = param(req, "status", "draft");
        if (!"submitted".equals(status) && !"draft".equals(status)) {
            status = "draft";
        }

        CourseGradeTemplate template = courseService.getTemplateByCourseId(courseId);
        if (template == null) {
            redirect(req, resp, backTo, "该课程没有成绩模板，无法保存", "danger");
            return;
        }

        String[] studentIds = req.getParameterValues("studentId");
        String[] dailyJsons = req.getParameterValues("dailyJson");
        String[] finalScores = req.getParameterValues("finalScore");

        if (studentIds == null || studentIds.length == 0) {
            redirect(req, resp, backTo, "没有可以保存的成绩数据", "warning");
            return;
        }

        List<StudentGrade> toSave = new ArrayList<>();
        int skipped = 0;
        for (int i = 0; i < studentIds.length; i++) {
            Integer studentId = parseIntOrNull(at(studentIds, i));
            if (studentId == null) {
                continue;
            }
            String finalStr = at(finalScores, i);
            if (finalStr == null || finalStr.trim().isEmpty()) {
                skipped++;
                continue;
            }
            Double finalScore = parseDoubleOrNull(finalStr);
            if (finalScore == null || finalScore < 0 || finalScore > 100) {
                redirect(req, resp, backTo, "期末成绩必须在 0~100 之间", "danger");
                return;
            }
            String dailyJson = at(dailyJsons, i);
            if (dailyJson == null || dailyJson.trim().isEmpty()) {
                dailyJson = "{}";
            }
            Map<String, Double> daily = parseDailyJson(dailyJson);

            StudentGrade sg = new StudentGrade();
            sg.setStudentId(studentId);
            sg.setDailyScoreJson(dailyJson);
            sg.setFinalExamScore(finalScore);
            sg.setTotalScore(computeTotal(template, daily, finalScore));
            sg.setGpa(computeGpa(sg.getTotalScore()));
            sg.setStatus(status);
            toSave.add(sg);
        }

        if (toSave.isEmpty()) {
            redirect(req, resp, backTo, "没有填写任何期末成绩，未保存", "warning");
            return;
        }

        if (gradeService.saveGrades(toSave, courseId, status)) {
            String word = "submitted".equals(status) ? "已提交" : "已保存为草稿";
            String extra = skipped > 0 ? "（有 " + skipped + " 名学生因为没填期末成绩被跳过）" : "";
            redirect(req, resp, backTo, "成绩" + word + "，共 " + toSave.size() + " 条" + extra, "success");
        } else {
            redirect(req, resp, backTo, "保存失败，请稍后重试", "danger");
        }
    }

    // ===================== 统计 =====================

    public void stats(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/teacher/course?method=list", "请先选择课程", "warning");
            return;
        }
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            redirect(req, resp, "/teacher/course?method=list", "课程不存在", "danger");
            return;
        }
        GradeStats stats = gradeService.getCourseStats(courseId);
        req.setAttribute("course", course);
        req.setAttribute("stats", stats);
        req.setAttribute("template", courseService.getTemplateByCourseId(courseId));
        forward(req, resp, "teacher/teacher_grade_stats.jsp");
    }

    // ===================== 导出 Excel =====================

    public void export(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/teacher/course?method=list", "请先选择课程", "warning");
            return;
        }
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            redirect(req, resp, "/teacher/course?method=list", "课程不存在", "danger");
            return;
        }
        CourseGradeTemplate template = courseService.getTemplateByCourseId(courseId);
        List<StudentGrade> gradeList = gradeService.getStudentsByCourse(courseId);
        List<TemplateItem> items = (template == null) ? new ArrayList<TemplateItem>() : template.getItemList();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩单");

            List<String> headers = new ArrayList<>();
            headers.add("学号");
            headers.add("姓名");
            for (TemplateItem item : items) {
                headers.add(item.getItemName() + "(" + item.getItemRatio() + "%)");
            }
            headers.add("期末成绩");
            headers.add("总评成绩");
            headers.add("绩点");
            headers.add("状态");

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            if (gradeList != null) {
                for (StudentGrade sg : gradeList) {
                    Row row = sheet.createRow(rowNum++);
                    int col = 0;
                    row.createCell(col++).setCellValue(nullSafe(sg.getStudentNumber()));
                    row.createCell(col++).setCellValue(nullSafe(sg.getStudentName()));

                    Map<String, Double> daily = parseDailyJson(sg.getDailyScoreJson());
                    for (TemplateItem item : items) {
                        Double v = daily.get(item.getItemName());
                        if (v == null) {
                            row.createCell(col++).setCellValue("");
                        } else {
                            row.createCell(col++).setCellValue(v);
                        }
                    }
                    row.createCell(col++).setCellValue(sg.getFinalExamScore() == null ? 0 : sg.getFinalExamScore());
                    row.createCell(col++).setCellValue(sg.getTotalScore() == null ? 0 : sg.getTotalScore());
                    row.createCell(col++).setCellValue(sg.getGpa() == null ? 0 : sg.getGpa());
                    row.createCell(col).setCellValue(statusText(sg.getStatus()));
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.setColumnWidth(i, 16 * 256);
            }
            sheet.setColumnWidth(1, 20 * 256);

            String fileName = URLEncoder.encode(course.getCourseName() + "_成绩单.xlsx", "UTF-8");
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            try (OutputStream out = resp.getOutputStream()) {
                workbook.write(out);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!resp.isCommitted()) {
                resp.sendError(500, "导出成绩失败：" + e.getMessage());
            }
        }
    }

    // ===================== 批量导入 =====================

    /** 下载导入模板：表头按照该课程的成绩模板生成，并带上当前选课学生的学号姓名 */
    public void template(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/teacher/course?method=list", "请先选择课程", "warning");
            return;
        }
        Course course = courseService.getCourseById(courseId);
        CourseGradeTemplate template = courseService.getTemplateByCourseId(courseId);
        if (course == null || template == null) {
            redirect(req, resp, "/teacher/grade?method=list&courseId=" + courseId, "课程或成绩模板不存在", "danger");
            return;
        }

        List<TemplateItem> items = template.getItemList();
        List<StudentGrade> gradeList = gradeService.getStudentsByCourse(courseId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩导入");

            List<String> headers = new ArrayList<>();
            headers.add("学号");
            headers.add("姓名");
            for (TemplateItem item : items) {
                headers.add(item.getItemName());
            }
            headers.add("期末成绩");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            if (gradeList != null) {
                for (StudentGrade sg : gradeList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(nullSafe(sg.getStudentNumber()));
                    row.createCell(1).setCellValue(nullSafe(sg.getStudentName()));
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.setColumnWidth(i, 14 * 256);
            }
            sheet.setColumnWidth(1, 18 * 256);

            String fileName = URLEncoder.encode(course.getCourseName() + "_成绩导入模板.xlsx", "UTF-8");
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            try (OutputStream out = resp.getOutputStream()) {
                workbook.write(out);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!resp.isCommitted()) {
                resp.sendError(500, "生成导入模板失败：" + e.getMessage());
            }
        }
    }

    /** 接收上传的 Excel，按学号匹配学生后写入成绩（默认保存为草稿，方便核对后再提交） */
    public void importGrades(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/teacher/course?method=list", "缺少课程参数", "danger");
            return;
        }
        String backTo = "/teacher/grade?method=list&courseId=" + courseId;

        CourseGradeTemplate template = courseService.getTemplateByCourseId(courseId);
        if (template == null) {
            redirect(req, resp, backTo, "该课程没有成绩模板，无法导入", "danger");
            return;
        }

        Part part;
        try {
            part = req.getPart("file");
        } catch (IllegalStateException e) {
            redirect(req, resp, backTo, "文件太大，请控制在 5MB 以内", "danger");
            return;
        }
        if (part == null || part.getSize() <= 0) {
            redirect(req, resp, backTo, "请选择要导入的 Excel 文件", "warning");
            return;
        }

        List<StudentGrade> gradeList = gradeService.getStudentsByCourse(courseId);
        Map<String, StudentGrade> byNumber = new HashMap<>();
        if (gradeList != null) {
            for (StudentGrade sg : gradeList) {
                if (sg.getStudentNumber() != null) {
                    byNumber.put(sg.getStudentNumber().trim(), sg);
                }
            }
        }

        List<TemplateItem> items = template.getItemList();
        List<StudentGrade> toSave = new ArrayList<>();
        List<String> unknownNumbers = new ArrayList<>();
        int badRows = 0;

        DataFormatter formatter = new DataFormatter();
        try (InputStream in = part.getInputStream();
             Workbook workbook = WorkbookFactory.create(in)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                redirect(req, resp, backTo, "Excel 是空的", "danger");
                return;
            }

            // 用表头定位列，避免用户移动列顺序就失效
            Map<String, Integer> colIndex = new LinkedHashMap<>();
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                String name = formatter.formatCellValue(headerRow.getCell(c)).trim();
                if (!name.isEmpty()) {
                    colIndex.put(name, c);
                }
            }
            Integer numberCol = colIndex.get("学号");
            Integer finalCol = colIndex.get("期末成绩");
            if (numberCol == null || finalCol == null) {
                redirect(req, resp, backTo, "表头缺少「学号」或「期末成绩」列，请用导入模板填写", "danger");
                return;
            }

            for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                String number = formatter.formatCellValue(row.getCell(numberCol)).trim();
                if (number.isEmpty()) {
                    continue;
                }
                String finalStr = formatter.formatCellValue(row.getCell(finalCol)).trim();
                if (finalStr.isEmpty()) {
                    badRows++;
                    continue;
                }

                StudentGrade target = byNumber.get(number);
                if (target == null) {
                    unknownNumbers.add(number);
                    continue;
                }

                Double finalScore = parseDoubleOrNull(finalStr);
                if (finalScore == null || finalScore < 0 || finalScore > 100) {
                    badRows++;
                    continue;
                }

                Map<String, Double> daily = new LinkedHashMap<>();
                for (TemplateItem item : items) {
                    Integer col = colIndex.get(item.getItemName());
                    if (col == null) {
                        continue;
                    }
                    Double v = parseDoubleOrNull(formatter.formatCellValue(row.getCell(col)).trim());
                    if (v == null) {
                        continue;
                    }
                    if (v < 0 || v > 100) {
                        daily.put(item.getItemName(), 0.0);
                    } else {
                        daily.put(item.getItemName(), v);
                    }
                }

                StudentGrade sg = new StudentGrade();
                sg.setStudentId(target.getStudentId());
                sg.setDailyScoreJson(toJson(daily));
                sg.setFinalExamScore(finalScore);
                sg.setTotalScore(computeTotal(template, daily, finalScore));
                sg.setGpa(computeGpa(sg.getTotalScore()));
                sg.setStatus("draft");
                toSave.add(sg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirect(req, resp, backTo, "解析 Excel 失败：" + e.getMessage(), "danger");
            return;
        }

        if (toSave.isEmpty()) {
            String msg = "没有可导入的数据";
            if (!unknownNumbers.isEmpty()) {
                msg += "，学号不在本课程名单里：" + joinLimited(unknownNumbers);
            }
            redirect(req, resp, backTo, msg, "warning");
            return;
        }

        if (!gradeService.saveGrades(toSave, courseId, "draft")) {
            redirect(req, resp, backTo, "导入失败，写入数据库时出错", "danger");
            return;
        }

        StringBuilder msg = new StringBuilder("成功导入 " + toSave.size() + " 条成绩（草稿），请核对后再提交");
        if (badRows > 0) {
            msg.append("；跳过 ").append(badRows).append(" 行无效数据");
        }
        if (!unknownNumbers.isEmpty()) {
            msg.append("；学号不在名单里：").append(joinLimited(unknownNumbers));
        }
        redirect(req, resp, backTo, msg.toString(), "success");
    }

    // ===================== 计算辅助 =====================

    /** 总评 = Σ(平时分项分数 × 分项比例%) + 期末成绩 × 期末比例% */
    static double computeTotal(CourseGradeTemplate template, Map<String, Double> daily, double finalScore) {
        double total = 0;
        if (template != null && template.getItemList() != null) {
            for (TemplateItem item : template.getItemList()) {
                Double v = daily.get(item.getItemName());
                if (v != null) {
                    total += v * item.getItemRatio() / 100.0;
                }
            }
            total += finalScore * template.getFinalRatio() / 100.0;
        } else {
            total = finalScore;
        }
        return round2(total);
    }

    /** 绩点 = (总评 - 50) / 10，最低 0 */
    static double computeGpa(Double totalScore) {
        if (totalScore == null) {
            return 0;
        }
        double gpa = (totalScore - 50) / 10.0;
        return round2(Math.max(0, gpa));
    }

    static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    /** 解析 {"考勤":18,"作业":34.5} 这种扁平 JSON 对象，容错处理非法内容 */
    static Map<String, Double> parseDailyJson(String json) {
        Map<String, Double> map = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) {
            return map;
        }
        Matcher m = JSON_PAIR.matcher(json);
        while (m.find()) {
            try {
                map.put(m.group(1), Double.valueOf(m.group(2)));
            } catch (NumberFormatException ignore) {
                // 忽略坏数据
            }
        }
        return map;
    }

    static String toJson(Map<String, Double> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Double> e : map.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            sb.append('"').append(e.getKey().replace("\"", "\\\"")).append("\":")
              .append(e.getValue());
            first = false;
        }
        return sb.append('}').toString();
    }

    private static String statusText(String status) {
        if ("draft".equals(status)) return "草稿";
        if ("submitted".equals(status)) return "已提交";
        return nullSafe(status);
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static String at(String[] arr, int i) {
        return (arr == null || i < 0 || i >= arr.length) ? null : arr[i];
    }

    private static Integer parseIntOrNull(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double parseDoubleOrNull(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? null : Double.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String joinLimited(List<String> list) {
        int max = 8;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size() && i < max; i++) {
            if (i > 0) {
                sb.append("、");
            }
            sb.append(list.get(i));
        }
        if (list.size() > max) {
            sb.append(" 等 ").append(list.size()).append(" 个");
        }
        return sb.toString();
    }
}