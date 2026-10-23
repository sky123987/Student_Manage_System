<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>没有权限</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
</head>
<body>
<div class="container" style="max-width:560px;margin-top:12vh;">
    <div class="card shadow-sm border-0">
        <div class="card-body text-center p-5">
            <div class="display-6 text-warning mb-3">403</div>
            <h5 class="mb-3">没有该操作的权限</h5>
            <p class="text-muted mb-1">你的角色权限里不包含这个功能，请联系管理员在「角色权限」页面开通。</p>
            <c:if test="${not empty forbiddenAction}">
                <p class="text-muted small mb-4">权限码：<code>${forbiddenAction}</code></p>
            </c:if>
            <a class="btn btn-primary" href="javascript:history.back()">返回上一页</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/views/login.jsp">回到登录页</a>
        </div>
    </div>
</div>
</body>
</html>