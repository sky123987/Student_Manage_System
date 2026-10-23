<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>学生管理 - 成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h3 class="mb-0">学生管理</h3>
  <div>
    <a href="${pageContext.request.contextPath}/admin/student?method=toAdd" class="btn btn-primary">
      <i class="bi bi-plus-lg"></i> 新增学生
    </a>
    <a href="${pageContext.request.contextPath}/admin/student?method=print" target="_blank" class="btn btn-outline-secondary">
      <i class="bi bi-printer"></i> 打印学生名单
    </a>
    <a href="${pageContext.request.contextPath}/admin/dashboard?method=index" class="btn btn-outline-dark">返回控制台</a>
  </div>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<div class="table-responsive">
  <table class="table table-striped table-hover align-middle">
    <thead class="table-dark">
    <tr>
      <th>ID</th>
      <th>学号</th>
      <th>姓名</th>
      <th>班级</th>
      <th>性别</th>
      <th>电话</th>
      <th>邮箱</th>
      <th style="width: 140px;">操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="s" items="${studentList}">
      <tr>
        <td>${s.id}</td>
        <td>${s.studentNumber}</td>
        <td>${s.name}</td>
        <td><c:out value="${empty s.className ? '未分班' : s.className}"/></td>
        <td>${s.gender}</td>
        <td><c:out value="${s.phone}"/></td>
        <td><c:out value="${s.email}"/></td>
        <td>
          <a href="${pageContext.request.contextPath}/admin/student?method=toEdit&id=${s.id}"
             class="btn btn-sm btn-outline-primary">编辑</a>
          <a href="${pageContext.request.contextPath}/admin/student?method=delete&id=${s.id}"
             class="btn btn-sm btn-outline-danger"
             onclick="return confirm('确定删除学生 ${s.name} 吗？该学生的登录账号和成绩会一起删除。');">删除</a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty studentList}">
      <tr><td colspan="8" class="text-center text-muted py-4">还没有学生，点击右上角「新增学生」添加</td></tr>
    </c:if>
    </tbody>
  </table>
</div>
</body>
</html>