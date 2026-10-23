package com.lut.grade.controller;

import com.lut.grade.dao.TeacherDao;
import com.lut.grade.model.Course;
import com.lut.grade.model.Teacher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 教师端 - 我的课程 + 工作台。
 */
@WebServlet("/teacher/course")
public class TeacherCourseServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final TeacherDao teacherDao = new TeacherDao();
    private final com.lut.grade.service.CourseService courseService =
            new com.lut.grade.service.CourseService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Teacher teacher = currentTeacher(req);
        if (teacher == null) {
            req.setAttribute("msg", "您的教师档案不存在，请联系管理员在「账号管理」里补全");
            req.setAttribute("msgType", "warning");
            forward(req, resp, "teacher/index.jsp");
            return;
        }
        List<Course> courses = courseService.getCoursesByTeacherId(teacher.getId());
        req.setAttribute("courseList", courses);
        req.setAttribute("teacher", teacher);
        forward(req, resp, "teacher/teacher_course_list.jsp");
    }

    public void dashboard(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Teacher teacher = currentTeacher(req);
        if (teacher != null) {
            req.setAttribute("courseCount", courseService.countCoursesByTeacherId(teacher.getId()));
            req.setAttribute("studentCount", courseService.countStudentsByTeacherId(teacher.getId()));
            req.setAttribute("pendingCount", courseService.countPendingGradesByTeacherId(teacher.getId()));
            req.setAttribute("taskList", courseService.getPendingTasksByTeacherId(teacher.getId()));
            req.setAttribute("teacher", teacher);
        } else {
            req.setAttribute("msg", "您的教师档案不存在，请联系管理员在「账号管理」里补全");
            req.setAttribute("msgType", "warning");
        }
        forward(req, resp, "teacher/index.jsp");
    }

    private Teacher currentTeacher(HttpServletRequest req) {
        com.lut.grade.model.User user = currentUser(req);
        return user == null ? null : teacherDao.findByUserId(user.getId());
    }
}