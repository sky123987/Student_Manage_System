<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>编辑教师 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="container" style="max-width: 640px;">
  <h3>编辑教师</h3>
  <hr>
  <%@ include file="/WEB-INF/jspf/msg.jsp" %>

  <form action="${pageContext.request.contextPath}/admin/teacher?method=update" method="post">
    <input type="hidden" name="id" value="${teacher.id}">
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
        <input type="text" name="title" class="form-control" value="${teacher.title}">
      </div>
    </div>
    <button type="submit" class="btn btn-primary">保存修改</button>
    <a href="${pageContext.request.contextPath}/admin/teacher?method=list" class="btn btn-secondary">返回列表</a>
  </form>
</div>
</body>
</html>