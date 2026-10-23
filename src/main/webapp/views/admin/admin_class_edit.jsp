<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>编辑班级 - 成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="container" style="max-width: 560px;">
  <h4>编辑班级</h4>
  <hr>
  <%@ include file="/WEB-INF/jspf/msg.jsp" %>
  <form action="${pageContext.request.contextPath}/admin/class?method=update" method="post">
    <input type="hidden" name="id" value="${schoolClass.id}">
    <div class="mb-3">
      <label class="form-label">班级名称 <span class="text-danger">*</span></label>
      <input type="text" name="className" class="form-control"
             value="<c:out value='${schoolClass.className}'/>" required>
    </div>
    <div class="mb-3">
      <label class="form-label">年级</label>
      <input type="text" name="gradeYear" class="form-control"
             value="<c:out value='${schoolClass.gradeYear}'/>">
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
    <a href="${pageContext.request.contextPath}/admin/class?method=list" class="btn btn-secondary">返回</a>
  </form>
</div>
</body>
</html>