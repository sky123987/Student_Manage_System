<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>成绩统计 - 成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jsp" %>
<div class="wrapper">
  <div class="content">
    <%@ include file="/WEB-INF/jspf/msg.jsp" %>

    <div class="d-flex justify-content-between align-items-center mb-2">
      <h4 class="mb-0">成绩统计：<c:out value="${course.courseName}"/></h4>
      <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/teacher/course?method=list">返回课程列表</a>
    </div>
    <hr>

    <div class="row g-3 mb-4">
      <div class="col-md-3">
        <div class="card text-bg-primary"><div class="card-body">
          <div class="fs-3 fw-bold">${stats.avgScore}</div><div>平均分</div>
        </div></div>
      </div>
      <div class="col-md-3">
        <div class="card text-bg-success"><div class="card-body">
          <div class="fs-3 fw-bold">${stats.maxScore}</div><div>最高分</div>
        </div></div>
      </div>
      <div class="col-md-3">
        <div class="card text-bg-danger"><div class="card-body">
          <div class="fs-3 fw-bold">${stats.minScore}</div><div>最低分</div>
        </div></div>
      </div>
      <div class="col-md-3">
        <div class="card text-bg-info"><div class="card-body">
          <div class="fs-3 fw-bold">${stats.passRate}%</div><div>及格率（共 ${stats.totalCount} 人）</div>
        </div></div>
      </div>
    </div>

    <div class="card">
      <div class="card-header">分数段分布</div>
      <div class="card-body">
        <c:set var="segTotal" value="${empty stats ? 0 : stats.totalCount}" />
        <div class="d-flex align-items-center mb-2">
          <div style="width: 90px;">90-100</div>
          <div class="progress flex-grow-1 me-3" style="height: 22px;">
            <div class="progress-bar bg-success"
                 style="width: ${segTotal gt 0 ? (stats.count90to100 * 100 / segTotal) : 0}%;"></div>
          </div>
          <div style="width: 60px;" class="text-end">${stats.count90to100} 人</div>
        </div>
        <div class="d-flex align-items-center mb-2">
          <div style="width: 90px;">80-89</div>
          <div class="progress flex-grow-1 me-3" style="height: 22px;">
            <div class="progress-bar bg-primary"
                 style="width: ${segTotal gt 0 ? (stats.count80to89 * 100 / segTotal) : 0}%;"></div>
          </div>
          <div style="width: 60px;" class="text-end">${stats.count80to89} 人</div>
        </div>
        <div class="d-flex align-items-center mb-2">
          <div style="width: 90px;">70-79</div>
          <div class="progress flex-grow-1 me-3" style="height: 22px;">
            <div class="progress-bar bg-info"
                 style="width: ${segTotal gt 0 ? (stats.count70to79 * 100 / segTotal) : 0}%;"></div>
          </div>
          <div style="width: 60px;" class="text-end">${stats.count70to79} 人</div>
        </div>
        <div class="d-flex align-items-center mb-2">
          <div style="width: 90px;">60-69</div>
          <div class="progress flex-grow-1 me-3" style="height: 22px;">
            <div class="progress-bar bg-warning"
                 style="width: ${segTotal gt 0 ? (stats.count60to69 * 100 / segTotal) : 0}%;"></div>
          </div>
          <div style="width: 60px;" class="text-end">${stats.count60to69} 人</div>
        </div>
        <div class="d-flex align-items-center mb-2">
          <div style="width: 90px;">60 以下</div>
          <div class="progress flex-grow-1 me-3" style="height: 22px;">
            <div class="progress-bar bg-danger"
                 style="width: ${segTotal gt 0 ? (stats.countBelow60 * 100 / segTotal) : 0}%;"></div>
          </div>
          <div style="width: 60px;" class="text-end">${stats.countBelow60} 人</div>
        </div>
        <c:if test="${empty stats or stats.totalCount eq 0}">
          <div class="text-muted">本课程还没有已提交的成绩，暂时无法统计。</div>
        </c:if>
      </div>
    </div>

    <div class="mt-3">
      <a class="btn btn-outline-primary"
         href="${pageContext.request.contextPath}/teacher/grade?method=export&courseId=${course.id}">
        <i class="bi bi-file-earmark-excel"></i> 导出成绩单（Excel）
      </a>
      <a class="btn btn-outline-secondary"
         href="${pageContext.request.contextPath}/teacher/grade?method=list&courseId=${course.id}">去录入成绩</a>
    </div>
  </div>
</div>
</body>
</html>