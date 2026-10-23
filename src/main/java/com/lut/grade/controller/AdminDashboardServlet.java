package com.lut.grade.controller;

import com.lut.grade.dao.CourseDao;
import com.lut.grade.dao.StudentDao;
import com.lut.grade.dao.TeacherDao;
import com.lut.grade.dao.UserDao;
import com.lut.grade.service.ClassService;
import com.lut.grade.service.GradeModifyApplyService;
import com.lut.grade.service.MakeupApplyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理员端 - 系统概览。除了人数和课程数，也把待办的审批数量带到首页。
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StudentDao studentDao = new StudentDao();
    private final TeacherDao teacherDao = new TeacherDao();
    private final CourseDao courseDao = new CourseDao();
    private final UserDao userDao = new UserDao();
    private final ClassService classService = new ClassService();
    private final GradeModifyApplyService applyService = new GradeModifyApplyService();
    private final MakeupApplyService makeupApplyService = new MakeupApplyService();

    public void index(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("studentCount", studentDao.countAll());
        req.setAttribute("teacherCount", teacherDao.countAll());
        req.setAttribute("courseCount", courseDao.countAll());
        req.setAttribute("classCount", classService.getAllClasses().size());
        req.setAttribute("userCount", userDao.countAll());

        int approvalCount = 0;
        int makeupCount = 0;
        try {
            approvalCount = applyService.getPendingApplies().size();
        } catch (Exception ignore) { /* 统计失败不影响首页 */ }
        try {
            makeupCount = makeupApplyService.getPendingApplies().size();
        } catch (Exception ignore) { /* 统计失败不影响首页 */ }
        req.setAttribute("approvalCount", approvalCount);
        req.setAttribute("makeupCount", makeupCount);

        forward(req, resp, "admin/index.jsp");
    }
}