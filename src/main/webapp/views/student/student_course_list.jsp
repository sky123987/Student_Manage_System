<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>选课 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/student.css?v=20261023c">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jsp" %>
<div class="wrapper">
  <div class="content">
    <%@ include file="/WEB-INF/jspf/msg.jsp" %>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4 class="mb-0">可选课程</h4>
      <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/student/course?method=myCourses">我的课表</a>
    </div>

    <table class="table table-striped table-hover align-middle">
      <thead class="table-dark">
      <tr>
        <th>课程名称</th>
        <th>学分</th>
        <th>学时</th>
        <th>类型</th>
        <th>任课教师</th>
        <th>状态</th>
        <th style="width: 110px;">操作</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="c" items="${courseList}">
        <tr>
          <td><c:out value="${c.courseName}"/></td>
          <td><c:out value="${c.credit}"/></td>
          <td><c:out value="${c.courseHours}"/></td>
          <td><c:out value="${c.courseType}"/></td>
          <td><c:out value="${empty c.teacherName ? '待安排' : c.teacherName}"/></td>
          <td>
            <c:choose>
              <c:when test="${c.status eq '已选'}"><span class="badge bg-success">已选</span></c:when>
              <c:otherwise><span class="badge bg-light text-dark">未选</span></c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:choose>
              <c:when test="${c.status eq '已选'}">
                <button class="btn btn-sm btn-outline-secondary" disabled>已选</button>
              </c:when>
              <c:otherwise>
                <a class="btn btn-sm btn-primary"
                   href="${pageContext.request.contextPath}/student/course?method=select&courseId=${c.id}"
                   onclick="return confirm('确定选修《${c.courseName}》吗？');">选课</a>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty courseList}">
        <tr><td colspan="7" class="text-center text-muted py-4">当前没有可选课程</td></tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>