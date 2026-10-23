<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>学生中心 - 成绩管理系统</title>
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

    <c:choose>
      <c:when test="${noProfile}">
        <div class="alert alert-warning">
          你的学生档案还没有建立，请联系管理员在「学生管理」中为你补建档案后再使用学生功能。
        </div>
      </c:when>
      <c:otherwise>
        <h4 class="mb-3">学生中心</h4>

        <div class="mb-3">
          <span class="badge bg-secondary">学号 ${student.studentNumber}</span>
          <span class="badge bg-secondary">姓名 <c:out value="${student.name}"/></span>
          <span class="badge bg-secondary">班级 <c:out value="${empty student.className ? '未分班' : student.className}"/></span>
        </div>

        <div class="row g-3 mb-4">
          <div class="col-md-3">
            <div class="card text-bg-primary"><div class="card-body">
              <div class="fs-3 fw-bold">${stats.avgScore}</div><div>平均分</div>
            </div></div>
          </div>
          <div class="col-md-3">
            <div class="card text-bg-success"><div class="card-body">
              <div class="fs-3 fw-bold">${stats.avgGpa}</div><div>平均绩点</div>
            </div></div>
          </div>
          <div class="col-md-3">
            <div class="card text-bg-info"><div class="card-body">
              <div class="fs-3 fw-bold">${stats.totalCredit}</div><div>已获学分</div>
            </div></div>
          </div>
          <div class="col-md-3">
            <div class="card text-bg-warning"><div class="card-body">
              <div class="fs-3 fw-bold">${courseCount}</div><div>已选课程</div>
            </div></div>
          </div>
        </div>

        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <span>已出成绩的课程</span>
            <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/student/grade?method=list">查看全部成绩</a>
          </div>
          <div class="card-body p-0">
            <table class="table table-striped table-hover align-middle mb-0">
              <thead class="table-dark">
              <tr>
                <th>课程</th>
                <th>学分</th>
                <th>教师</th>
                <th>总评</th>
                <th>绩点</th>
                <th>等级</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach var="g" items="${gradeList}" end="4">
                <tr>
                  <td><c:out value="${g.courseName}"/></td>
                  <td><c:out value="${g.credit}"/></td>
                  <td><c:out value="${empty g.teacherName ? '—' : g.teacherName}"/></td>
                  <td><c:out value="${g.totalScore}"/></td>
                  <td><c:out value="${g.gpa}"/></td>
                  <td>
                    <c:choose>
                      <c:when test="${g.gradeLevel eq '不及格'}"><span class="badge bg-danger">不及格</span></c:when>
                      <c:otherwise><span class="badge bg-success"><c:out value="${g.gradeLevel}"/></span></c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty gradeList}">
                <tr><td colspan="6" class="text-center text-muted py-4">还没有已公布的成绩</td></tr>
              </c:if>
              </tbody>
            </table>
          </div>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</div>
</body>
</html>