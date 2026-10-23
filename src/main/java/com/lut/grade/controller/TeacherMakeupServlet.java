package com.lut.grade.controller;

import com.lut.grade.dao.TeacherDao;
import com.lut.grade.model.Teacher;
import com.lut.grade.model.User;
import com.lut.grade.service.MakeupApplyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 教师端 - 补考成绩录入。
 */
@WebServlet("/teacher/makeup")
public class TeacherMakeupServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final MakeupApplyService makeupApplyService = new MakeupApplyService();
    private final TeacherDao teacherDao = new TeacherDao();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Teacher teacher = currentTeacher(req);
        if (teacher == null) {
            req.setAttribute("msg", "教师档案不存在，请联系管理员");
            req.setAttribute("msgType", "warning");
            forward(req, resp, "teacher/index.jsp");
            return;
        }
        req.setAttribute("makeupList", makeupApplyService.getApprovedAppliesByTeacherId(teacher.getId()));
        forward(req, resp, "teacher/teacher_makeup_list.jsp");
    }

    public void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String backTo = "/teacher/makeup?method=list";
        Integer applyId = paramInt(req, "applyId");
        Integer studentId = paramInt(req, "studentId");
        Integer courseId = paramInt(req, "courseId");
        Double makeupScore = paramDouble(req, "makeupScore");

        if (applyId == null || studentId == null || courseId == null || makeupScore == null) {
            redirect(req, resp, backTo, "补考成绩填写不完整", "danger");
            return;
        }
        if (makeupScore < 0 || makeupScore > 100) {
            redirect(req, resp, backTo, "补考成绩必须在 0~100 之间", "danger");
            return;
        }
        if (makeupApplyService.submitMakeupScore(applyId, studentId, courseId, makeupScore)) {
            redirect(req, resp, backTo, "补考成绩已录入，总评按补考成绩重新计算", "success");
        } else {
            redirect(req, resp, backTo, "录入失败，请稍后重试", "danger");
        }
    }

    private Teacher currentTeacher(HttpServletRequest req) {
        User user = currentUser(req);
        return user == null ? null : teacherDao.findByUserId(user.getId());
    }
}