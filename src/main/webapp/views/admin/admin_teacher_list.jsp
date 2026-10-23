<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>教师管理 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h3 class="mb-0">教师管理</h3>
  <div>
    <a href="${pageContext.request.contextPath}/admin/teacher?method=toAdd" class="btn btn-primary">
      <i class="bi bi-plus-lg"></i> 新增教师
    </a>
    <a href="${pageContext.request.contextPath}/admin/teacher?method=print" target="_blank" class="btn btn-outline-secondary">
      <i class="bi bi-printer"></i> 打印教师名单
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
      <th>工号</th>
      <th>姓名</th>
      <th>职称</th>
      <th style="width: 140px;">操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="t" items="${teacherList}">
      <tr>
        <td>${t.id}</td>
        <td>${t.teacherNumber}</td>
        <td>${t.name}</td>
        <td><c:out value="${empty t.title ? '—' : t.title}"/></td>
        <td>
          <a href="${pageContext.request.contextPath}/admin/teacher?method=toEdit&id=${t.id}"
             class="btn btn-sm btn-outline-primary">编辑</a>
          <a href="${pageContext.request.contextPath}/admin/teacher?method=delete&id=${t.id}"
             class="btn btn-sm btn-outline-danger"
             onclick="return confirm('确定删除教师 ${t.name} 吗？该教师的登录账号会一起删除。');">删除</a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty teacherList}">
      <tr><td colspan="5" class="text-center text-muted py-4">还没有教师，点击右上角「新增教师」添加</td></tr>
    </c:if>
    </tbody>
  </table>
</div>
</body>
</html>