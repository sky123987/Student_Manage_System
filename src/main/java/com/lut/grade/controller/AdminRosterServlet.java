package com.lut.grade.controller;

import com.lut.grade.dao.CourseSelectionDao;
import com.lut.grade.model.Course;
import com.lut.grade.model.StudentGrade;
import com.lut.grade.service.CourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 管理端 - 选课名单管理。
 * Word 要求里写的是“学生选课，教师/管理员管理名单”，教师端已经有了，
 * 这里补管理员这一侧：可以按课程查看全部选课学生，并把某个学生移出课程。
 */
@WebServlet("/admin/roster")
public class AdminRosterServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final CourseService courseService = new CourseService();
    private final CourseSelectionDao selectionDao = new CourseSelectionDao();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Course> courseList = courseService.getAllCoursesWithTeacher();
        req.setAttribute("courseList", courseList);

        Integer courseId = paramInt(req, "courseId");
        if (courseId != null) {
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                redirect(req, resp, "/admin/roster?method=list", "课程不存在或已被删除", "danger");
                return;
            }
            List<StudentGrade> rosterList = selectionDao.findRosterByCourseId(courseId);
            req.setAttribute("course", course);
            req.setAttribute("courseId", courseId);
            req.setAttribute("rosterList", rosterList);
            req.setAttribute("rosterCount", rosterList == null ? 0 : rosterList.size());
        }
        forward(req, resp, "admin/admin_roster_list.jsp");
    }

    public void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer courseId = paramInt(req, "courseId");
        Integer studentId = paramInt(req, "studentId");
        if (courseId == null || studentId == null) {
            redirect(req, resp, "/admin/roster?method=list", "参数不完整，无法移除", "danger");
            return;
        }
        String backTo = "/admin/roster?method=list&courseId=" + courseId;
        if (selectionDao.deleteSelection(studentId, courseId)) {
            redirect(req, resp, backTo, "已把该学生移出本课程", "success");
        } else {
            redirect(req, resp, backTo, "移除失败，该学生可能已不在名单里", "warning");
        }
    }
}