package com.lut.grade.controller;

import com.lut.grade.dao.TeacherDao;
import com.lut.grade.model.GradeModifyApply;
import com.lut.grade.model.Teacher;
import com.lut.grade.model.User;
import com.lut.grade.service.GradeModifyApplyService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 教师端 - 提交成绩修改申请（成绩提交后被锁定，改分要走审批）。
 */
@WebServlet("/teacher/apply")
public class TeacherApplyServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final GradeModifyApplyService applyService = new GradeModifyApplyService();
    private final TeacherDao teacherDao = new TeacherDao();

    public void submit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/teacher/course?method=list", "缺少课程参数", "danger");
            return;
        }
        String backTo = "/teacher/grade?method=list&courseId=" + courseId;

        User user = currentUser(req);
        Teacher teacher = (user == null) ? null : teacherDao.findByUserId(user.getId());
        if (teacher == null) {
            redirect(req, resp, backTo, "您的教师档案不存在，无法提交申请", "danger");
            return;
        }

        String reason = param(req, "reason");
        if (reason == null || reason.isEmpty()) {
            redirect(req, resp, backTo, "请填写申请理由", "warning");
            return;
        }

        GradeModifyApply apply = new GradeModifyApply();
        apply.setCourseId(courseId);
        apply.setTeacherId(teacher.getId());
        apply.setReason(reason);

        if (applyService.submitApply(apply)) {
            redirect(req, resp, backTo, "修改申请已提交，等待管理员审批", "success");
        } else {
            redirect(req, resp, backTo, "提交失败，请稍后重试", "danger");
        }
    }
}