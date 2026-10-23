package com.lut.grade.controller;

import com.lut.grade.model.User;
import com.lut.grade.service.MenuService;
import com.lut.grade.service.PermissionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 所有 Servlet 的父类。
 *
 * 职责：
 * 1) 用查询串里的 method 参数分发到同名方法（保持原有调用方式，页面不用改）；
 * 2) 统一做登录检查和权限检查 —— 权限码 = "模块:方法"，模块名由 @WebServlet 的
 *    路径换算而来（/admin/student -> admin.student），所以新加方法后不用再登记；
 * 3) 提供 forward / redirect 辅助方法，并把 ?msg= 自动搬到 request 域，
 *    这样重定向之后目标页面照样能显示提示（原来的实现里注册成功、改密成功的提示都会丢）。
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** 不需要登录就能访问的方法（登录、注册） */
    private static final Set<String> PUBLIC_METHODS =
            new HashSet<>(Arrays.asList("login", "register", "doLogin", "doRegister"));

    private static final String LOGIN_PAGE = "/views/login.jsp";

    protected PermissionService permissions() {
        return PermissionService.getInstance();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // multipart 请求体一旦超过 maxRequestSize，Tomcat 在第一次读参数时就抛
        // IllegalStateException。这里必须先拦住，否则会变成 500 白页，
        // 用户只看到"点了没反应"，看不出是文件太大。
        String methodName;
        try {
            methodName = req.getParameter("method");
        } catch (IllegalStateException e) {
            resp.setStatus(413);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("<meta charset='UTF-8'><div style='font-family:sans-serif;padding:40px'>"
                    + "<h4>上传的文件太大了</h4><p>请把文件压缩到 2MB 以内再试。</p>"
                    + "<a href='javascript:history.back()'>返回上一页</a></div>");
            return;
        }
        if (methodName == null || methodName.trim().isEmpty()) {
            methodName = "index";
        }
        methodName = methodName.trim();

        User user = currentUser(req);
        boolean isPublic = PUBLIC_METHODS.contains(methodName);

        // ---- 登录检查 ----
        if (user == null && !isPublic) {
            if (isAjax(req)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"ok\":false,\"msg\":\"登录已失效，请重新登录\"}");
            } else {
                redirect(req, resp, LOGIN_PAGE, "请先登录", "warning");
            }
            return;
        }

        // ---- 权限检查 ----
        if (user != null && !isPublic) {
            String code = moduleCode() + ":" + methodName;
            if (!permissions().isAllowed(user.getRole(), code)) {
                forbidden(req, resp, methodName);
                return;
            }
        }

        try {
            Method target = getClass().getMethod(methodName,
                    HttpServletRequest.class, HttpServletResponse.class);
            target.invoke(this, req, resp);
        } catch (NoSuchMethodException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "找不到方法：" + methodName);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause() == null ? e : e.getCause();
            cause.printStackTrace();
            if (!resp.isCommitted()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "服务器内部错误：" + cause.getMessage());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "方法无法访问");
        }
    }

    /** 模块权限前缀，由 @WebServlet 的路径换算，例如 /admin/student -> admin.student */
    protected String moduleCode() {
        WebServlet ws = getClass().getAnnotation(WebServlet.class);
        String path = "";
        if (ws != null) {
            if (ws.value() != null && ws.value().length > 0) {
                path = ws.value()[0];
            } else if (ws.urlPatterns() != null && ws.urlPatterns().length > 0) {
                path = ws.urlPatterns()[0];
            }
        }
        if (path.isEmpty()) {
            return getClass().getSimpleName().replace("Servlet", "").toLowerCase();
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/*")) {
            path = path.substring(0, path.length() - 2);
        }
        return path.replace('/', '.');
    }

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object u = session.getAttribute("user");
        return (u instanceof User) ? (User) u : null;
    }

    protected boolean isAjax(HttpServletRequest req) {
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
    }

    /** 转发到 /views 下的页面；顺带把查询串里的 msg / type 放到 request 域 */
    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        if (req.getAttribute("msg") == null) {
            String m = req.getParameter("msg");
            if (m != null && !m.trim().isEmpty()) {
                req.setAttribute("msg", m);
            }
        }
        if (req.getAttribute("msgType") == null) {
            String t = req.getParameter("type");
            req.setAttribute("msgType", (t == null || t.trim().isEmpty()) ? "info" : t.trim());
        }
        if (req.getAttribute("menuList") == null) {
            User u = currentUser(req);
            if (u != null) {
                String m = req.getParameter("method");
                String currentCode = moduleCode() + ":" + (m == null || m.trim().isEmpty() ? "index" : m.trim());
                req.setAttribute("menuList",
                        MenuService.getInstance().menusFor(u.getRole(), currentCode));
            }
        }
        String target = view.startsWith("/") ? view : "/views/" + view;
        req.getRequestDispatcher(target).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        redirect(req, resp, path, null, null);
    }

    /** 重定向并携带提示信息 */
    protected void redirect(HttpServletRequest req, HttpServletResponse resp,
                            String path, String msg, String type) throws IOException {
        String target = (path.startsWith("/") ? req.getContextPath() + path : path);
        if (msg != null && !msg.isEmpty()) {
            target += (target.contains("?") ? "&" : "?") + "msg=" + encode(msg)
                    + "&type=" + encode(type == null ? "success" : type);
        }
        resp.sendRedirect(target);
    }

    protected String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    private void forbidden(HttpServletRequest req, HttpServletResponse resp, String methodName)
            throws IOException {
        if (isAjax(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"ok\":false,\"msg\":\"没有该操作权限\"}");
            return;
        }
        req.setAttribute("forbiddenAction", moduleCode() + ":" + methodName);
        try {
            req.getRequestDispatcher("/views/error/403.jsp").forward(req, resp);
        } catch (ServletException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有该操作权限");
        }
    }

    // ---------- 参数读取小工具 ----------

    protected String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? null : v.trim();
    }

    protected String param(HttpServletRequest req, String name, String defaultValue) {
        String v = param(req, name);
        return (v == null || v.isEmpty()) ? defaultValue : v;
    }

    protected Integer paramInt(HttpServletRequest req, String name) {
        String v = param(req, name);
        if (v == null || v.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected int paramInt(HttpServletRequest req, String name, int defaultValue) {
        Integer v = paramInt(req, name);
        return v == null ? defaultValue : v;
    }

    protected Double paramDouble(HttpServletRequest req, String name) {
        String v = param(req, name);
        if (v == null || v.isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}