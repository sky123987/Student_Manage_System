<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>课程管理 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h3 class="mb-0">课程管理</h3>
  <div>
    <a href="${pageContext.request.contextPath}/admin/course?method=toAdd" class="btn btn-primary">
      <i class="bi bi-plus-lg"></i> 新增课程
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
      <th>课程名称</th>
      <th>学分</th>
      <th>学时</th>
      <th>类型</th>
      <th>任课教师</th>
      <th style="width: 260px;">操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="c" items="${courseList}">
      <tr>
        <td>${c.id}</td>
        <td>${c.courseName}</td>
        <td>${c.credit}</td>
        <td><c:out value="${empty c.courseHours ? '—' : c.courseHours}"/></td>
        <td><span class="badge bg-secondary">${c.courseType}</span></td>
        <td><c:out value="${empty c.teacherName ? '未分配' : c.teacherName}"/></td>
        <td>
          <a href="${pageContext.request.contextPath}/admin/course?method=toEdit&id=${c.id}"
             class="btn btn-sm btn-outline-primary">编辑</a>
          <a href="${pageContext.request.contextPath}/admin/template?method=toConfig&courseId=${c.id}"
             class="btn btn-sm btn-outline-info">成绩模板</a>
          <a href="${pageContext.request.contextPath}/admin/roster?method=list&courseId=${c.id}"
             class="btn btn-sm btn-outline-secondary">选课名单</a>
          <a href="${pageContext.request.contextPath}/admin/course?method=delete&id=${c.id}"
             class="btn btn-sm btn-outline-danger"
             onclick="return confirm('确定删除课程 ${c.courseName} 吗？');">删除</a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty courseList}">
      <tr><td colspan="7" class="text-center text-muted py-4">还没有课程，点击右上角「新增课程」添加</td></tr>
    </c:if>
    </tbody>
  </table>
</div>
</body>
</html>
