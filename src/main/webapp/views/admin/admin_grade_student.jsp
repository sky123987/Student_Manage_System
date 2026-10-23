<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>学生成绩排名 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css?v=20261023c">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h4 class="mb-0">学生成绩排名<c:if test="${not empty schoolClass}"> - <c:out value="${schoolClass.className}"/></c:if></h4>
  <div>
    <a href="${pageContext.request.contextPath}/admin/grade?method=exportClass&classId=${classId}"
       class="btn btn-outline-success">
      <i class="bi bi-file-earmark-excel"></i> 导出本班成绩
    </a>
    <a href="${pageContext.request.contextPath}/admin/grade?method=classStats" class="btn btn-outline-primary">返回班级统计</a>
    <a href="${pageContext.request.contextPath}/admin/dashboard?method=index" class="btn btn-outline-dark">返回控制台</a>
  </div>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<form class="row g-2 align-items-end mb-3" method="get"
      action="${pageContext.request.contextPath}/admin/grade">
  <input type="hidden" name="method" value="studentRanks">
  <div class="col-auto">
    <label class="form-label mb-1">切换班级</label>
    <select name="classId" class="form-select">
      <c:forEach var="cs" items="${classStats}">
        <option value="${cs.classId}" ${cs.classId eq classId ? 'selected' : ''}>
          <c:out value="${cs.className}"/>
        </option>
      </c:forEach>
    </select>
  </div>
  <div class="col-auto">
    <button type="submit" class="btn btn-primary">查看</button>
  </div>
</form>

<table class="table table-striped table-hover align-middle">
  <thead class="table-dark">
  <tr>
    <th>排名</th>
    <th>学号</th>
    <th>姓名</th>
    <th>课程</th>
    <th>总评</th>
    <th>绩点</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="g" items="${studentRanks}" varStatus="st">
    <tr>
      <td>${st.index + 1}</td>
      <td>${g.studentNumber}</td>
      <td><c:out value="${g.studentName}"/></td>
      <td><c:out value="${empty g.courseName ? '—' : g.courseName}"/></td>
      <td>
        <c:choose>
          <c:when test="${empty g.totalScore}"><span class="text-muted">未录入</span></c:when>
          <c:otherwise>
            <span class="fw-bold ${g.totalScore lt 60 ? 'text-danger' : 'text-success'}">
              <c:out value="${g.totalScore}"/>
            </span>
          </c:otherwise>
        </c:choose>
      </td>
      <td><c:out value="${empty g.gpa ? '—' : g.gpa}"/></td>
    </tr>
  </c:forEach>
  <c:if test="${empty studentRanks}">
    <tr><td colspan="6" class="text-center text-muted py-4">该班级还没有成绩数据</td></tr>
  </c:if>
  </tbody>
</table>
</body>
</html>