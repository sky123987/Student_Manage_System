package com.lut.grade.controller;

import com.lut.grade.dao.StudentDao;
import com.lut.grade.dao.TeacherDao;
import com.lut.grade.dao.UserDao;
import com.lut.grade.model.Student;
import com.lut.grade.model.Teacher;
import com.lut.grade.model.User;
import com.lut.grade.service.AccountService;
import com.lut.grade.service.UserService;
import com.lut.grade.util.PasswordPolicy;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 用户入口：登录、注册、退出、修改密码、个人中心、上传头像。
 */
@WebServlet("/user")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024L, maxRequestSize = 4 * 1024 * 1024L)
public class UserServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final List<String> ALLOWED_EXT =
            Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    private final UserDao userDao = new UserDao();
    private final StudentDao studentDao = new StudentDao();
    private final TeacherDao teacherDao = new TeacherDao();

    // ===================== 登录 / 注册 / 退出 =====================

    public void login(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = param(req, "username");
        String password = param(req, "password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("msg", "请输入账号和密码");
            req.setAttribute("msgType", "warning");
            forward(req, resp, "login.jsp");
            return;
        }

        User user = userService.login(username, password);
        if (user == null) {
            req.setAttribute("msg", "账号或密码错误！");
            req.setAttribute("msgType", "danger");
            req.setAttribute("username", username);
            forward(req, resp, "login.jsp");
            return;
        }

        // 老账号如果缺档案，这里补一条，避免一进业务页面就报"档案不存在"
        accountService.ensureProfile(user);

        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        session.removeAttribute("msg");

        String role = user.getRole();
        if ("admin".equals(role)) {
            redirect(req, resp, "/admin/dashboard?method=index");
        } else if ("teacher".equals(role)) {
            redirect(req, resp, "/teacher/course?method=dashboard");
        } else {
            redirect(req, resp, "/student/dashboard?method=index");
        }
    }

    public void register(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = param(req, "username");
        String password = param(req, "password");
        String confirm = param(req, "confirmPassword");
        if (confirm == null) {
            confirm = param(req, "confirmPwd");
        }
        String role = param(req, "role", "student");
        String realName = param(req, "realName");
        String number = param(req, "number");
        Integer classId = paramInt(req, "classId");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            backToRegister(req, resp, "账号和密码不能为空", username, realName, number, role);
            return;
        }
        if (!password.equals(confirm)) {
            backToRegister(req, resp, "两次输入的密码不一致", username, realName, number, role);
            return;
        }
        // 密码强度：至少 6 位，且必须同时包含字母和数字（规则集中在 PasswordPolicy）
        String pwdError = PasswordPolicy.check(password);
        if (pwdError != null) {
            backToRegister(req, resp, pwdError, username, realName, number, role);
            return;
        }
        if (!"teacher".equals(role)) {
            role = "student";
        }

        String error;
        if ("teacher".equals(role)) {
            Teacher t = new Teacher();
            t.setName(isBlank(realName) ? username : realName);
            t.setTeacherNumber(isBlank(number) ? username : number);
            error = accountService.createTeacher(username, password, t);
        } else {
            Student s = new Student();
            s.setName(isBlank(realName) ? username : realName);
            s.setStudentNumber(isBlank(number) ? username : number);
            s.setClassId(classId);
            error = accountService.createStudent(username, password, s);
        }

        if (error != null) {
            backToRegister(req, resp, error, username, realName, number, role);
            return;
        }

        // 成功：提示放在 URL 上，登录页能显示（原来放在 session 里，登录页读不到，提示就丢了）
        redirect(req, resp, "/views/login.jsp", "注册成功，请使用「" + username + "」登录", "success");
    }

    private void backToRegister(HttpServletRequest req, HttpServletResponse resp,
                                String msg, String username, String realName,
                                String number, String role)
            throws ServletException, IOException {
        req.setAttribute("msg", msg);
        req.setAttribute("msgType", "danger");
        req.setAttribute("username", username);
        req.setAttribute("realName", realName);
        req.setAttribute("number", number);
        req.setAttribute("role", role);
        forward(req, resp, "register.jsp");
    }

    public void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect(req, resp, "/views/login.jsp", "已安全退出", "info");
    }

    public void changePassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) {
            redirect(req, resp, "/views/login.jsp", "请先登录", "warning");
            return;
        }

        String oldPwd = param(req, "oldPwd");
        String newPwd = param(req, "newPwd");
        String confirmPwd = param(req, "confirmPwd");

        if (isBlank(oldPwd) || isBlank(newPwd) || isBlank(confirmPwd)) {
            failChangePassword(req, resp, "三个密码框都要填写");
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            failChangePassword(req, resp, "两次输入的新密码不一致");
            return;
        }
        String pwdError = PasswordPolicy.check(newPwd);
        if (pwdError != null) {
            failChangePassword(req, resp, "新" + pwdError);
            return;
        }

        boolean ok = userService.changePassword(user, oldPwd, newPwd);
        if (!ok) {
            failChangePassword(req, resp, "原密码不正确");
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect(req, resp, "/views/login.jsp", "密码修改成功，请用新密码重新登录", "success");
    }

    private void failChangePassword(HttpServletRequest req, HttpServletResponse resp, String msg)
            throws ServletException, IOException {
        req.setAttribute("msg", msg);
        req.setAttribute("msgType", "danger");
        forward(req, resp, "change_password.jsp");
    }

    // ===================== 个人中心 / 头像 =====================

    public void toProfile(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User sessionUser = currentUser(req);
        if (sessionUser == null) {
            redirect(req, resp, "/views/login.jsp", "请先登录", "warning");
            return;
        }
        User fresh = userDao.findById(sessionUser.getId());
        if (fresh != null) {
            req.getSession().setAttribute("user", fresh);
        } else {
            fresh = sessionUser;
        }

        req.setAttribute("profileUser", fresh);
        if ("student".equals(fresh.getRole())) {
            req.setAttribute("studentProfile", studentDao.findByUserId(fresh.getId()));
        } else if ("teacher".equals(fresh.getRole())) {
            req.setAttribute("teacherProfile", teacherDao.findByUserId(fresh.getId()));
        }
        forward(req, resp, "user_profile.jsp");
    }

    public void uploadAvatar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) {
            redirect(req, resp, "/views/login.jsp", "请先登录", "warning");
            return;
        }

        // 表单字段名以 avatarFile 为准，但历史上页面写的是 avatar，
        // 两个名字都收一下，避免改了页面忘了改后端（或反过来）导致"选了文件传不上去"。
        Part part = null;
        try {
            for (String field : new String[]{"avatarFile", "avatar", "file"}) {
                Part p = req.getPart(field);
                if (p != null && p.getSize() > 0) {
                    part = p;
                    break;
                }
            }
        } catch (IllegalStateException e) {
            redirect(req, resp, "/user?method=toProfile", "图片太大了，请选择 2MB 以内的图片", "danger");
            return;
        }

        if (part == null || part.getSize() <= 0) {
            redirect(req, resp, "/user?method=toProfile", "请选择要上传的图片", "warning");
            return;
        }

        String submitted = part.getSubmittedFileName();
        String ext = extensionOf(submitted);
        if (!ALLOWED_EXT.contains(ext)) {
            redirect(req, resp, "/user?method=toProfile",
                    "只支持 jpg / jpeg / png / gif / bmp / webp 格式的图片", "danger");
            return;
        }

        String dir = uploadDir();
        if (dir == null) {
            redirect(req, resp, "/user?method=toProfile", "服务器无法确定上传目录，头像上传失败", "danger");
            return;
        }
        File folder = new File(dir);
        if (!folder.exists() && !folder.mkdirs()) {
            redirect(req, resp, "/user?method=toProfile", "创建上传目录失败", "danger");
            return;
        }

        String fileName = "avatar_" + user.getId() + "_" + System.currentTimeMillis() + "." + ext;
        File target = new File(folder, fileName);
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            redirect(req, resp, "/user?method=toProfile", "保存图片失败：" + e.getMessage(), "danger");
            return;
        }

        String relative = "/uploads/avatar/" + fileName;
        if (!userDao.updateAvatar(user.getId(), relative)) {
            redirect(req, resp, "/user?method=toProfile", "头像已保存到磁盘，但更新数据库失败", "danger");
            return;
        }

        User fresh = userDao.findById(user.getId());
        if (fresh != null) {
            req.getSession().setAttribute("user", fresh);
        }
        redirect(req, resp, "/user?method=toProfile", "头像已更新", "success");
    }

    private String uploadDir() {
        String real = getServletContext().getRealPath("/uploads/avatar");
        if (real != null) {
            return real;
        }
        // 打成 war 不展开部署时拿不到真实路径，退到临时目录
        String tmp = System.getProperty("java.io.tmpdir");
        return tmp == null ? null : new File(tmp, "dsh-uploads/avatar").getAbsolutePath();
    }

    private static String extensionOf(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}