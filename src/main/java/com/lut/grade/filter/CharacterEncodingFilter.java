package com.lut.grade.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        // 设置请求体编码为 UTF-8（解决 POST 乱码）
        req.setCharacterEncoding("UTF-8");
        // 设置响应内容类型和编码（解决响应乱码）
        resp.setContentType("text/html;charset=UTF-8");

        // 放行请求
        chain.doFilter(req, resp);
    }
}