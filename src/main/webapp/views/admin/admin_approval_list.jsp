<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>审批中心 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css?v=20261023c">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h3 class="mb-0">成绩修改申请审批</h3>
  <a href="${pageContext.request.contextPath}/admin/dashboard?method=index" class="btn btn-outline-dark">返回控制台</a>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<table class="table table-striped table-hover align-middle">
  <thead class="table-dark">
  <tr>
    <th>ID</th>
    <th>课程</th>
    <th>申请教师</th>
    <th>申请理由</th>
    <th>申请时间</th>
    <th>状态</th>
    <th style="width: 170px;">操作</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="a" items="${applyList}">
    <tr>
      <td>${a.id}</td>
      <td>${a.courseName}</td>
      <td>${a.teacherName}</td>
      <td><c:out value="${a.reason}"/></td>
      <td>${a.applyTime}</td>
      <td>
        <c:choose>
          <c:when test="${a.status eq 'pending'}"><span class="badge bg-warning text-dark">待审批</span></c:when>
          <c:when test="${a.status eq 'approved'}"><span class="badge bg-success">已通过</span></c:when>
          <c:when test="${a.status eq 'rejected'}"><span class="badge bg-secondary">已驳回</span></c:when>
          <c:otherwise><span class="badge bg-light text-dark">${a.status}</span></c:otherwise>
        </c:choose>
      </td>
      <td>
        <a href="${pageContext.request.contextPath}/admin/approval?method=approve&applyId=${a.id}&courseId=${a.courseId}"
           class="btn btn-sm btn-success"
           onclick="return confirm('通过后该课程的成绩会被解锁为草稿状态，教师可以重新录入。确定通过吗？');">通过</a>
        <a href="${pageContext.request.contextPath}/admin/approval?method=reject&applyId=${a.id}&courseId=${a.courseId}"
           class="btn btn-sm btn-outline-secondary"
           onclick="return confirm('确定驳回这条申请吗？');">驳回</a>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty applyList}">
    <tr><td colspan="7" class="text-center text-muted py-4">当前没有待审批的成绩修改申请</td></tr>
  </c:if>
  </tbody>
</table>
</body>
</html>