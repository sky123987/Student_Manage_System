package com.lut.grade.controller;

import com.lut.grade.model.Course;
import com.lut.grade.service.CourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理员端 - 课程管理。表单里的数字字段都做了容错，填错不会 500。
 */
@WebServlet("/admin/course")
public class AdminCourseServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final CourseService courseService = new CourseService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("courseList", courseService.getAllCoursesWithTeacher());
        forward(req, resp, "admin/admin_course_list.jsp");
    }

    public void toAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("teacherList", courseService.getAllTeachers());
        forward(req, resp, "admin/admin_course_add.jsp");
    }

    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String error = fillCourse(req, new Course(), false);
        if (error != null) {
            redirect(req, resp, "/admin/course?method=toAdd", error, "danger");
            return;
        }
        Course c = new Course();
        fillCourse(req, c, false);
        if (courseService.addCourse(c)) {
            redirect(req, resp, "/admin/course?method=list", "课程「" + c.getCourseName() + "」添加成功", "success");
        } else {
            redirect(req, resp, "/admin/course?method=toAdd", "添加失败，请稍后重试", "danger");
        }
    }

    public void toEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = paramInt(req, "id");
        Course course = (id == null) ? null : courseService.getCourseById(id);
        if (course == null) {
            redirect(req, resp, "/admin/course?method=list", "课程不存在", "danger");
            return;
        }
        req.setAttribute("course", course);
        req.setAttribute("teacherList", courseService.getAllTeachers());
        forward(req, resp, "admin/admin_course_edit.jsp");
    }

    public void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = paramInt(req, "id");
        Course c = (id == null) ? null : courseService.getCourseById(id);
        if (c == null) {
            redirect(req, resp, "/admin/course?method=list", "课程不存在", "danger");
            return;
        }
        String error = fillCourse(req, c, true);
        if (error != null) {
            redirect(req, resp, "/admin/course?method=toEdit&id=" + id, error, "danger");
            return;
        }
        if (courseService.updateCourse(c)) {
            redirect(req, resp, "/admin/course?method=list", "课程信息已更新", "success");
        } else {
            redirect(req, resp, "/admin/course?method=toEdit&id=" + id, "更新失败，请稍后重试", "danger");
        }
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = paramInt(req, "id");
        if (id == null) {
            redirect(req, resp, "/admin/course?method=list", "缺少课程 id", "danger");
            return;
        }
        if (courseService.deleteCourse(id)) {
            redirect(req, resp, "/admin/course?method=list", "课程已删除", "success");
        } else {
            redirect(req, resp, "/admin/course?method=list", "删除失败，可能有成绩或选课记录仍在使用该课程", "danger");
        }
    }

    /** @return null 表示校验通过，否则是错误提示 */
    private String fillCourse(HttpServletRequest req, Course c, boolean withId) {
        String name = param(req, "courseName");
        if (name == null || name.isEmpty()) {
            return "课程名称不能为空";
        }
        Double credit = paramDouble(req, "credit");
        if (credit == null || credit <= 0) {
            return "学分必须是大于 0 的数字";
        }
        Integer hours = paramInt(req, "courseHours");
        if (withId) {
            c.setId(paramInt(req, "id"));
        }
        c.setCourseName(name);
        c.setCredit(credit);
        c.setTeacherId(paramInt(req, "teacherId"));
        c.setCourseHours(hours == null ? 0 : hours);
        c.setCourseType(param(req, "courseType", "必修"));
        return null;
    }
}