package com.lut.grade.controller;

import com.lut.grade.dao.StudentDao;
import com.lut.grade.model.GradeDetail;
import com.lut.grade.model.Student;
import com.lut.grade.model.User;
import com.lut.grade.service.CourseSelectionService;
import com.lut.grade.service.GradeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** 学生端 - 首页工作台 */
@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final GradeService gradeService = new GradeService();
    private final StudentDao studentDao = new StudentDao();
    private final CourseSelectionService csService = new CourseSelectionService();

    public void index(HttpServletRequest req, HttpServletResponse resp)
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
        Map<String, Object> stats = gradeService.calculateStudentStats(gradeList);

        int courseCount = 0;
        List<com.lut.grade.model.Course> myCourses = csService.getMyCourses(student.getId());
        if (myCourses != null) {
            courseCount = myCourses.size();
        }

        req.setAttribute("student", student);
        req.setAttribute("gradeList", gradeList);
        req.setAttribute("stats", stats);
        req.setAttribute("courseCount", courseCount);
        forward(req, resp, "student/index.jsp");
    }
}