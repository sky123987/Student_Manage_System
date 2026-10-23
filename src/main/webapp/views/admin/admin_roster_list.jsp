<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>选课名单管理 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css?v=20261023c" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?v=20261023c">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css?v=20261023c">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h4 class="mb-0">选课名单管理</h4>
  <a href="${pageContext.request.contextPath}/admin/dashboard?method=index" class="btn btn-outline-dark">返回控制台</a>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<form class="row g-2 align-items-end mb-3" method="get"
      action="${pageContext.request.contextPath}/admin/roster">
  <input type="hidden" name="method" value="list">
  <div class="col-md-5">
    <label class="form-label mb-1">选择课程</label>
    <select name="courseId" class="form-select" onchange="this.form.submit()">
      <option value="">-- 请选择课程 --</option>
      <c:forEach var="c" items="${courseList}">
        <option value="${c.id}" ${c.id eq courseId ? 'selected' : ''}>
          <c:out value="${c.courseName}"/>（<c:out value="${empty c.teacherName ? '未安排教师' : c.teacherName}"/>）
        </option>
      </c:forEach>
    </select>
  </div>
  <div class="col-md-3">
    <button type="submit" class="btn btn-primary">查看名单</button>
  </div>
</form>

<c:if test="${not empty course}">
  <div class="d-flex justify-content-between align-items-center mb-2">
    <div>
      <span class="fw-bold"><c:out value="${course.courseName}"/></span>
      <span class="text-muted ms-2">
        任课教师：<c:out value="${empty course.teacherName ? '未安排' : course.teacherName}"/>
        ｜ 学分：<c:out value="${course.credit}"/>
        ｜ 已选人数：<span class="fw-bold">${rosterCount}</span>
      </span>
    </div>
    <div class="input-group" style="max-width: 280px;">
      <span class="input-group-text"><i class="bi bi-search"></i></span>
      <input type="text" id="rosterFilter" class="form-control" placeholder="按学号/姓名/班级筛选">
    </div>
  </div>

  <table class="table table-striped table-hover align-middle" id="rosterTable">
    <thead class="table-dark">
    <tr>
      <th style="width: 70px;">序号</th>
      <th>学号</th>
      <th>姓名</th>
      <th>班级</th>
      <th>成绩状态</th>
      <th style="width: 120px;">操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="r" items="${rosterList}" varStatus="st">
      <tr>
        <td>${st.index + 1}</td>
        <td>${r.studentNumber}</td>
        <td><c:out value="${r.studentName}"/></td>
        <td><c:out value="${empty r.className ? '未分班' : r.className}"/></td>
        <td>
          <c:choose>
            <c:when test="${r.status eq 'submitted'}"><span class="badge bg-success">已提交</span></c:when>
            <c:when test="${r.status eq 'draft'}"><span class="badge bg-warning text-dark">草稿</span></c:when>
            <c:otherwise><span class="badge bg-secondary">未录入</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <a class="btn btn-sm btn-outline-danger"
             href="${pageContext.request.contextPath}/admin/roster?method=remove&courseId=${courseId}&studentId=${r.studentId}"
             onclick="return confirm('确定把该学生移出本课程吗？他的成绩记录也会一并删除。');">移出课程</a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty rosterList}">
      <tr><td colspan="6" class="text-center text-muted py-4">这门课还没有学生选课</td></tr>
    </c:if>
    </tbody>
  </table>
</c:if>

<c:if test="${empty course}">
  <div class="alert alert-secondary">请选择一门课程查看选课名单。</div>
</c:if>

<script src="${pageContext.request.contextPath}/js/jquery.min.js?v=20261023c"></script>
<script>
  // 名单可能很长，用 jQuery 做一个即时筛选：输入学号/姓名/班级立刻过滤表格行
  $(function () {
    $('#rosterFilter').on('input', function () {
      var kw = $.trim($(this).val()).toLowerCase();
      $('#rosterTable tbody tr').each(function () {
        var text = $(this).text().toLowerCase();
        $(this).toggle(kw === '' || text.indexOf(kw) !== -1);
      });
    });
  });
</script>
</body>
</html>