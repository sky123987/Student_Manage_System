<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>成绩录入 - 成绩管理系统</title>
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teacher.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jsp" %>
<div class="wrapper">
  <div class="content">
    <%@ include file="/WEB-INF/jspf/msg.jsp" %>

    <div class="d-flex justify-content-between align-items-center mb-2">
      <h4 class="mb-0">
        成绩录入：<c:out value="${course.courseName}"/>
        <span class="text-muted fs-6">（平时 ${template.dailyRatio}% + 期末 ${template.finalRatio}%）</span>
      </h4>
      <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/teacher/course?method=list">返回课程列表</a>
    </div>

    <div class="d-flex flex-wrap align-items-center gap-2 mb-3">
      <a class="btn btn-sm btn-outline-success"
         href="${pageContext.request.contextPath}/teacher/grade?method=export&courseId=${courseId}">
        <i class="bi bi-file-earmark-excel"></i> 导出成绩单
      </a>
      <a class="btn btn-sm btn-outline-primary"
         href="${pageContext.request.contextPath}/teacher/grade?method=stats&courseId=${courseId}">
        <i class="bi bi-bar-chart"></i> 成绩统计
      </a>
      <a class="btn btn-sm btn-outline-secondary"
         href="${pageContext.request.contextPath}/teacher/roster?method=list&courseId=${courseId}">
        <i class="bi bi-people"></i> 选课名单
      </a>
      <c:if test="${not isSubmitted}">
        <a class="btn btn-sm btn-outline-dark"
           href="${pageContext.request.contextPath}/teacher/grade?method=template&courseId=${courseId}">
          <i class="bi bi-download"></i> 下载导入模板
        </a>
        <form class="d-flex align-items-center gap-2 mb-0"
              action="${pageContext.request.contextPath}/teacher/grade?method=importGrades&courseId=${courseId}"
              method="post" enctype="multipart/form-data">
          <input type="file" name="file" class="form-control form-control-sm" style="max-width: 260px;" required>
          <button type="submit" class="btn btn-sm btn-outline-warning">
            <i class="bi bi-upload"></i> 批量导入
          </button>
        </form>
      </c:if>
    </div>

    <input type="hidden" id="dailyRatio" value="${template.dailyRatio / 100.0}">
    <input type="hidden" id="finalRatio" value="${template.finalRatio / 100.0}">

    <form id="gradeForm" action="${pageContext.request.contextPath}/teacher/grade?method=save" method="post">
      <input type="hidden" name="courseId" value="${courseId}">
      <input type="hidden" name="status" id="formStatus" value="draft">

      <div class="table-responsive">
        <table class="table table-bordered table-hover text-center align-middle">
          <thead class="table-primary">
          <tr>
            <th>学号</th>
            <th>姓名</th>
            <c:forEach items="${templateItems}" var="item">
              <th>${item.itemName}<br><small class="text-muted">(${item.itemRatio}%)</small></th>
            </c:forEach>
            <th>期末成绩<br><small class="text-muted">(${template.finalRatio}%)</small></th>
            <th>总评成绩</th>
            <th>绩点</th>
            <th>状态</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${gradeList}" var="g">
            <tr>
              <td>
                ${g.studentNumber}
                <input type="hidden" name="studentId" value="${g.studentId}">
                <input type="hidden" name="dailyJson" id="dailyJson_${g.studentId}">
                <input type="hidden" id="dailyJsonStore_${g.studentId}"
                       value="<c:out value='${g.dailyScoreJson}'/>">
              </td>
              <td><c:out value="${g.studentName}"/></td>

              <c:forEach items="${templateItems}" var="item">
                <td>
                  <input type="number" step="0.1" min="0" max="100"
                         class="form-control form-control-sm item-score"
                         data-student-id="${g.studentId}"
                         data-item-name="${item.itemName}"
                         data-item-ratio="${item.itemRatio / 100.0}"
                         oninput="calcRow(${g.studentId})" ${isSubmitted ? 'disabled' : ''}>
                </td>
              </c:forEach>

              <td>
                <input type="number" step="0.1" min="0" max="100"
                       class="form-control form-control-sm final-score"
                       id="final_${g.studentId}"
                       name="finalScore"
                       value="${g.finalExamScore}"
                       oninput="calcRow(${g.studentId})" ${isSubmitted ? 'disabled' : ''}>
              </td>

              <td><input type="number" class="form-control form-control-sm bg-light"
                         id="total_${g.studentId}" readonly></td>
              <td><input type="number" class="form-control form-control-sm bg-light"
                         id="gpa_${g.studentId}" readonly></td>
              <td>
                <c:choose>
                  <c:when test="${g.status eq 'submitted'}"><span class="badge bg-success">已提交</span></c:when>
                  <c:when test="${g.status eq 'draft'}"><span class="badge bg-warning text-dark">草稿</span></c:when>
                  <c:otherwise><span class="badge bg-light text-dark">未录入</span></c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty gradeList}">
            <tr><td colspan="9" class="text-center text-muted py-4">
              本课程还没有学生选课，无法录入成绩
            </td></tr>
          </c:if>
          </tbody>
        </table>
      </div>

      <div class="mt-3 d-flex justify-content-between align-items-center">
        <div class="d-flex align-items-center">
          <c:choose>
            <c:when test="${isSubmitted}">
              <div class="alert alert-info mb-0 py-2 me-3">
                <i class="bi bi-lock-fill"></i> 该课程成绩已提交锁定，需申请修改后由管理员解锁。
              </div>
              <button type="button" class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#applyModal">
                <i class="bi bi-pencil-square"></i> 申请修改成绩
              </button>
            </c:when>
            <c:otherwise>
              <c:if test="${not empty gradeList}">
                <button type="button" class="btn btn-secondary" onclick="submitForm('draft')">保存草稿</button>
                <button type="button" class="btn btn-success" onclick="submitForm('submitted')">正式提交成绩</button>
              </c:if>
            </c:otherwise>
          </c:choose>
        </div>
        <div class="text-muted small">
          总评 = 各平时分项 × 分项比例 + 期末成绩 × 期末比例（后端会重新计算并校验）
        </div>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="applyModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="${pageContext.request.contextPath}/teacher/apply?method=submit" method="post">
        <input type="hidden" name="courseId" value="${courseId}">
        <div class="modal-header">
          <h5 class="modal-title">申请修改成绩</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <label class="form-label">请填写修改理由：</label>
          <textarea name="reason" class="form-control" rows="3" required></textarea>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="submit" class="btn btn-primary">提交申请</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
  window.onload = function () {
    var studentIds = [];
    document.querySelectorAll(".item-score").forEach(function (input) {
      var sid = input.getAttribute("data-student-id");
      if (studentIds.indexOf(sid) === -1) { studentIds.push(sid); }
    });

    studentIds.forEach(function (studentId) {
      var jsonStore = document.getElementById("dailyJsonStore_" + studentId);
      if (jsonStore && jsonStore.value) {
        try {
          var dailyData = JSON.parse(jsonStore.value);
          document.querySelectorAll('.item-score[data-student-id="' + studentId + '"]').forEach(function (input) {
            var itemName = input.getAttribute("data-item-name");
            if (dailyData[itemName] !== undefined && dailyData[itemName] !== null) {
              input.value = dailyData[itemName];
            }
          });
        } catch (e) { console.error("平时成绩 JSON 解析失败", e); }
      }
      calcRow(studentId);
    });
  };

  function calcRow(studentId) {
    var dailyRatio = parseFloat(document.getElementById("dailyRatio").value) || 0;
    var finalRatio = parseFloat(document.getElementById("finalRatio").value) || 0;

    var dailySum = 0;
    var dailyJsonObj = {};

    document.querySelectorAll('.item-score[data-student-id="' + studentId + '"]').forEach(function (input) {
      var score = parseFloat(input.value);
      if (isNaN(score)) { score = 0; }
      var itemRatio = parseFloat(input.getAttribute("data-item-ratio")) || 0;
      var itemName = input.getAttribute("data-item-name");
      dailySum += score * itemRatio;
      dailyJsonObj[itemName] = score;
    });

    document.getElementById("dailyJson_" + studentId).value = JSON.stringify(dailyJsonObj);

    var finalInput = document.getElementById("final_" + studentId);
    var finalScore = parseFloat(finalInput ? finalInput.value : 0);
    if (isNaN(finalScore)) { finalScore = 0; }

    var total = (dailySum * dailyRatio) + (finalScore * finalRatio);
    total = Math.round(total * 100) / 100;

    var gpa = total >= 60 ? (total - 50) / 10.0 : 0;
    gpa = Math.round(gpa * 100) / 100;

    document.getElementById("total_" + studentId).value = total;
    document.getElementById("gpa_" + studentId).value = gpa;
  }

  function submitForm(status) {
    if (status === 'submitted') {
      if (!confirm('正式提交后成绩将锁定，如需修改需向管理员申请，确认提交吗？')) { return; }
    } else {
      if (!confirm('确认保存为草稿吗？')) { return; }
    }
    document.getElementById("formStatus").value = status;
    document.getElementById("gradeForm").submit();
  }
</script>
<script>
  // 批量导入前先在前端挡一下格式，省得传上去才发现不是 Excel
  $(function () {
    $('form[enctype="multipart/form-data"]').on('submit', function (e) {
      var input = this.querySelector('input[type=file]');
      if (!input || !input.files || input.files.length === 0) {
        alert('请先选择要导入的 Excel 文件');
        e.preventDefault();
        return;
      }
      if (!/\.(xlsx|xls)$/i.test(input.files[0].name)) {
        alert('只支持 .xlsx 或 .xls 格式的 Excel 文件');
        e.preventDefault();
      }
    });
  });
</script>
</body>
</html>
