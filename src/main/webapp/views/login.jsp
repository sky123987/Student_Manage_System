<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>登录 - 学生成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="bg-light">
<div class="container" style="max-width: 420px; margin-top: 8vh;">
  <div class="text-center mb-4">
    <h2>🎓 学生成绩管理系统</h2>
    <p class="text-muted">请使用学校分配的账号登录</p>
  </div>

  <c:if test="${not empty msg or not empty param.msg}">
    <div class="alert alert-${empty param.type ? 'danger' : param.type}" role="alert">
      <c:out value="${empty msg ? param.msg : msg}"/>
    </div>
  </c:if>

  <div class="card shadow-sm">
    <div class="card-body p-4">
      <form action="${pageContext.request.contextPath}/user?method=login" method="post">
        <div class="mb-3">
          <label class="form-label">账号</label>
          <input type="text" name="username" class="form-control" required autofocus>
        </div>
        <div class="mb-3">
          <label class="form-label">密码</label>
          <input type="password" name="password" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary w-100">登录</button>
      </form>
    </div>
  </div>

  <div class="text-center mt-3">
    <a href="${pageContext.request.contextPath}/views/register.jsp">没有账号？点击注册</a>
  </div>
</div>
</body>
</html>