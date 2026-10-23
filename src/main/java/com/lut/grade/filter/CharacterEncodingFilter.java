package com.lut.grade.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * 统一使用 UTF-8 编码。
 *
 * 注意：不能对静态资源（css/js/images/uploads）设置 text/html，
 * 否则浏览器会因为 MIME 类型不匹配而拒绝应用样式表、脚本和字体。
 */
public class CharacterEncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        // 请求体编码（解决 POST 中文乱码）
        req.setCharacterEncoding("UTF-8");

        // 只给动态页面设置响应类型；静态资源交给 Tomcat 的 DefaultServlet 决定
        if (!isStaticResource(req)) {
            resp.setContentType("text/html;charset=UTF-8");
        }

        chain.doFilter(req, resp);
    }

    private boolean isStaticResource(ServletRequest req) {
        if (!(req instanceof HttpServletRequest)) {
            return false;
        }
        HttpServletRequest request = (HttpServletRequest) req;
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }
        return uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/uploads/")
                || uri.equals("/favicon.ico");
    }
}