package com.lut.grade.controller;

import com.lut.grade.dao.CourseSelectionDao;
import com.lut.grade.dao.TeacherDao;
import com.lut.grade.model.Course;
import com.lut.grade.model.StudentGrade;
import com.lut.grade.model.Teacher;
import com.lut.grade.model.User;
import com.lut.grade.service.CourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 教师端 - 选课名单管理。
 * 之前学生能选课、能退课，但教师完全看不到自己课上有谁，这里补上。
 */
@WebServlet("/teacher/roster")
public class TeacherRosterServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final CourseSelectionDao selectionDao = new CourseSelectionDao();
    private final TeacherDao teacherDao = new TeacherDao();
    private final CourseService courseService = new CourseService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Teacher teacher = currentTeacher(req);
        if (teacher == null) {
            redirect(req, resp, "/views/login.jsp", "教师档案不存在，请重新登录", "danger");
            return;
        }

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
        // 只能看自己的课
        if (course.getTeacherId() == null || !course.getTeacherId().equals(teacher.getId())) {
            redirect(req, resp, "/teacher/course?method=list", "这不是您负责的课程", "danger");
            return;
        }

        List<StudentGrade> rosterList = selectionDao.findRosterByCourseId(courseId);
        req.setAttribute("course", course);
        req.setAttribute("courseId", courseId);
        req.setAttribute("rosterList", rosterList);
        forward(req, resp, "teacher/teacher_roster_list.jsp");
    }

    public void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Teacher teacher = currentTeacher(req);
        Integer courseId = paramInt(req, "courseId");
        Integer studentId = paramInt(req, "studentId");
        if (teacher == null || courseId == null || studentId == null) {
            redirect(req, resp, "/teacher/course?method=list", "参数不完整", "danger");
            return;
        }

        Course course = courseService.getCourseById(courseId);
        if (course == null || course.getTeacherId() == null
                || !course.getTeacherId().equals(teacher.getId())) {
            redirect(req, resp, "/teacher/course?method=list", "这不是您负责的课程", "danger");
            return;
        }

        String backTo = "/teacher/roster?method=list&courseId=" + courseId;
        if (selectionDao.deleteSelection(studentId, courseId)) {
            redirect(req, resp, backTo, "已将该学生移出本课程", "success");
        } else {
            redirect(req, resp, backTo, "移除失败，可能该学生已经不在名单里", "warning");
        }
    }

    private Teacher currentTeacher(HttpServletRequest req) {
        User user = currentUser(req);
        if (user == null) {
            return null;
        }
        return teacherDao.findByUserId(user.getId());
    }
}