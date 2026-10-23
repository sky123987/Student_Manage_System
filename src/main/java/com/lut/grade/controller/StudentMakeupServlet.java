package com.lut.grade.controller;

import com.lut.grade.dao.StudentDao;
import com.lut.grade.model.MakeupApply;
import com.lut.grade.model.Student;
import com.lut.grade.model.User;
import com.lut.grade.service.MakeupApplyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** 学生端 - 补考申请 */
@WebServlet("/student/makeup")
public class StudentMakeupServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final MakeupApplyService makeupApplyService = new MakeupApplyService();
    private final StudentDao studentDao = new StudentDao();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer studentId = currentStudentId(req);
        if (studentId == null) {
            noProfile(req, resp);
            return;
        }
        List<MakeupApply> applyList = makeupApplyService.findAppliesByStudentId(studentId);
        req.setAttribute("applyList", applyList);
        forward(req, resp, "student/student_makeup_list.jsp");
    }

    public void apply(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer studentId = currentStudentId(req);
        if (studentId == null) {
            noProfile(req, resp);
            return;
        }
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/student/makeup?method=list", "缺少课程参数", "danger");
            return;
        }

        String backTo = "/student/makeup?method=list";

        // 同一门课已经有「待审批」或「已通过」的申请就不允许重复提交
        List<MakeupApply> existing = makeupApplyService.findAppliesByStudentId(studentId);
        if (existing != null) {
            for (MakeupApply a : existing) {
                if (courseId.equals(a.getCourseId())
                        && ("pending".equals(a.getStatus()) || "approved".equals(a.getStatus()))) {
                    redirect(req, resp, backTo, "这门课已经提交过补考申请了，请等待审批", "warning");
                    return;
                }
            }
        }

        if (makeupApplyService.submitApply(studentId, courseId)) {
            redirect(req, resp, backTo, "补考申请已提交，请等待管理员审批", "success");
        } else {
            redirect(req, resp, backTo, "补考申请提交失败，请稍后重试", "danger");
        }
    }

    private Integer currentStudentId(HttpServletRequest req) {
        User user = currentUser(req);
        if (user == null) {
            return null;
        }
        Student student = studentDao.findByUserId(user.getId());
        return (student == null) ? null : student.getId();
    }

    private void noProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        redirect(req, resp, "/student/dashboard?method=index",
                "你的学生档案不存在，请联系管理员补充后再使用学生功能", "danger");
    }
}