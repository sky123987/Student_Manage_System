<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>我的成绩 - 成绩管理系统</title>
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
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4 class="mb-0">我的成绩与统计</h4>
      <a href="${pageContext.request.contextPath}/student/grade?method=export" class="btn btn-outline-success btn-sm">
        <i class="bi bi-file-earmark-excel"></i> 导出我的成绩单
      </a>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-md-4">
        <div class="card text-bg-info"><div class="card-body">
          <div class="fs-3 fw-bold">${stats.avgScore}</div><div>平均分</div>
        </div></div>
      </div>
      <div class="col-md-4">
        <div class="card text-bg-success"><div class="card-body">
          <div class="fs-3 fw-bold">${stats.totalCredit}</div><div>总学分（及格课程）</div>
        </div></div>
      </div>
      <div class="col-md-4">
        <div class="card text-bg-warning"><div class="card-body">
          <div class="fs-3 fw-bold">${stats.avgGpa}</div><div>平均绩点</div>
        </div></div>
      </div>
    </div>

    <table class="table table-hover table-bordered text-center align-middle">
      <thead class="table-dark">
      <tr>
        <th>课程名称</th>
        <th>学分</th>
        <th>授课教师</th>
        <th>总评</th>
        <th>等级</th>
        <th>绩点</th>
        <th style="width: 200px;">补考</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${gradeList}" var="g">
        <tr>
          <td><c:out value="${g.courseName}"/></td>
          <td><c:out value="${g.credit}"/></td>
          <td><c:out value="${empty g.teacherName ? '—' : g.teacherName}"/></td>
          <td>
            <span class="fw-bold ${g.totalScore lt 60 ? 'text-danger' : 'text-success'}">
              <c:out value="${g.totalScore}"/>
            </span>
          </td>
          <td><c:out value="${g.gradeLevel}"/></td>
          <td><c:out value="${g.gpa}"/></td>
          <td>
            <c:choose>
              <c:when test="${g.totalScore ge 60}">
                <span class="text-muted">已及格</span>
              </c:when>
              <c:when test="${g.makeupStatus eq 'pending'}">
                <span class="badge bg-warning text-dark">补考待审批</span>
              </c:when>
              <c:when test="${g.makeupStatus eq 'approved'}">
                <span class="badge bg-info text-dark">补考申请已通过</span>
                <c:choose>
                  <c:when test="${not empty g.makeupScore}">
                    <br><small class="text-success">补考得分：${g.makeupScore}</small>
                  </c:when>
                  <c:otherwise>
                    <br><small class="text-muted">等待教师录入补考分</small>
                  </c:otherwise>
                </c:choose>
              </c:when>
              <c:when test="${g.makeupStatus eq 'rejected'}">
                <span class="badge bg-danger">补考被驳回</span>
                <a href="${pageContext.request.contextPath}/student/makeup?method=apply&courseId=${g.courseId}"
                   class="btn btn-sm btn-warning mt-1"
                   onclick="return confirm('之前的补考申请被驳回，确认重新申请吗？');">重新申请</a>
              </c:when>
              <c:otherwise>
                <a href="${pageContext.request.contextPath}/student/makeup?method=apply&courseId=${g.courseId}"
                   class="btn btn-sm btn-warning"
                   onclick="return confirm('确认申请补考该课程吗？');">申请补考</a>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty gradeList}">
        <tr><td colspan="7" class="text-center text-muted py-4">暂无成绩数据</td></tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>
