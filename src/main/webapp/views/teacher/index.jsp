<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>教师工作台 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jsp" %>
<div class="wrapper">
  <div class="content">
    <%@ include file="/WEB-INF/jspf/msg.jsp" %>

    <h4 class="mb-3">教师工作台</h4>

    <div class="row g-3 mb-4">
      <div class="col-md-4">
        <div class="card text-bg-primary">
          <div class="card-body">
            <div class="fs-2 fw-bold">${courseCount}</div>
            <div>我的课程</div>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card text-bg-success">
          <div class="card-body">
            <div class="fs-2 fw-bold">${studentCount}</div>
            <div>授课学生</div>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card text-bg-warning">
          <div class="card-body">
            <div class="fs-2 fw-bold">${pendingCount}</div>
            <div>待录入成绩</div>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>待办事项</span>
        <a href="${pageContext.request.contextPath}/teacher/course?method=list" class="btn btn-sm btn-primary">进入我的课程</a>
      </div>
      <div class="card-body p-0">
        <table class="table table-striped table-hover align-middle mb-0">
          <thead class="table-dark">
          <tr>
            <th>课程</th>
            <th>班级</th>
            <th>选课人数</th>
            <th>待录入人数</th>
            <th style="width: 120px;">操作</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="t" items="${taskList}">
            <tr>
              <td><c:out value="${t.courseName}"/></td>
              <td><c:out value="${empty t.className ? '—' : t.className}"/></td>
              <td>${t.totalStudents}</td>
              <td>
                <c:choose>
                  <c:when test="${t.pendingStudents gt 0}">
                    <span class="badge bg-warning text-dark">${t.pendingStudents}</span>
                  </c:when>
                  <c:otherwise><span class="badge bg-success">已完成</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <a class="btn btn-sm btn-outline-primary"
                   href="${pageContext.request.contextPath}/teacher/grade?method=list&courseId=${t.courseId}">录入/查看</a>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty taskList}">
            <tr><td colspan="5" class="text-center text-muted py-4">暂时没有教学任务</td></tr>
          </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
</body>
</html>