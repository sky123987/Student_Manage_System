<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>新增账号 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="container" style="max-width: 720px;">
  <h3>新增账号</h3>
  <hr>
  <%@ include file="/WEB-INF/jspf/msg.jsp" %>

  <form action="${pageContext.request.contextPath}/admin/user?method=add" method="post">
    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label">账号 <span class="text-danger">*</span></label>
        <input type="text" name="username" class="form-control" value="${username}" required>
      </div>
      <div class="col-md-6 mb-3">
        <label class="form-label">初始密码</label>
        <input type="text" name="password" class="form-control" value="${defaultPassword}">
      </div>
    </div>

    <div class="mb-3">
      <label class="form-label">角色 <span class="text-danger">*</span></label>
      <select name="role" id="roleSelect" class="form-select" onchange="toggleRoleFields()">
        <option value="student" ${role eq 'student' ? 'selected' : ''}>学生</option>
        <option value="teacher" ${role eq 'teacher' ? 'selected' : ''}>教师</option>
        <option value="admin" ${role eq 'admin' ? 'selected' : ''}>管理员</option>
      </select>
      <div class="form-text">管理员账号没有学生/教师档案，只能管理后台</div>
    </div>

    <div id="profileFields">
      <div class="row">
        <div class="col-md-6 mb-3">
          <label class="form-label">真实姓名 <span class="text-danger">*</span></label>
          <input type="text" name="realName" class="form-control" required>
        </div>
        <div class="col-md-6 mb-3">
          <label class="form-label" id="numberLabel">学号 <span class="text-danger">*</span></label>
          <input type="text" name="number" class="form-control" required>
        </div>
      </div>

      <div class="row" id="studentOnly">
        <div class="col-md-4 mb-3">
          <label class="form-label">班级</label>
          <select name="classId" class="form-select">
            <option value="">-- 未分班 --</option>
            <c:forEach var="cl" items="${classList}">
              <option value="${cl.id}">${cl.className}</option>
            </c:forEach>
          </select>
        </div>
        <div class="col-md-4 mb-3">
          <label class="form-label">性别</label>
          <select name="gender" class="form-select">
            <option value="男">男</option>
            <option value="女">女</option>
          </select>
        </div>
        <div class="col-md-4 mb-3">
          <label class="form-label">电话</label>
          <input type="text" name="phone" class="form-control">
        </div>
      </div>

      <div class="row" id="teacherOnly" style="display: none;">
        <div class="col-md-4 mb-3">
          <label class="form-label">职称</label>
          <input type="text" name="title" class="form-control" placeholder="如：讲师">
        </div>
      </div>

      <div class="mb-3" id="emailOnly">
        <label class="form-label">邮箱</label>
        <input type="email" name="email" class="form-control">
      </div>
    </div>

    <button type="submit" class="btn btn-primary">保存</button>
    <a href="${pageContext.request.contextPath}/admin/user?method=list" class="btn btn-secondary">返回列表</a>
  </form>
</div>

<script>
  function toggleRoleFields() {
    var role = document.getElementById('roleSelect').value;
    document.getElementById('profileFields').style.display = (role === 'admin') ? 'none' : '';
    document.getElementById('studentOnly').style.display = (role === 'student') ? '' : 'none';
    document.getElementById('teacherOnly').style.display = (role === 'teacher') ? '' : 'none';
    document.getElementById('emailOnly').style.display = (role === 'student') ? '' : 'none';
    document.getElementById('numberLabel').innerHTML =
        (role === 'teacher' ? '工号' : '学号') + ' <span class="text-danger">*</span>';
  }
  toggleRoleFields();
</script>
</body>
</html>