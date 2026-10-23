<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>班级管理 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css?v=20261023c">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h3 class="mb-0">班级管理</h3>
  <div>
    <a href="${pageContext.request.contextPath}/admin/class?method=toAdd" class="btn btn-primary">
      <i class="bi bi-plus-lg"></i> 新增班级
    </a>
    <a href="${pageContext.request.contextPath}/admin/dashboard?method=index" class="btn btn-outline-dark">返回控制台</a>
  </div>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<table class="table table-striped table-hover align-middle">
  <thead class="table-dark">
  <tr>
    <th>ID</th>
    <th>班级名称</th>
    <th>年级</th>
    <th style="width: 150px;">操作</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="cl" items="${classList}">
    <tr>
      <td>${cl.id}</td>
      <td>${cl.className}</td>
      <td><c:out value="${empty cl.gradeYear ? '—' : cl.gradeYear}"/></td>
      <td>
        <a href="${pageContext.request.contextPath}/admin/class?method=toEdit&id=${cl.id}"
           class="btn btn-sm btn-outline-primary">编辑</a>
        <a href="${pageContext.request.contextPath}/admin/class?method=delete&id=${cl.id}"
           class="btn btn-sm btn-outline-danger"
           onclick="return confirm('确定删除班级 ${cl.className} 吗？该班学生会被置为未分班。');">删除</a>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty classList}">
    <tr><td colspan="4" class="text-center text-muted py-4">还没有班级，点击右上角「新增班级」添加</td></tr>
  </c:if>
  </tbody>
</table>
</body>
</html>