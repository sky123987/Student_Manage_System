<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>班级成绩统计 - 成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h4 class="mb-0">班级成绩统计</h4>
  <div>
    <a href="${pageContext.request.contextPath}/admin/grade?method=exportAll" class="btn btn-outline-success">
      <i class="bi bi-file-earmark-excel"></i> 导出全校统计报表
    </a>
    <a href="${pageContext.request.contextPath}/admin/dashboard?method=index" class="btn btn-outline-dark">返回控制台</a>
  </div>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<table class="table table-striped table-hover align-middle">
  <thead class="table-dark">
  <tr>
    <th>排名</th>
    <th>班级</th>
    <th>学生人数</th>
    <th>平均分</th>
    <th>平均绩点</th>
    <th style="width: 220px;">操作</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="cs" items="${classStats}" varStatus="st">
    <tr>
      <td>${st.index + 1}</td>
      <td><c:out value="${cs.className}"/></td>
      <td>${cs.studentCount}</td>
      <td><span class="fw-bold">${cs.avgScore}</span></td>
      <td>${cs.avgGpa}</td>
      <td>
        <a class="btn btn-sm btn-outline-primary"
           href="${pageContext.request.contextPath}/admin/grade?method=studentRanks&classId=${cs.classId}">学生排名</a>
        <a class="btn btn-sm btn-outline-success"
           href="${pageContext.request.contextPath}/admin/grade?method=exportClass&classId=${cs.classId}">
          <i class="bi bi-file-earmark-excel"></i> 导出
        </a>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty classStats}">
    <tr><td colspan="6" class="text-center text-muted py-4">还没有班级数据</td></tr>
  </c:if>
  </tbody>
</table>
</body>
</html>