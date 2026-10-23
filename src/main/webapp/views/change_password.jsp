<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>修改密码 - 学生成绩管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css?v=20261023c">
</head>
<body class="auth-body">
<main class="auth-shell">
    <div class="auth-brand">
        <div class="auth-logo"><i class="bi bi-shield-lock-fill"></i></div>
        <h1>学生成绩管理系统</h1>
        <p>Account Security</p>
    </div>

    <section class="auth-card">
        <div class="auth-card-head">
            <h2>修改密码</h2>
            <p>为了账号安全，请定期更换密码</p>
        </div>

        <c:if test="${not empty msg}">
            <div class="alert alert-${empty param.type ? 'danger' : param.type} auth-alert" role="alert">
                <i class="bi bi-exclamation-circle me-1"></i>${msg}
            </div>
        </c:if>

        <form id="changePwdForm" action="${pageContext.request.contextPath}/user?method=changePassword"
              method="post" onsubmit="return validateNewPassword()">
            <div class="mb-3">
                <label class="form-label" for="oldPwd">原密码</label>
                <div class="input-group auth-input">
                    <span class="input-group-text"><i class="bi bi-lock"></i></span>
                    <input type="password" class="form-control" id="oldPwd" name="oldPwd"
                           placeholder="请输入原密码" autocomplete="current-password" required>
                    <button class="btn btn-eye" type="button" data-toggle-password="oldPwd" aria-label="显示密码">
                        <i class="bi bi-eye"></i>
                    </button>
                </div>
            </div>

            <div class="mb-2">
                <label class="form-label" for="newPwd">新密码</label>
                <div class="input-group auth-input">
                    <span class="input-group-text"><i class="bi bi-key"></i></span>
                    <input type="password" class="form-control" id="newPwd" name="newPwd"
                           placeholder="请输入新密码" autocomplete="new-password" required>
                    <button class="btn btn-eye" type="button" data-toggle-password="newPwd" aria-label="显示密码">
                        <i class="bi bi-eye"></i>
                    </button>
                </div>
            </div>

            <div class="form-text mb-3">密码至少 6 位，且必须同时包含字母和数字，例如 abc123</div>

            <div class="mb-3">
                <label class="form-label" for="confirmPwd">确认新密码</label>
                <div class="input-group auth-input">
                    <span class="input-group-text"><i class="bi bi-shield-check"></i></span>
                    <input type="password" class="form-control" id="confirmPwd" name="confirmPwd"
                           placeholder="请再次输入新密码" autocomplete="new-password" required>
                    <button class="btn btn-eye" type="button" data-toggle-password="confirmPwd" aria-label="显示密码">
                        <i class="bi bi-eye"></i>
                    </button>
                </div>
            </div>

            <button type="submit" class="btn btn-auth-primary w-100">确认修改</button>
            <a class="btn btn-outline-secondary w-100 mt-2" href="javascript:history.back()">返 回</a>
        </form>
    </section>

    <footer class="auth-footer">© 学生成绩管理系统 · 教务信息化平台</footer>
</main>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js?v=20261023c"></script>
<script src="${pageContext.request.contextPath}/js/auth.js?v=20261023c"></script>
<script>
    function validateNewPassword() {
        var pwd = document.getElementById('newPwd').value;
        var confirmPwd = document.getElementById('confirmPwd').value;
        if (pwd.length < 6) {
            alert('新密码长度不能少于 6 位');
            return false;
        }
        if (!/[A-Za-z]/.test(pwd) || !/[0-9]/.test(pwd)) {
            alert('新密码必须同时包含字母和数字，例如 abc123');
            return false;
        }
        if (pwd !== confirmPwd) {
            alert('两次输入的新密码不一致');
            return false;
        }
        return true;
    }
</script>
</body>
</html>