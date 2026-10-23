package com.lut.grade.controller;

import com.lut.grade.dao.AdminGradeDao;
import com.lut.grade.model.GradeDetail;
import com.lut.grade.model.SchoolClass;
import com.lut.grade.service.ClassService;
import com.lut.grade.util.ExcelWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 管理员端 - 成绩统计与报表导出
 * （班级维度统计 + 班级内学生排名 + 导出 Excel 报表）。
 */
@WebServlet("/admin/grade")
public class AdminGradeServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final AdminGradeDao adminGradeDao = new AdminGradeDao();
    private final ClassService classService = new ClassService();

    public void classStats(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Map<String, Object>> classStats = adminGradeDao.getClassStats();
        req.setAttribute("classStats", classStats);
        forward(req, resp, "admin/admin_grade_class.jsp");
    }

    public void studentRanks(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer classId = paramInt(req, "classId");
        if (classId == null) {
            redirect(req, resp, "/admin/grade?method=classStats", "请先选择一个班级", "warning");
            return;
        }
        List<GradeDetail> studentRanks = adminGradeDao.getStudentGradesByClassId(classId);
        req.setAttribute("studentRanks", studentRanks);
        req.setAttribute("classId", classId);
        req.setAttribute("classStats", adminGradeDao.getClassStats());
        SchoolClass sc = classService.getClassById(classId);
        req.setAttribute("schoolClass", sc);
        forward(req, resp, "admin/admin_grade_student.jsp");
    }

    /** 导出某个班级的全部成绩明细。 */
    public void exportClass(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Integer classId = paramInt(req, "classId");
        if (classId == null) {
            redirect(req, resp, "/admin/grade?method=classStats", "请先选择一个班级", "warning");
            return;
        }
        SchoolClass sc = classService.getClassById(classId);
        String className = sc == null ? ("班级" + classId) : sc.getClassName();

        List<GradeDetail> list = adminGradeDao.getStudentGradesByClassId(classId);
        List<String[]> rows = new ArrayList<>();
        int index = 1;
        if (list != null) {
            for (GradeDetail g : list) {
                rows.add(new String[]{
                        String.valueOf(index++),
                        ExcelWriter.nvl(g.getStudentNumber()),
                        ExcelWriter.nvl(g.getStudentName()),
                        ExcelWriter.nvl(g.getCourseName()),
                        ExcelWriter.nvl(g.getTotalScore()),
                        ExcelWriter.nvl(g.getGpa()),
                        level(g.getTotalScore())
                });
            }
        }
        String[] headers = {"序号", "学号", "姓名", "课程", "总评成绩", "绩点", "等级"};
        ExcelWriter.write(resp, className + "_成绩明细", "成绩明细",
                className + " 成绩报表", headers, rows);
    }

    /** 百分制转等级制（Word 5.1 的换算规则，与 GradeService 保持一致）。 */
    private static String level(Double score) {
        if (score == null) {
            return "—";
        }
        if (score >= 90) {
            return "优";
        }
        if (score >= 80) {
            return "良";
        }
        if (score >= 70) {
            return "中";
        }
        if (score >= 60) {
            return "及格";
        }
        return "不及格";
    }

    /** 导出全校班级统计汇总（平均分/平均绩点/人数）。 */
    public void exportAll(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<Map<String, Object>> stats = adminGradeDao.getClassStats();
        List<String[]> rows = new ArrayList<>();
        int index = 1;
        if (stats != null) {
            for (Map<String, Object> m : stats) {
                rows.add(new String[]{
                        String.valueOf(index++),
                        ExcelWriter.nvl(m.get("className")),
                        ExcelWriter.nvl(m.get("studentCount")),
                        ExcelWriter.nvl(m.get("avgScore")),
                        ExcelWriter.nvl(m.get("avgGpa"))
                });
            }
        }
        String[] headers = {"序号", "班级", "学生人数", "平均分", "平均绩点"};
        ExcelWriter.write(resp, "全校班级成绩统计", "班级统计",
                "全校班级成绩统计报表", headers, rows);
    }
}