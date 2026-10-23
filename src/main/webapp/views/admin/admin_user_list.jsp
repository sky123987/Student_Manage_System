<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>账号管理 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h3 class="mb-0">账号管理 <span class="text-muted fs-6">（共 ${totalCount} 个账号）</span></h3>
  <div>
    <a href="${pageContext.request.contextPath}/admin/user?method=toAdd" class="btn btn-primary">
      <i class="bi bi-plus-lg"></i> 新增账号
    </a>
    <a href="${pageContext.request.contextPath}/admin/dashboard?method=index" class="btn btn-outline-dark">返回控制台</a>
  </div>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<ul class="nav nav-pills mb-3">
  <li class="nav-item"><a class="nav-link ${empty activeRole ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/user?method=list">全部</a></li>
  <li class="nav-item"><a class="nav-link ${activeRole eq 'admin' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/user?method=list&role=admin">管理员</a></li>
  <li class="nav-item"><a class="nav-link ${activeRole eq 'teacher' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/user?method=list&role=teacher">教师</a></li>
  <li class="nav-item"><a class="nav-link ${activeRole eq 'student' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/user?method=list&role=student">学生</a></li>
</ul>

<table class="table table-striped table-hover align-middle">
  <thead class="table-dark">
  <tr>
    <th>ID</th>
    <th>账号</th>
    <th>角色</th>
    <th>姓名</th>
    <th>学号/工号</th>
    <th>班级</th>
    <th>档案</th>
    <th style="width: 300px;">操作</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="a" items="${accountList}">
    <tr>
      <td>${a.id}</td>
      <td>${a.username}</td>
      <td>
        <c:choose>
          <c:when test="${a.role eq 'admin'}"><span class="badge bg-danger">管理员</span></c:when>
          <c:when test="${a.role eq 'teacher'}"><span class="badge bg-success">教师</span></c:when>
          <c:otherwise><span class="badge bg-primary">学生</span></c:otherwise>
        </c:choose>
      </td>
      <td><c:out value="${empty a.realName ? '—' : a.realName}"/></td>
      <td><c:out value="${empty a.number ? '—' : a.number}"/></td>
      <td><c:out value="${empty a.className ? '—' : a.className}"/></td>
      <td>
        <c:choose>
          <%-- 管理员只存在于 user 表，本来就没有学生/教师档案，不算异常 --%>
          <c:when test="${a.role eq 'admin'}"><span class="text-muted">不适用</span></c:when>
          <c:when test="${a.hasProfile}"><span class="text-success">正常</span></c:when>
          <c:otherwise><span class="text-danger">缺档案</span></c:otherwise>
        </c:choose>
      </td>
      <td>
        <a href="${pageContext.request.contextPath}/admin/user?method=resetPassword&id=${a.id}"
           class="btn btn-sm btn-outline-warning"
           onclick="return confirm('确定把 ${a.username} 的密码重置为 123456 吗？');">重置密码</a>

        <c:if test="${a.role ne 'admin'}">
          <a href="${pageContext.request.contextPath}/admin/user?method=updateRole&id=${a.id}&role=${a.role eq 'teacher' ? 'student' : 'teacher'}"
             class="btn btn-sm btn-outline-info"
             onclick="return confirm('确定把 ${a.username} 的角色改为「${a.role eq 'teacher' ? '学生' : '教师'}」吗？');">
            改为${a.role eq 'teacher' ? '学生' : '教师'}
          </a>
        </c:if>

        <a href="${pageContext.request.contextPath}/admin/user?method=delete&id=${a.id}"
           class="btn btn-sm btn-outline-danger"
           onclick="return confirm('确定删除账号 ${a.username} 吗？对应档案会一起删除。');">删除</a>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty accountList}">
    <tr><td colspan="8" class="text-center text-muted py-4">没有符合条件的账号</td></tr>
  </c:if>
  </tbody>
</table>
</body>
</html>