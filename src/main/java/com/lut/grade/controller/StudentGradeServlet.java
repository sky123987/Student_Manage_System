package com.lut.grade.controller;

import com.lut.grade.dao.StudentDao;
import com.lut.grade.model.GradeDetail;
import com.lut.grade.model.MakeupApply;
import com.lut.grade.model.Student;
import com.lut.grade.model.User;
import com.lut.grade.service.GradeService;
import com.lut.grade.service.MakeupApplyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.lut.grade.util.ExcelWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 学生端 - 我的成绩（含补考状态） */
@WebServlet("/student/grade")
public class StudentGradeServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final GradeService gradeService = new GradeService();
    private final StudentDao studentDao = new StudentDao();
    private final MakeupApplyService makeupApplyService = new MakeupApplyService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) {
            redirect(req, resp, "/views/login.jsp", "请先登录", "warning");
            return;
        }

        Student student = studentDao.findByUserId(user.getId());
        if (student == null) {
            req.setAttribute("msg", "你的学生档案不存在，请联系管理员补充后再使用学生功能");
            req.setAttribute("msgType", "danger");
            req.setAttribute("noProfile", Boolean.TRUE);
            forward(req, resp, "student/index.jsp");
            return;
        }

        List<GradeDetail> gradeList = gradeService.getStudentGradesWithStats(student.getId());

        // 把每门课最新的补考申请状态合并进成绩行，页面上就能直接显示「补考中 / 已通过」等
        List<MakeupApply> applyList = makeupApplyService.findAppliesByStudentId(student.getId());
        Map<Integer, MakeupApply> applyMap = new HashMap<>();
        if (applyList != null) {
            for (MakeupApply apply : applyList) {
                MakeupApply old = applyMap.get(apply.getCourseId());
                if (old == null || (apply.getApplyTime() != null && old.getApplyTime() != null
                        && apply.getApplyTime().after(old.getApplyTime()))) {
                    applyMap.put(apply.getCourseId(), apply);
                }
            }
        }
        if (gradeList != null) {
            for (GradeDetail gd : gradeList) {
                MakeupApply apply = applyMap.get(gd.getCourseId());
                if (apply != null) {
                    gd.setMakeupStatus(apply.getStatus());
                    gd.setMakeupScore(apply.getMakeupScore());
                }
            }
        }

        Map<String, Object> stats = gradeService.calculateStudentStats(gradeList);
        req.setAttribute("student", student);
        req.setAttribute("gradeList", gradeList);
        req.setAttribute("stats", stats);
        forward(req, resp, "student/student_grade_list.jsp");
    }

    /** 导出我的成绩单（Excel）。Word 要求里的“成绩单”报表。 */
    public void export(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = currentUser(req);
        if (user == null) {
            redirect(req, resp, "/views/login.jsp", "请先登录", "warning");
            return;
        }
        Student student = studentDao.findByUserId(user.getId());
        if (student == null) {
            redirect(req, resp, "/student/dashboard?method=index", "你的学生档案不存在，无法导出成绩单", "danger");
            return;
        }

        List<GradeDetail> gradeList = gradeService.getStudentGradesWithStats(student.getId());
        Map<String, Object> stats = gradeService.calculateStudentStats(gradeList);

        List<String[]> rows = new ArrayList<>();
        int index = 1;
        if (gradeList != null) {
            for (GradeDetail g : gradeList) {
                rows.add(new String[]{
                        String.valueOf(index++),
                        ExcelWriter.nvl(g.getCourseName()),
                        ExcelWriter.nvl(g.getCredit()),
                        ExcelWriter.nvl(g.getTotalScore()),
                        ExcelWriter.nvl(g.getGpa()),
                        ExcelWriter.nvl(g.getGradeLevel())
                });
            }
        }
        // 末尾追加一行汇总，和页面上的“平均分/总学分/平均绩点”保持一致
        rows.add(new String[]{
                "合计", "平均分 " + ExcelWriter.nvl(stats.get("avgScore")),
                "总学分 " + ExcelWriter.nvl(stats.get("totalCredit")),
                "平均绩点 " + ExcelWriter.nvl(stats.get("avgGpa")), "", "", ""
        });

        String[] headers = {"序号", "课程", "学分", "总评成绩", "绩点", "等级"};
        ExcelWriter.write(resp, student.getStudentNumber() + "_" + student.getName() + "_成绩单",
                "成绩单", student.getName() + "（" + student.getStudentNumber() + "）成绩单",
                headers, rows);
    }
}