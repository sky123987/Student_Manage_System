<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>管理员控制台 - 成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jsp" %>

<div class="wrapper">

  <div class="content">
    <h3>系统概览</h3>
    <hr>
    <%@ include file="/WEB-INF/jspf/msg.jsp" %>

    <div class="row g-3 mt-2">
      <div class="col-md-3">
        <div class="card border-primary h-100">
          <div class="card-body">
            <div class="text-muted small">学生总数</div>
            <div class="fs-2 fw-bold text-primary">${studentCount}</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-success h-100">
          <div class="card-body">
            <div class="text-muted small">教师总数</div>
            <div class="fs-2 fw-bold text-success">${teacherCount}</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-info h-100">
          <div class="card-body">
            <div class="text-muted small">课程总数</div>
            <div class="fs-2 fw-bold text-info">${courseCount}</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-secondary h-100">
          <div class="card-body">
            <div class="text-muted small">班级总数</div>
            <div class="fs-2 fw-bold text-secondary">${classCount}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mt-1">
      <div class="col-md-4">
        <div class="card h-100">
          <div class="card-body">
            <div class="text-muted small">系统账号</div>
            <div class="fs-2 fw-bold">${userCount}</div>
            <a href="${pageContext.request.contextPath}/admin/user?method=list" class="btn btn-sm btn-outline-primary mt-2">账号管理</a>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card h-100">
          <div class="card-body">
            <div class="text-muted small">待审批的成绩修改申请</div>
            <div class="fs-2 fw-bold text-warning">${approvalCount}</div>
            <a href="${pageContext.request.contextPath}/admin/approval?method=list" class="btn btn-sm btn-outline-warning mt-2">去审批</a>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card h-100">
          <div class="card-body">
            <div class="text-muted small">待审批的补考申请</div>
            <div class="fs-2 fw-bold text-danger">${makeupCount}</div>
            <a href="${pageContext.request.contextPath}/admin/makeup?method=list" class="btn btn-sm btn-outline-danger mt-2">去审批</a>
          </div>
        </div>
      </div>
    </div>

    <div class="card mt-4">
      <div class="card-header bg-info text-white">管理员可以做什么</div>
      <div class="card-body">
        <ul class="mb-0">
          <li>账号管理：新增/删除账号、重置密码、调整角色</li>
          <li>学生与教师档案：维护学号、工号、班级、联系方式</li>
          <li>课程管理：分配授课教师，配置成绩计算模板</li>
          <li>成绩统计：按班级看排名与平均分</li>
          <li>审批中心：处理教师成绩修改申请与学生补考申请</li>
          <li>权限管理：按角色开关每个功能点的访问权限</li>
        </ul>
      </div>
    </div>
  </div>
</div>
</body>
</html>