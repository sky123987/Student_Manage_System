<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>我的课表 - 成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/student.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jsp" %>
<div class="wrapper">
  <div class="content">
    <%@ include file="/WEB-INF/jspf/msg.jsp" %>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4 class="mb-0">我的课表</h4>
      <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/student/course?method=list">去选课</a>
    </div>

    <table class="table table-striped table-hover align-middle">
      <thead class="table-dark">
      <tr>
        <th>课程名称</th>
        <th>学分</th>
        <th>学时</th>
        <th>类型</th>
        <th>任课教师</th>
        <th style="width: 110px;">操作</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="c" items="${myCourseList}">
        <tr>
          <td><c:out value="${c.courseName}"/></td>
          <td><c:out value="${c.credit}"/></td>
          <td><c:out value="${c.courseHours}"/></td>
          <td><c:out value="${c.courseType}"/></td>
          <td><c:out value="${empty c.teacherName ? '待安排' : c.teacherName}"/></td>
          <td>
            <a class="btn btn-sm btn-outline-danger"
               href="${pageContext.request.contextPath}/student/course?method=drop&courseId=${c.id}"
               onclick="return confirm('确定退选《${c.courseName}》吗？退课后该课程成绩将一并删除。');">退课</a>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty myCourseList}">
        <tr><td colspan="6" class="text-center text-muted py-4">你还没有选课</td></tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>