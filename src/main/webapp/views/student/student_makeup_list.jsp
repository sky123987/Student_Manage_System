<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>我的补考申请 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/student.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jsp" %>
<div class="wrapper">
  <div class="content">
    <%@ include file="/WEB-INF/jspf/msg.jsp" %>
    <h4 class="mb-3"><i class="bi bi-arrow-repeat"></i> 我的补考申请记录</h4>

    <table class="table table-hover table-bordered text-center align-middle">
      <thead class="table-dark">
      <tr>
        <th>申请时间</th>
        <th>课程名称</th>
        <th>原总评成绩</th>
        <th>申请状态</th>
        <th>补考成绩</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${applyList}" var="a">
        <tr>
          <td>${a.applyTime}</td>
          <td><c:out value="${a.courseName}"/></td>
          <td><span class="fw-bold text-danger"><c:out value="${a.originalScore}"/></span></td>
          <td>
            <c:choose>
              <c:when test="${a.status eq 'pending'}">
                <span class="badge bg-warning text-dark"><i class="bi bi-hourglass-split"></i> 审批中</span>
              </c:when>
              <c:when test="${a.status eq 'approved'}">
                <span class="badge bg-success"><i class="bi bi-check-circle"></i> 已通过</span>
              </c:when>
              <c:when test="${a.status eq 'rejected'}">
                <span class="badge bg-danger"><i class="bi bi-x-circle"></i> 未通过</span>
              </c:when>
              <c:otherwise><span class="badge bg-light text-dark">${a.status}</span></c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:choose>
              <c:when test="${empty a.makeupScore}"><span class="text-muted">--</span></c:when>
              <c:otherwise><span class="fw-bold text-primary">${a.makeupScore}</span></c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty applyList}">
        <tr><td colspan="5" class="text-center text-muted py-4">
          你还没有提交过补考申请。可在「我的成绩」里对不及格课程申请补考。
        </td></tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>