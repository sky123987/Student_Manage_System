<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>修改密码 - 学生成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container" style="max-width: 480px; margin-top: 8vh;">
  <h3 class="mb-3">修改密码</h3>

  <c:if test="${not empty msg or not empty param.msg}">
    <div class="alert alert-${empty param.type ? 'danger' : param.type}" role="alert">
      <c:out value="${empty msg ? param.msg : msg}"/>
    </div>
  </c:if>

  <div class="card shadow-sm">
    <div class="card-body p-4">
      <form id="changePwdForm" action="${pageContext.request.contextPath}/user?method=changePassword" method="post" onsubmit="return validateNewPassword()">
        <div class="mb-3">
          <label class="form-label">旧密码</label>
          <input type="password" name="oldPwd" class="form-control" required>
        </div>
        <div class="mb-3">
          <label class="form-label">新密码</label>
          <input type="password" name="newPwd" id="newPwd" class="form-control" required>
          <div class="form-text">密码至少 6 位，且必须同时包含字母和数字，例如 abc123</div>
        </div>
        <div class="mb-3">
          <label class="form-label">确认新密码</label>
          <input type="password" name="confirmPwd" id="confirmPwd" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary w-100">确认修改</button>
      </form>
    </div>
  </div>

  <div class="text-center mt-3">
    <a href="javascript:history.back()">返回上一页</a>
  </div>
</div>

<script>
  // 与后端 PasswordPolicy 规则保持一致
  function validateNewPassword() {
    var pwd = document.getElementById('newPwd').value;
    if (pwd.length < 6) {
      alert('密码长度不能少于 6 位');
      return false;
    }
    if (!/[A-Za-z]/.test(pwd) || !/[0-9]/.test(pwd)) {
      alert('密码必须同时包含字母和数字，例如 abc123');
      return false;
    }
    if (pwd !== document.getElementById('confirmPwd').value) {
      alert('两次输入的新密码不一致');
      return false;
    }
    return true;
  }
</script>
</body>
</html>