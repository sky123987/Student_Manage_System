<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>我的课程 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css?v=20261023c">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jsp" %>
<div class="wrapper">
  <div class="content">
    <%@ include file="/WEB-INF/jspf/msg.jsp" %>
    <h4 class="mb-3">我的课程</h4>

    <table class="table table-striped table-hover align-middle">
      <thead class="table-dark">
      <tr>
        <th>ID</th>
        <th>课程名称</th>
        <th>学分</th>
        <th>学时</th>
        <th>类型</th>
        <th style="width: 300px;">操作</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="c" items="${courseList}">
        <tr>
          <td>${c.id}</td>
          <td><c:out value="${c.courseName}"/></td>
          <td>${c.credit}</td>
          <td>${c.courseHours}</td>
          <td><c:out value="${c.courseType}"/></td>
          <td>
            <a class="btn btn-sm btn-outline-primary"
               href="${pageContext.request.contextPath}/teacher/grade?method=list&courseId=${c.id}">成绩录入</a>
            <a class="btn btn-sm btn-outline-info"
               href="${pageContext.request.contextPath}/teacher/grade?method=stats&courseId=${c.id}">成绩统计</a>
            <a class="btn btn-sm btn-outline-secondary"
               href="${pageContext.request.contextPath}/teacher/roster?method=list&courseId=${c.id}">选课名单</a>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty courseList}">
        <tr><td colspan="6" class="text-center text-muted py-4">您还没有被安排课程，请联系管理员</td></tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>