<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>新增课程 - 成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="container" style="max-width: 640px;">
  <h4>新增课程</h4>
  <hr>
  <%@ include file="/WEB-INF/jspf/msg.jsp" %>
  <form action="${pageContext.request.contextPath}/admin/course?method=add" method="post">
    <div class="mb-3">
      <label class="form-label">课程名称 <span class="text-danger">*</span></label>
      <input type="text" name="courseName" class="form-control" required>
    </div>
    <div class="row">
      <div class="col-md-4 mb-3">
        <label class="form-label">学分 <span class="text-danger">*</span></label>
        <input type="number" step="0.5" min="0.5" name="credit" class="form-control" value="2.0" required>
      </div>
      <div class="col-md-4 mb-3">
        <label class="form-label">学时</label>
        <input type="number" min="0" name="courseHours" class="form-control" value="32">
      </div>
      <div class="col-md-4 mb-3">
        <label class="form-label">课程类型</label>
        <select name="courseType" class="form-select">
          <option value="必修">必修</option>
          <option value="选修">选修</option>
        </select>
      </div>
    </div>
    <div class="mb-3">
      <label class="form-label">指派授课教师</label>
      <select name="teacherId" class="form-select">
        <option value="">-- 暂不指派 --</option>
        <c:forEach items="${teacherList}" var="t">
          <option value="${t.id}"><c:out value="${t.name}"/>（<c:out value="${t.teacherNumber}"/>）</option>
        </c:forEach>
      </select>
    </div>
    <button type="submit" class="btn btn-success">保存</button>
    <a href="${pageContext.request.contextPath}/admin/course?method=list" class="btn btn-secondary">返回</a>
  </form>
</div>
</body>
</html>