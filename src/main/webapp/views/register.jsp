<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册 - 学生成绩管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css?v=20261023c">
</head>
<body class="auth-body">
<main class="auth-shell auth-shell-wide">
    <div class="auth-brand">
        <div class="auth-logo"><i class="bi bi-mortarboard-fill"></i></div>
        <h1>学生成绩管理系统</h1>
        <p>Student Achievement Management</p>
    </div>

    <section class="auth-card">
        <div class="auth-card-head">
            <h2>创建账号</h2>
            <p>填写基本信息即可注册使用</p>
        </div>

        <c:if test="${not empty msg}">
            <div class="alert alert-${empty param.type ? 'danger' : param.type} auth-alert" role="alert">
                <i class="bi bi-exclamation-circle me-1"></i>${msg}
            </div>
        </c:if>

        <form id="registerForm" action="${pageContext.request.contextPath}/user?method=register"
              method="post" onsubmit="return validateRegisterForm()">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label" for="username">用户名 <span class="text-danger">*</span></label>
                    <div class="input-group auth-input">
                        <span class="input-group-text"><i class="bi bi-person"></i></span>
                        <input type="text" class="form-control" id="username" name="username"
                               value="${param.username}" placeholder="3-32 位用户名"
                               minlength="3" maxlength="32" autocomplete="username" required>
                    </div>
                </div>

                <div class="col-md-6">
                    <label class="form-label" for="roleSelect">角色 <span class="text-danger">*</span></label>
                    <select class="form-select" id="roleSelect" name="role" onchange="syncNumberLabel()" required>
                        <option value="student" ${param.role == 'teacher' ? '' : 'selected'}>学生</option>
                        <option value="teacher" ${param.role == 'teacher' ? 'selected' : ''}>教师</option>
                    </select>
                </div>

                <div class="col-md-6">
                    <label class="form-label" for="password">密码 <span class="text-danger">*</span></label>
                    <div class="input-group auth-input">
                        <span class="input-group-text"><i class="bi bi-lock"></i></span>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="请输入密码" autocomplete="new-password" required>
                        <button class="btn btn-eye" type="button" data-toggle-password="password" aria-label="显示密码">
                            <i class="bi bi-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="col-md-6">
                    <label class="form-label" for="confirmPassword">确认密码 <span class="text-danger">*</span></label>
                    <div class="input-group auth-input">
                        <span class="input-group-text"><i class="bi bi-shield-lock"></i></span>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                               placeholder="请再次输入密码" autocomplete="new-password" required>
                        <button class="btn btn-eye" type="button" data-toggle-password="confirmPassword" aria-label="显示密码">
                            <i class="bi bi-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="col-md-6">
                    <label class="form-label" for="realName">姓名 <span class="text-danger">*</span></label>
                    <div class="input-group auth-input">
                        <span class="input-group-text"><i class="bi bi-person-vcard"></i></span>
                        <input type="text" class="form-control" id="realName" name="realName"
                               value="${param.realName}" placeholder="请输入真实姓名" required>
                    </div>
                </div>

                <div class="col-md-6">
                    <label class="form-label" for="number" id="numberLabel">学号 <span class="text-danger">*</span></label>
                    <div class="input-group auth-input">
                        <span class="input-group-text"><i class="bi bi-credit-card-2-front"></i></span>
                        <input type="text" class="form-control" id="number" name="number"
                               value="${param.number}" placeholder="请输入学号" required>
                    </div>
                </div>
            </div>

            <div class="form-text mb-3 mt-2">密码至少 6 位，且必须同时包含字母和数字，例如 abc123</div>

            <button type="submit" class="btn btn-auth-primary w-100">注 册</button>
        </form>

        <div class="auth-links">
            已有账号？<a href="${pageContext.request.contextPath}/views/login.jsp">返回登录</a>
        </div>
    </section>

    <footer class="auth-footer">© 学生成绩管理系统 · 教务信息化平台</footer>
</main>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js?v=20261023c"></script>
<script src="${pageContext.request.contextPath}/js/auth.js?v=20261023c"></script>
<script>
    function syncNumberLabel() {
        var role = document.getElementById('roleSelect').value;
        var label = document.getElementById('numberLabel');
        var input = document.getElementById('number');
        if (role === 'teacher') {
            label.innerHTML = '工号 <span class="text-danger">*</span>';
            input.placeholder = '请输入工号';
        } else {
            label.innerHTML = '学号 <span class="text-danger">*</span>';
            input.placeholder = '请输入学号';
        }
    }

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

    syncNumberLabel();
</script>
</body>
</html>