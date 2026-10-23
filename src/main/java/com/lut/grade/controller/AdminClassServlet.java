package com.lut.grade.controller;

import com.lut.grade.model.SchoolClass;
import com.lut.grade.service.ClassService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理员端 - 班级管理。
 */
@WebServlet("/admin/class")
public class AdminClassServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ClassService classService = new ClassService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("classList", classService.getAllClasses());
        forward(req, resp, "admin/admin_class_list.jsp");
    }

    public void toAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp, "admin/admin_class_add.jsp");
    }

    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = param(req, "className");
        if (name == null || name.isEmpty()) {
            redirect(req, resp, "/admin/class?method=toAdd", "班级名称不能为空", "danger");
            return;
        }
        SchoolClass c = new SchoolClass();
        c.setClassName(name);
        c.setGradeYear(param(req, "gradeYear"));
        if (classService.addClass(c)) {
            redirect(req, resp, "/admin/class?method=list", "班级「" + name + "」添加成功", "success");
        } else {
            redirect(req, resp, "/admin/class?method=toAdd", "添加失败，请稍后重试", "danger");
        }
    }

    public void toEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = paramInt(req, "id");
        SchoolClass sc = (id == null) ? null : classService.getClassById(id);
        if (sc == null) {
            redirect(req, resp, "/admin/class?method=list", "班级不存在", "danger");
            return;
        }
        req.setAttribute("schoolClass", sc);
        forward(req, resp, "admin/admin_class_edit.jsp");
    }

    public void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = paramInt(req, "id");
        if (id == null) {
            redirect(req, resp, "/admin/class?method=list", "缺少班级 id", "danger");
            return;
        }
        SchoolClass c = new SchoolClass();
        c.setId(id);
        c.setClassName(param(req, "className"));
        c.setGradeYear(param(req, "gradeYear"));
        if (classService.updateClass(c)) {
            redirect(req, resp, "/admin/class?method=list", "班级信息已更新", "success");
        } else {
            redirect(req, resp, "/admin/class?method=toEdit&id=" + id, "更新失败", "danger");
        }
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = paramInt(req, "id");
        if (id == null) {
            redirect(req, resp, "/admin/class?method=list", "缺少班级 id", "danger");
            return;
        }
        if (classService.deleteClass(id)) {
            redirect(req, resp, "/admin/class?method=list", "班级已删除，原班级学生变为未分班", "success");
        } else {
            redirect(req, resp, "/admin/class?method=list", "删除失败", "danger");
        }
    }
}