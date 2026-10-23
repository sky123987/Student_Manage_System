<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>注册 - 学生成绩管理系统</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container" style="max-width: 520px; margin-top: 6vh;">
  <h2 class="mb-3">新用户注册</h2>

  <c:if test="${not empty msg or not empty param.msg}">
    <div class="alert alert-${empty param.type ? 'danger' : param.type}" role="alert">
      <c:out value="${empty msg ? param.msg : msg}"/>
    </div>
  </c:if>

  <div class="card shadow-sm">
    <div class="card-body p-4">
      <form id="registerForm" action="${pageContext.request.contextPath}/user?method=register" method="post" onsubmit="return validateRegisterForm()">
        <div class="mb-3">
          <label class="form-label">账号 <span class="text-danger">*</span></label>
          <input type="text" name="username" class="form-control" required minlength="3" maxlength="32">
          <div class="form-text">3~32 位，登录时使用</div>
        </div>

        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">密码 <span class="text-danger">*</span></label>
            <input type="password" name="password" id="password" class="form-control" required>
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">确认密码 <span class="text-danger">*</span></label>
            <input type="password" name="confirmPassword" id="confirmPassword" class="form-control" required>
          </div>
        </div>
        <div class="form-text mb-3">密码至少 6 位，且必须同时包含字母和数字，例如 abc123</div>

        <div class="mb-3">
          <label class="form-label">注册角色 <span class="text-danger">*</span></label>
          <select name="role" class="form-select" id="roleSelect" onchange="syncNumberLabel()">
            <option value="student">学生</option>
            <option value="teacher">教师</option>
          </select>
          <div class="form-text">管理员账号只能由管理员在后台创建</div>
        </div>

        <div class="mb-3">
          <label class="form-label">真实姓名 <span class="text-danger">*</span></label>
          <input type="text" name="realName" class="form-control" required maxlength="32">
        </div>

        <div class="mb-3">
          <label class="form-label" id="numberLabel">学号 <span class="text-danger">*</span></label>
          <input type="text" name="number" class="form-control" required maxlength="32">
          <div class="form-text">学号/工号需要唯一，重复注册会被拒绝</div>
        </div>

        <button type="submit" class="btn btn-primary w-100">立即注册</button>
      </form>
    </div>
  </div>

  <div class="text-center mt-3 mb-5">
    <a href="${pageContext.request.contextPath}/views/login.jsp">已有账号？返回登录</a>
  </div>
</div>

<script>
  // 注册密码的前端校验：与后端 PasswordPolicy 规则保持一致
  // 不符合时用 alert 弹出提示，优于浏览器原生的 minlength 气泡（后者在部分浏览器里显示不明显）
  function validateRegisterForm() {
    var pwd = document.getElementById('password').value;
    var confirmPwd = document.getElementById('confirmPassword').value;
    if (pwd.length < 6) {
      alert('密码长度不能少于 6 位');
      return false;
    }
    if (!/[A-Za-z]/.test(pwd) || !/[0-9]/.test(pwd)) {
      alert('密码必须同时包含字母和数字，例如 abc123');
      return false;
    }
    if (pwd !== confirmPwd) {
      alert('两次输入的密码不一致');
      return false;
    }
    return true;
  }

  function syncNumberLabel() {
    var role = document.getElementById('roleSelect').value;
    document.getElementById('numberLabel').innerHTML =
        (role === 'teacher' ? '工号' : '学号') + ' <span class="text-danger">*</span>';
  }
  syncNumberLabel();
</script>
</body>
</html>