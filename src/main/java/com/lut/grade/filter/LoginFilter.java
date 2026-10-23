package com.lut.grade.filter;

import com.lut.grade.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 登录与角色区域的粗粒度拦截。
 *
 * 这里只做两件事：
 * 1) 没登录的人不能访问业务页面（登录页、注册页、静态资源除外）；
 * 2) 角色不能进别人的目录（/admin/、/teacher/、/student/ 前缀严格匹配）。
 *
 * 细到"某个功能按钮能不能点"的权限由 BaseServlet 用数据库里的角色权限表判断，
 * 所以这里放宽一点没关系，多一道闸而已。
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    /** 不需要登录的路径前缀 */
    private static final List<String> PUBLIC_PREFIXES = Arrays.asList(
            "/css/", "/js/", "/images/", "/uploads/", "/favicon.ico");

    /** 不需要登录的页面 */
    private static final List<String> PUBLIC_PAGES = Arrays.asList(
            "/views/login.jsp", "/views/register.jsp", "/views/error/");

    /** /user 上允许匿名调用的方法 */
    private static final List<String> PUBLIC_USER_METHODS = Arrays.asList("login", "register");

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        req.setCharacterEncoding("UTF-8");

        String ctx = req.getContextPath();
        String uri = req.getRequestURI();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }

        if (isPublic(uri, req)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(ctx + "/views/login.jsp?msg="
                    + java.net.URLEncoder.encode("请先登录", "UTF-8") + "&type=warning");
            return;
        }

        String role = user.getRole();
        if (isRoleAreaDenied(uri, role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "无权访问该角色的页面");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String uri, HttpServletRequest req) {
        for (String prefix : PUBLIC_PREFIXES) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        for (String page : PUBLIC_PAGES) {
            if (uri.startsWith(page)) {
                return true;
            }
        }
        // 只有 /user 上的登录、注册允许匿名
        if (uri.equals("/user") || uri.startsWith("/user?")) {
            String method = req.getParameter("method");
            return method == null || PUBLIC_USER_METHODS.contains(method);
        }
        return false;
    }

    private boolean isRoleAreaDenied(String uri, String role) {
        if ("admin".equals(role)) {
            return false;
        }
        if (uri.startsWith("/admin/") || uri.startsWith("/views/admin/")) {
            return true;
        }
        if ("student".equals(role)) {
            return uri.startsWith("/teacher/") || uri.startsWith("/views/teacher/");
        }
        if ("teacher".equals(role)) {
            return uri.startsWith("/student/") || uri.startsWith("/views/student/");
        }
        return true;
    }

    @Override
    public void destroy() {
        // nothing
    }
}