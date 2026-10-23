<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>选课名单 - 成绩管理系统</title>
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

    <div class="d-flex justify-content-between align-items-center mb-2">
      <h4 class="mb-0">选课名单：<c:out value="${course.courseName}"/></h4>
      <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/teacher/course?method=list">返回课程列表</a>
    </div>
    <p class="text-muted">
      学分 <c:out value="${course.credit}"/> ·
      学时 <c:out value="${course.courseHours}"/> ·
      类型 <c:out value="${course.courseType}"/> ·
      共 ${rosterList.size()} 人
    </p>

    <table class="table table-striped table-hover align-middle">
      <thead class="table-dark">
      <tr>
        <th>学号</th>
        <th>姓名</th>
        <th>班级</th>
        <th>总评成绩</th>
        <th>状态</th>
        <th style="width: 120px;">操作</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="r" items="${rosterList}">
        <tr>
          <td>${r.studentNumber}</td>
          <td><c:out value="${r.studentName}"/></td>
          <td><c:out value="${empty r.className ? '—' : r.className}"/></td>
          <td><c:out value="${empty r.totalScore ? '未录入' : r.totalScore}"/></td>
          <td>
            <c:choose>
              <c:when test="${r.status eq 'submitted'}"><span class="badge bg-success">已提交</span></c:when>
              <c:when test="${r.status eq 'draft'}"><span class="badge bg-warning text-dark">草稿</span></c:when>
              <c:otherwise><span class="badge bg-light text-dark">未录入</span></c:otherwise>
            </c:choose>
          </td>
          <td>
            <a class="btn btn-sm btn-outline-danger"
               href="${pageContext.request.contextPath}/teacher/roster?method=remove&courseId=${courseId}&studentId=${r.studentId}"
               onclick="return confirm('确定把这名学生从本课程名单中移除吗？');">移除</a>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty rosterList}">
        <tr><td colspan="6" class="text-center text-muted py-4">还没有学生选修本课程</td></tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>