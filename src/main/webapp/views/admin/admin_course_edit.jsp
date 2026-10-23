<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>编辑课程 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="container" style="max-width: 640px;">
  <h3>编辑课程</h3>
  <hr>
  <%@ include file="/WEB-INF/jspf/msg.jsp" %>

  <form action="${pageContext.request.contextPath}/admin/course?method=update" method="post">
    <input type="hidden" name="id" value="${course.id}">

    <div class="mb-3">
      <label class="form-label">课程名称</label>
      <input type="text" name="courseName" class="form-control" value="${course.courseName}" required>
    </div>

    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label">学分</label>
        <input type="number" step="0.5" min="0.5" max="20" name="credit" class="form-control"
               value="${course.credit}" required>
      </div>
      <div class="col-md-6 mb-3">
        <label class="form-label">学时</label>
        <input type="number" min="0" max="500" name="courseHours" class="form-control"
               value="${course.courseHours}">
      </div>
    </div>

    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label">课程类型</label>
        <select name="courseType" class="form-select">
          <option value="必修" ${course.courseType eq '必修' ? 'selected' : ''}>必修</option>
          <option value="选修" ${course.courseType eq '选修' ? 'selected' : ''}>选修</option>
          <option value="限选" ${course.courseType eq '限选' ? 'selected' : ''}>限选</option>
        </select>
      </div>
      <div class="col-md-6 mb-3">
        <label class="form-label">任课教师</label>
        <select name="teacherId" class="form-select">
          <option value="">-- 暂不分配 --</option>
          <c:forEach var="t" items="${teacherList}">
            <option value="${t.id}" ${course.teacherId eq t.id ? 'selected' : ''}>${t.name}（${t.teacherNumber}）</option>
          </c:forEach>
        </select>
      </div>
    </div>

    <button type="submit" class="btn btn-primary">保存修改</button>
    <a href="${pageContext.request.contextPath}/admin/course?method=list" class="btn btn-secondary">返回列表</a>
  </form>
</div>
</body>
</html>