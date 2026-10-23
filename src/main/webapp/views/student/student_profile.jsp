<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>个人信息 - 成绩管理系统</title>
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
    <h4 class="mb-3">个人信息</h4>

    <div class="card" style="max-width: 720px;">
      <div class="card-body">
        <div class="row mb-3">
          <div class="col-md-6">
            <label class="form-label text-muted">学号</label>
            <input type="text" class="form-control" value="${student.studentNumber}" readonly>
          </div>
          <div class="col-md-6">
            <label class="form-label text-muted">姓名</label>
            <input type="text" class="form-control" value="<c:out value='${student.name}'/>" readonly>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label text-muted">班级</label>
          <input type="text" class="form-control"
                 value="<c:out value='${empty student.className ? "未分班" : student.className}'/>" readonly>
        </div>

        <hr>
        <p class="text-muted small">学号、姓名和班级由管理员维护，如需更正请联系管理员。以下联系方式可以自行修改。</p>

        <form action="${pageContext.request.contextPath}/student/profile?method=save" method="post">
          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">性别</label>
              <select name="gender" class="form-select">
                <option value="男" ${student.gender eq '男' ? 'selected' : ''}>男</option>
                <option value="女" ${student.gender eq '女' ? 'selected' : ''}>女</option>
              </select>
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">联系电话</label>
              <input type="text" name="phone" class="form-control"
                     value="<c:out value='${student.phone}'/>" placeholder="7~15 位数字">
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">邮箱</label>
              <input type="email" name="email" class="form-control"
                     value="<c:out value='${student.email}'/>">
            </div>
          </div>
          <button type="submit" class="btn btn-primary">保存修改</button>
          <a href="${pageContext.request.contextPath}/student/dashboard?method=index" class="btn btn-secondary">返回首页</a>
        </form>
      </div>
    </div>
  </div>
</div>
</body>
</html>