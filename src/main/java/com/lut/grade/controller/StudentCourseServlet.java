package com.lut.grade.controller;

import com.lut.grade.dao.StudentDao;
import com.lut.grade.model.Course;
import com.lut.grade.model.Student;
import com.lut.grade.model.User;
import com.lut.grade.service.CourseSelectionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** 学生端 - 选课 / 退课 / 我的课表 */
@WebServlet("/student/course")
public class StudentCourseServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final CourseSelectionService csService = new CourseSelectionService();
    private final StudentDao studentDao = new StudentDao();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer studentId = currentStudentId(req);
        if (studentId == null) {
            noProfile(req, resp);
            return;
        }
        List<Course> courseList = csService.getAvailableCourses(studentId);
        req.setAttribute("courseList", courseList);
        forward(req, resp, "student/student_course_list.jsp");
    }

    public void select(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer studentId = currentStudentId(req);
        if (studentId == null) {
            noProfile(req, resp);
            return;
        }
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/student/course?method=list", "缺少课程参数", "danger");
            return;
        }
        if (csService.selectCourse(studentId, courseId)) {
            redirect(req, resp, "/student/course?method=list", "选课成功", "success");
        } else {
            redirect(req, resp, "/student/course?method=list", "选课失败，可能已经选过这门课了", "warning");
        }
    }

    public void myCourses(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer studentId = currentStudentId(req);
        if (studentId == null) {
            noProfile(req, resp);
            return;
        }
        req.setAttribute("myCourseList", csService.getMyCourses(studentId));
        forward(req, resp, "student/student_my_courses.jsp");
    }

    public void drop(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer studentId = currentStudentId(req);
        if (studentId == null) {
            noProfile(req, resp);
            return;
        }
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/student/course?method=myCourses", "缺少课程参数", "danger");
            return;
        }
        if (csService.dropCourse(studentId, courseId)) {
            redirect(req, resp, "/student/course?method=myCourses", "退课成功", "success");
        } else {
            redirect(req, resp, "/student/course?method=myCourses", "退课失败", "danger");
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