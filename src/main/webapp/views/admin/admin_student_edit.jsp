<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>编辑学生 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css?v=20261023c">
</head>
<body class="p-4">
<div class="container" style="max-width: 720px;">
  <h3>编辑学生</h3>
  <hr>
  <%@ include file="/WEB-INF/jspf/msg.jsp" %>

  <form action="${pageContext.request.contextPath}/admin/student?method=update" method="post">
    <input type="hidden" name="id" value="${student.id}">

    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label">学号 <span class="text-danger">*</span></label>
        <input type="text" name="studentNumber" class="form-control" value="${student.studentNumber}" required>
      </div>
      <div class="col-md-6 mb-3">
        <label class="form-label">姓名 <span class="text-danger">*</span></label>
        <input type="text" name="name" class="form-control" value="${student.name}" required>
      </div>
    </div>

    <div class="row">
      <div class="col-md-4 mb-3">
        <label class="form-label">班级</label>
        <select name="classId" class="form-select">
          <option value="">-- 未分班 --</option>
          <c:forEach var="cl" items="${classList}">
            <option value="${cl.id}" ${student.classId eq cl.id ? 'selected' : ''}>${cl.className}</option>
          </c:forEach>
        </select>
      </div>
      <div class="col-md-4 mb-3">
        <label class="form-label">性别</label>
        <select name="gender" class="form-select">
          <option value="男" ${student.gender eq '女' ? '' : 'selected'}>男</option>
          <option value="女" ${student.gender eq '女' ? 'selected' : ''}>女</option>
        </select>
      </div>
      <div class="col-md-4 mb-3">
        <label class="form-label">电话</label>
        <input type="text" name="phone" class="form-control" value="${student.phone}">
      </div>
    </div>

    <div class="mb-3">
      <label class="form-label">邮箱</label>
      <input type="email" name="email" class="form-control" value="${student.email}">
    </div>

    <button type="submit" class="btn btn-primary">保存修改</button>
    <a href="${pageContext.request.contextPath}/admin/student?method=list" class="btn btn-secondary">返回列表</a>
  </form>
</div>
</body>
</html>