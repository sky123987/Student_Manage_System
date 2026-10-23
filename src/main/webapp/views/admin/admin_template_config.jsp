<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>成绩模板配置 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body class="p-4">
<div class="d-flex justify-content-between align-items-center mb-2">
  <h4 class="mb-0">成绩模板配置：<c:out value="${course.courseName}"/></h4>
  <a href="${pageContext.request.contextPath}/admin/course?method=list" class="btn btn-outline-secondary">返回课程列表</a>
</div>
<hr>
<%@ include file="/WEB-INF/jspf/msg.jsp" %>

<div class="alert alert-info">
  平时成绩比例 + 期末成绩比例必须等于 100%。各平时分项的比例之和必须等于「平时成绩比例」。
  总评 = Σ(分项成绩 × 分项比例%) + 期末成绩 × 期末比例%。
</div>

<form action="${pageContext.request.contextPath}/admin/template?method=save" method="post"
      onsubmit="return checkTotalRatio();">
  <input type="hidden" name="courseId" value="${course.id}">

  <div class="row" style="max-width: 620px;">
    <div class="col-md-4 mb-3">
      <label class="form-label">平时成绩比例（%）</label>
      <input type="number" min="0" max="100" name="dailyRatio" id="dailyRatio"
             class="form-control" value="${empty template ? 40 : template.dailyRatio}" required>
    </div>
    <div class="col-md-4 mb-3">
      <label class="form-label">期末成绩比例（%）</label>
      <input type="number" min="0" max="100" name="finalRatio" id="finalRatio"
             class="form-control" value="${empty template ? 60 : template.finalRatio}" required>
    </div>
    <div class="col-md-4 mb-3">
      <label class="form-label">允许补考</label>
      <select name="allowMakeup" class="form-select">
        <option value="1" ${empty template or template.allowMakeup eq 1 ? 'selected' : ''}>允许</option>
        <option value="0" ${not empty template and template.allowMakeup eq 0 ? 'selected' : ''}>不允许</option>
      </select>
    </div>
  </div>

  <h6 class="mt-2">平时成绩分项</h6>
  <table class="table table-bordered align-middle" style="max-width: 620px;">
    <thead class="table-light">
    <tr>
      <th>分项名称</th>
      <th style="width: 150px;">占比（%）</th>
      <th style="width: 90px;">操作</th>
    </tr>
    </thead>
    <tbody id="itemBody">
    <c:choose>
      <c:when test="${not empty template and not empty template.itemList}">
        <c:forEach var="it" items="${template.itemList}">
          <tr>
            <td><input type="text" name="itemName" class="form-control"
                       value="<c:out value='${it.itemName}'/>" required></td>
            <td><input type="number" min="0" max="100" name="itemRatio"
                       class="form-control item-ratio" value="${it.itemRatio}"
                       oninput="calcItemSum()" required></td>
            <td><button type="button" class="btn btn-sm btn-outline-danger"
                        onclick="removeItem(this)">删除</button></td>
          </tr>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <tr>
          <td><input type="text" name="itemName" class="form-control" value="平时作业" required></td>
          <td><input type="number" min="0" max="100" name="itemRatio"
                     class="form-control item-ratio" value="20" oninput="calcItemSum()" required></td>
          <td><button type="button" class="btn btn-sm btn-outline-danger"
                      onclick="removeItem(this)">删除</button></td>
        </tr>
        <tr>
          <td><input type="text" name="itemName" class="form-control" value="实验/上机" required></td>
          <td><input type="number" min="0" max="100" name="itemRatio"
                     class="form-control item-ratio" value="20" oninput="calcItemSum()" required></td>
          <td><button type="button" class="btn btn-sm btn-outline-danger"
                      onclick="removeItem(this)">删除</button></td>
        </tr>
      </c:otherwise>
    </c:choose>
    </tbody>
  </table>

  <div class="mb-3">
    <button type="button" class="btn btn-sm btn-outline-primary" onclick="addItem()">+ 增加分项</button>
    <span class="ms-3">分项比例合计：<b id="itemSum">0</b>% / 平时比例 <b id="dailyShow">0</b>%</span>
  </div>

  <button type="submit" class="btn btn-success">保存模板</button>
  <a href="${pageContext.request.contextPath}/admin/course?method=list" class="btn btn-secondary">取消</a>
</form>

<script>
  function addItem() {
    var tb = document.getElementById('itemBody');
    var tr = document.createElement('tr');
    tr.innerHTML = '<td><input type="text" name="itemName" class="form-control" placeholder="分项名称" required></td>'
        + '<td><input type="number" min="0" max="100" name="itemRatio" class="form-control item-ratio" value="0" oninput="calcItemSum()" required></td>'
        + '<td><button type="button" class="btn btn-sm btn-outline-danger" onclick="removeItem(this)">删除</button></td>';
    tb.appendChild(tr);
    calcItemSum();
  }

  function removeItem(btn) {
    var tr = btn.closest('tr');
    var tb = document.getElementById('itemBody');
    if (tb.rows.length <= 1) {
      alert('至少保留一个平时成绩分项');
      return;
    }
    tr.remove();
    calcItemSum();
  }

  function calcItemSum() {
    var sum = 0;
    document.querySelectorAll('.item-ratio').forEach(function (input) {
      var v = parseFloat(input.value);
      if (!isNaN(v)) { sum += v; }
    });
    document.getElementById('itemSum').textContent = Math.round(sum * 100) / 100;
    var d = parseFloat(document.getElementById('dailyRatio').value);
    document.getElementById('dailyShow').textContent = isNaN(d) ? 0 : d;
  }

  function checkTotalRatio() {
    var d = parseFloat(document.getElementById('dailyRatio').value) || 0;
    var f = parseFloat(document.getElementById('finalRatio').value) || 0;
    var sum = 0;
    document.querySelectorAll('.item-ratio').forEach(function (input) {
      var v = parseFloat(input.value);
      if (!isNaN(v)) { sum += v; }
    });
    if (Math.abs(d + f - 100) > 0.001) {
      alert('平时比例与期末比例之和必须等于 100%，当前为 ' + (d + f) + '%');
      return false;
    }
    if (Math.abs(sum - d) > 0.001) {
      alert('各平时分项比例之和（' + sum + '%）必须等于平时成绩比例（' + d + '%）');
      return false;
    }
    return true;
  }

  window.onload = function () { calcItemSum(); };
</script>
</body>
</html>