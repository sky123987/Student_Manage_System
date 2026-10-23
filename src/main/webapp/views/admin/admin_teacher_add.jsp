<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>新增教师 - 成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="container" style="max-width: 640px;">
  <h3>新增教师</h3>
  <hr>
  <%@ include file="/WEB-INF/jspf/msg.jsp" %>

  <form action="${pageContext.request.contextPath}/admin/teacher?method=add" method="post">
    <h6 class="text-muted mt-3">登录账号</h6>
    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label">账号</label>
        <input type="text" name="username" class="form-control" value="${teacher.teacherNumber}"
               placeholder="留空则自动使用工号作为账号">
      </div>
      <div class="col-md-6 mb-3">
        <label class="form-label">初始密码</label>
        <input type="text" name="password" class="form-control" value="${defaultPassword}">
      </div>
    </div>

    <h6 class="text-muted mt-2">教师档案</h6>
    <div class="row">
      <div class="col-md-4 mb-3">
        <label class="form-label">工号 <span class="text-danger">*</span></label>
        <input type="text" name="teacherNumber" class="form-control" value="${teacher.teacherNumber}" required>
      </div>
      <div class="col-md-4 mb-3">
        <label class="form-label">姓名 <span class="text-danger">*</span></label>
        <input type="text" name="name" class="form-control" value="${teacher.name}" required>
      </div>
      <div class="col-md-4 mb-3">
        <label class="form-label">职称</label>
        <input type="text" name="title" class="form-control" value="${teacher.title}" placeholder="如：讲师">
      </div>
    </div>

    <button type="submit" class="btn btn-primary">保存</button>
    <a href="${pageContext.request.contextPath}/admin/teacher?method=list" class="btn btn-secondary">返回列表</a>
  </form>
</div>
</body>
</html>