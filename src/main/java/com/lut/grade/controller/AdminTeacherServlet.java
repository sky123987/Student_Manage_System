package com.lut.grade.controller;

import com.lut.grade.model.Teacher;
import com.lut.grade.service.AccountService;
import com.lut.grade.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 管理员 - 教师管理。新增/删除同样是"账号 + 档案"一起处理。
 */
@WebServlet("/admin/teacher")
public class AdminTeacherServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final TeacherService teacherService = new TeacherService();
    private final AccountService accountService = new AccountService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("teacherList", teacherService.getAllTeachers());
        forward(req, resp, "admin/admin_teacher_list.jsp");
    }

    public void toAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("defaultPassword", AccountService.DEFAULT_PASSWORD);
        forward(req, resp, "admin/admin_teacher_add.jsp");
    }

    public void add(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Teacher teacher = extractTeacher(req);
        String username = param(req, "username");
        String password = param(req, "password");

        if (isBlank(teacher.getTeacherNumber())) {
            backToForm(req, resp, "工号不能为空", teacher);
            return;
        }
        if (isBlank(teacher.getName())) {
            backToForm(req, resp, "姓名不能为空", teacher);
            return;
        }
        if (isBlank(username)) {
            username = teacher.getTeacherNumber();
        }

        String error = accountService.createTeacher(username, password, teacher);
        if (error != null) {
            backToForm(req, resp, error, teacher);
            return;
        }
        redirect(req, resp, "/admin/teacher?method=list", "教师「" + teacher.getName() + "」添加成功", "success");
    }

    public void toEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = paramInt(req, "id");
        Teacher teacher = (id == null) ? null : teacherService.getTeacherById(id);
        if (teacher == null) {
            redirect(req, resp, "/admin/teacher?method=list", "教师不存在", "danger");
            return;
        }
        req.setAttribute("teacher", teacher);
        forward(req, resp, "admin/admin_teacher_edit.jsp");
    }

    public void update(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = paramInt(req, "id");
        Teacher teacher = (id == null) ? null : teacherService.getTeacherById(id);
        if (teacher == null) {
            redirect(req, resp, "/admin/teacher?method=list", "教师不存在", "danger");
            return;
        }
        teacher.setTeacherNumber(param(req, "teacherNumber", teacher.getTeacherNumber()));
        teacher.setName(param(req, "name", teacher.getName()));
        teacher.setTitle(param(req, "title", teacher.getTitle()));

        if (teacherService.updateTeacher(teacher)) {
            redirect(req, resp, "/admin/teacher?method=list", "教师信息已更新", "success");
        } else {
            redirect(req, resp, "/admin/teacher?method=toEdit&id=" + id, "更新失败，请检查工号是否重复", "danger");
        }
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = paramInt(req, "id");
        Teacher teacher = (id == null) ? null : teacherService.getTeacherById(id);
        if (teacher == null) {
            redirect(req, resp, "/admin/teacher?method=list", "教师不存在", "danger");
            return;
        }
        if (teacher.getUserId() != null) {
            String error = accountService.deleteAccount(teacher.getUserId());
            if (error != null) {
                redirect(req, resp, "/admin/teacher?method=list", error, "danger");
                return;
            }
        } else {
            teacherService.deleteTeacher(id);
        }
        redirect(req, resp, "/admin/teacher?method=list", "教师「" + teacher.getName() + "」已删除", "success");
    }

    public void print(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("teacherList", teacherService.getAllTeachers());
        req.setAttribute("printDate", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
        forward(req, resp, "admin/print_teacher_list.jsp");
    }

    private Teacher extractTeacher(HttpServletRequest req) {
        Teacher t = new Teacher();
        t.setTeacherNumber(param(req, "teacherNumber"));
        t.setName(param(req, "name"));
        t.setTitle(param(req, "title"));
        return t;
    }

    private void backToForm(HttpServletRequest req, HttpServletResponse resp,
                            String msg, Teacher teacher)
            throws ServletException, IOException {
        req.setAttribute("msg", msg);
        req.setAttribute("msgType", "danger");
        req.setAttribute("teacher", teacher);
        req.setAttribute("defaultPassword", AccountService.DEFAULT_PASSWORD);
        forward(req, resp, "admin/admin_teacher_add.jsp");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}