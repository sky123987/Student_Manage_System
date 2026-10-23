<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>补考录入 - 成绩管理系统</title>
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
    <h4 class="mb-3">补考成绩录入</h4>
    <p class="text-muted">这里列出已通过审批的补考申请。补考成绩 ≥ 60 分按 60 分计（绩点 1.0），低于 60 分按实际分数计。</p>

    <table class="table table-striped table-hover align-middle">
      <thead class="table-dark">
      <tr>
        <th>学号</th>
        <th>姓名</th>
        <th>课程</th>
        <th>原总评</th>
        <th>当前补考分</th>
        <th style="width: 320px;">录入补考成绩</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="m" items="${makeupList}">
        <tr>
          <td>${m.studentNumber}</td>
          <td><c:out value="${m.studentName}"/></td>
          <td><c:out value="${m.courseName}"/></td>
          <td><c:out value="${empty m.originalScore ? '—' : m.originalScore}"/></td>
          <td><c:out value="${empty m.makeupScore ? '未录入' : m.makeupScore}"/></td>
          <td>
            <form class="d-flex" action="${pageContext.request.contextPath}/teacher/makeup?method=save" method="post">
              <input type="hidden" name="applyId" value="${m.id}">
              <input type="hidden" name="studentId" value="${m.studentId}">
              <input type="hidden" name="courseId" value="${m.courseId}">
              <input type="number" step="0.5" min="0" max="100" name="makeupScore"
                     class="form-control form-control-sm me-2" style="max-width: 120px;"
                     value="${m.makeupScore}" required>
              <button type="submit" class="btn btn-sm btn-primary">保存</button>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty makeupList}">
        <tr><td colspan="6" class="text-center text-muted py-4">当前没有已通过审批的补考申请</td></tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>