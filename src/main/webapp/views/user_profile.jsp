<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>个人资料 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?v=20261023c">
</head>
<body class="p-4">
<div class="container" style="max-width: 820px;">
  <div class="d-flex justify-content-between align-items-center mb-2">
    <h4 class="mb-0">个人资料</h4>
    <div>
      <a class="btn btn-outline-secondary"
         href="${pageContext.request.contextPath}/user?method=changePassword">修改密码</a>
    </div>
  </div>
  <hr>
  <%@ include file="/WEB-INF/jspf/msg.jsp" %>

  <div class="row g-4">
    <div class="col-md-4 text-center">
      <c:choose>
        <c:when test="${not empty profileUser.avatar}">
          <img src="${pageContext.request.contextPath}${profileUser.avatar}"
               class="rounded-circle border mb-3" style="width: 140px; height: 140px; object-fit: cover;" alt="头像">
        </c:when>
        <c:otherwise>
          <div class="rounded-circle bg-secondary text-white d-flex align-items-center justify-content-center mx-auto mb-3"
               style="width: 140px; height: 140px; font-size: 48px;">
            <i class="bi bi-person"></i>
          </div>
        </c:otherwise>
      </c:choose>

      <form action="${pageContext.request.contextPath}/user?method=uploadAvatar"
            method="post" enctype="multipart/form-data">
        <input type="file" name="avatarFile" class="form-control form-control-sm mb-2"
               accept=".jpg,.jpeg,.png,.gif,.bmp,.webp" required>
        <button type="submit" class="btn btn-sm btn-primary w-100">
          <i class="bi bi-upload"></i> 上传头像
        </button>
        <div class="form-text">支持 jpg/png/gif/bmp/webp，最大 2MB</div>
      </form>
    </div>

    <div class="col-md-8">
      <table class="table table-bordered align-middle">
        <tbody>
        <tr>
          <th style="width: 130px;" class="table-light">账号</th>
          <td><c:out value="${profileUser.username}"/></td>
        </tr>
        <tr>
          <th class="table-light">角色</th>
          <td><c:out value="${profileUser.roleName}"/></td>
        </tr>

        <c:if test="${not empty studentProfile}">
          <tr><th class="table-light">学号</th><td><c:out value="${studentProfile.studentNumber}"/></td></tr>
          <tr><th class="table-light">姓名</th><td><c:out value="${studentProfile.name}"/></td></tr>
          <tr><th class="table-light">班级</th>
            <td><c:out value="${empty studentProfile.className ? '未分班' : studentProfile.className}"/></td></tr>
          <tr><th class="table-light">性别</th><td><c:out value="${studentProfile.gender}"/></td></tr>
          <tr><th class="table-light">电话</th><td><c:out value="${studentProfile.phone}"/></td></tr>
          <tr><th class="table-light">邮箱</th><td><c:out value="${studentProfile.email}"/></td></tr>
        </c:if>

        <c:if test="${not empty teacherProfile}">
          <tr><th class="table-light">工号</th><td><c:out value="${teacherProfile.teacherNumber}"/></td></tr>
          <tr><th class="table-light">姓名</th><td><c:out value="${teacherProfile.name}"/></td></tr>
          <tr><th class="table-light">职称</th><td><c:out value="${teacherProfile.title}"/></td></tr>
        </c:if>

        <c:if test="${empty studentProfile and empty teacherProfile}">
          <tr><td colspan="2" class="text-muted">当前账号没有关联的学生/教师档案。</td></tr>
        </c:if>
        </tbody>
      </table>

      <a href="javascript:history.back()" class="btn btn-secondary">返回</a>
    </div>
  </div>
</div>
</body>
</html>