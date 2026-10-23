package com.lut.grade.controller;

import com.lut.grade.model.Student;
import com.lut.grade.model.Teacher;
import com.lut.grade.model.UserAccount;
import com.lut.grade.service.AccountService;
import com.lut.grade.service.ClassService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员 - 账号管理。
 * 这里解决的是原来最尴尬的死结：管理员只能从"学生/教师"页面建账号，
 * 而这两个页面又建不出账号；已经存在的孤立账号也没地方改角色、重置密码、删除。
 */
@WebServlet("/admin/user")
public class AdminUserServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountService();
    private final ClassService classService = new ClassService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String role = param(req, "role");
        List<UserAccount> all = accountService.listAccounts();
        List<UserAccount> filtered = new ArrayList<>();
        for (UserAccount a : all) {
            if (role == null || role.isEmpty() || role.equals(a.getRole())) {
                filtered.add(a);
            }
        }
        req.setAttribute("accountList", filtered);
        req.setAttribute("activeRole", role == null ? "" : role);
        req.setAttribute("totalCount", all.size());
        forward(req, resp, "admin/admin_user_list.jsp");
    }

    public void toAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("classList", classService.getAllClasses());
        req.setAttribute("defaultPassword", AccountService.DEFAULT_PASSWORD);
        forward(req, resp, "admin/admin_user_add.jsp");
    }

    public void add(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = param(req, "username");
        String password = param(req, "password");
        String role = param(req, "role", "student");
        String realName = param(req, "realName");
        String number = param(req, "number");

        String error;
        if ("admin".equals(role)) {
            // 管理员没有档案表，直接建账号
            error = accountService.selfRegister(username, password, "admin");
            if (error != null) {
                backToForm(req, resp, error, username, role);
                return;
            }
        } else if ("teacher".equals(role)) {
            Teacher t = new Teacher();
            t.setName(isBlank(realName) ? username : realName);
            t.setTeacherNumber(isBlank(number) ? username : number);
            t.setTitle(param(req, "title"));
            error = accountService.createTeacher(username, password, t);
            if (error != null) {
                backToForm(req, resp, error, username, role);
                return;
            }
        } else {
            Student s = new Student();
            s.setName(isBlank(realName) ? username : realName);
            s.setStudentNumber(isBlank(number) ? username : number);
            s.setClassId(paramInt(req, "classId"));
            s.setGender(param(req, "gender", "男"));
            error = accountService.createStudent(username, password, s);
            if (error != null) {
                backToForm(req, resp, error, username, role);
                return;
            }
        }
        redirect(req, resp, "/admin/user?method=list", "账号「" + username + "」创建成功", "success");
    }

    public void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = paramInt(req, "id");
        if (accountService.resetPassword(userId)) {
            redirect(req, resp, "/admin/user?method=list",
                    "密码已重置为 " + AccountService.DEFAULT_PASSWORD, "success");
        } else {
            redirect(req, resp, "/admin/user?method=list", "重置失败，账号可能不存在", "danger");
        }
    }

    public void updateRole(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = paramInt(req, "id");
        String role = param(req, "role");
        String error = accountService.changeRole(userId, role);
        if (error != null) {
            redirect(req, resp, "/admin/user?method=list", error, "danger");
        } else {
            redirect(req, resp, "/admin/user?method=list", "角色已修改为「" + roleName(role) + "」", "success");
        }
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = paramInt(req, "id");
        String error = accountService.deleteAccount(userId);
        if (error != null) {
            redirect(req, resp, "/admin/user?method=list", error, "danger");
        } else {
            redirect(req, resp, "/admin/user?method=list", "账号已删除", "success");
        }
    }

    private void backToForm(HttpServletRequest req, HttpServletResponse resp,
                            String msg, String username, String role)
            throws ServletException, IOException {
        req.setAttribute("msg", msg);
        req.setAttribute("msgType", "danger");
        req.setAttribute("username", username);
        req.setAttribute("role", role);
        req.setAttribute("classList", classService.getAllClasses());
        req.setAttribute("defaultPassword", AccountService.DEFAULT_PASSWORD);
        forward(req, resp, "admin/admin_user_add.jsp");
    }

    static String roleName(String role) {
        if ("admin".equals(role)) return "管理员";
        if ("teacher".equals(role)) return "教师";
        if ("student".equals(role)) return "学生";
        return role;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}