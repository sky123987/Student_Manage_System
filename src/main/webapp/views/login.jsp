<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - 学生成绩管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css?v=20261023c">
</head>
<body class="auth-body">
<main class="auth-shell">
    <div class="auth-brand">
        <div class="auth-logo"><i class="bi bi-mortarboard-fill"></i></div>
        <h1>学生成绩管理系统</h1>
        <p>Student Achievement Management</p>
    </div>

    <section class="auth-card">
        <div class="auth-card-head">
            <h2>欢迎回来</h2>
            <p>请使用您的账号登录系统</p>
        </div>

        <c:if test="${not empty msg or not empty param.msg}">
            <div class="alert alert-${empty param.type ? 'danger' : param.type} auth-alert" role="alert">
                <i class="bi bi-exclamation-circle me-1"></i>${not empty msg ? msg : param.msg}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/user?method=login" method="post">
            <div class="mb-3">
                <label class="form-label" for="username">用户名</label>
                <div class="input-group auth-input">
                    <span class="input-group-text"><i class="bi bi-person"></i></span>
                    <input type="text" class="form-control" id="username" name="username"
                           value="${param.username}" placeholder="请输入用户名"
                           autocomplete="username" required autofocus>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label" for="password">密码</label>
                <div class="input-group auth-input">
                    <span class="input-group-text"><i class="bi bi-lock"></i></span>
                    <input type="password" class="form-control" id="password" name="password"
                           placeholder="请输入密码" autocomplete="current-password" required>
                    <button class="btn btn-eye" type="button" data-toggle-password="password" aria-label="显示密码">
                        <i class="bi bi-eye"></i>
                    </button>
                </div>
            </div>

            <button type="submit" class="btn btn-auth-primary w-100 mt-2">登 录</button>
        </form>

        <div class="auth-links">
            还没有账号？<a href="${pageContext.request.contextPath}/views/register.jsp">立即注册</a>
        </div>
    </section>

    <footer class="auth-footer">© 学生成绩管理系统 · 教务信息化平台</footer>
</main>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js?v=20261023c"></script>
<script src="${pageContext.request.contextPath}/js/auth.js?v=20261023c"></script>
</body>
</html>