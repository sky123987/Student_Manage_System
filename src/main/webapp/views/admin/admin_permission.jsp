<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>权限管理 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h4 class="mb-0">权限管理 <span class="text-muted fs-6">（共 ${totalPerms} 项权限，按功能模块分组）</span></h4>
  <a href="${pageContext.request.contextPath}/admin/dashboard?method=index" class="btn btn-outline-dark">返回控制台</a>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<ul class="nav nav-tabs" role="tablist">
  <li class="nav-item">
    <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#teacherTab" type="button">教师权限</button>
  </li>
  <li class="nav-item">
    <button class="nav-link" data-bs-toggle="tab" data-bs-target="#studentTab" type="button">学生权限</button>
  </li>
</ul>

<div class="tab-content pt-3">
  <div class="tab-pane fade show active" id="teacherTab">
    <form action="${pageContext.request.contextPath}/admin/permission?method=save" method="post">
      <input type="hidden" name="role" value="teacher">
      <c:forEach var="group" items="${moduleGroups}">
        <div class="card mb-3">
          <div class="card-header py-2"><c:out value="${group.key}"/></div>
          <div class="card-body py-2">
            <c:forEach var="p" items="${group.value}">
              <label class="form-check form-check-inline me-3" style="min-width: 210px;">
                <input class="form-check-input" type="checkbox" name="codes" value="${p.code}"
                       ${matrix['teacher'].contains(p.code) ? 'checked' : ''}>
                <span class="form-check-label"><c:out value="${p.name}"/></span>
              </label>
            </c:forEach>
          </div>
        </div>
      </c:forEach>
      <button type="submit" class="btn btn-primary">保存教师权限</button>
    </form>
  </div>

  <div class="tab-pane fade" id="studentTab">
    <form action="${pageContext.request.contextPath}/admin/permission?method=save" method="post">
      <input type="hidden" name="role" value="student">
      <c:forEach var="group" items="${moduleGroups}">
        <div class="card mb-3">
          <div class="card-header py-2"><c:out value="${group.key}"/></div>
          <div class="card-body py-2">
            <c:forEach var="p" items="${group.value}">
              <label class="form-check form-check-inline me-3" style="min-width: 210px;">
                <input class="form-check-input" type="checkbox" name="codes" value="${p.code}"
                       ${matrix['student'].contains(p.code) ? 'checked' : ''}>
                <span class="form-check-label"><c:out value="${p.name}"/></span>
              </label>
            </c:forEach>
          </div>
        </div>
      </c:forEach>
      <button type="submit" class="btn btn-primary">保存学生权限</button>
    </form>
  </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>