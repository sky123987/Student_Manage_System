<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
  <title>学生名单 - 打印</title>
  <style>
    /* 打印页面的专属 CSS，不引入 Bootstrap，保持最纯净的排版 */
    body {
      font-family: "Microsoft YaHei", Arial, sans-serif;
      padding: 20px;
      color: #000;
    }
    .print-header {
      text-align: center;
      margin-bottom: 30px;
    }
    .print-header h2 {
      margin: 0 0 10px 0;
      font-size: 24px;
    }
    .print-header p {
      margin: 5px 0;
      font-size: 14px;
      color: #555;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      font-size: 14px;
    }
    th, td {
      border: 1px solid #000;
      padding: 8px;
      text-align: center;
    }
    th {
      background-color: #f2f2f2;
      font-weight: bold;
    }
    /* 打印时隐藏不需要的元素 */
    @media print {
      .no-print {
        display: none;
      }
      body {
        padding: 0;
      }
      @page {
        margin: 1cm;
      }
    }
    /* 屏幕显示时的按钮样式 */
    .print-btn {
      display: block;
      margin: 20px auto;
      padding: 10px 30px;
      font-size: 16px;
      background-color: #0d6efd;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
    }
    .print-btn:hover {
      background-color: #0b5ed7;
    }
  </style>
</head>
<body>

<!-- 打印按钮（在屏幕上显示，打印时隐藏） -->
<button class="print-btn no-print" onclick="window.print()">🖨️ 点击打印</button>

<!-- 打印页眉 -->
<div class="print-header">
  <h2>学生成绩管理系统 - 学生名单</h2>
  <p>打印时间：${printDate}</p>
</div>

<!-- 学生数据表格 -->
<table>
  <thead>
  <tr>
    <th>序号</th>
    <th>学号</th>
    <th>姓名</th>
    <th>班级</th>
    <th>性别</th>
    <th>电话</th>
    <th>邮箱</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${studentList}" var="s" varStatus="status">
    <tr>
      <td>${status.count}</td>
      <td>${s.studentNumber}</td>
      <td>${s.name}</td>
      <td>${s.className}</td>
      <td>${s.gender}</td>
      <td>${s.phone}</td>
      <td>${s.email}</td>
    </tr>
  </c:forEach>
  <c:if test="${empty studentList}">
    <tr><td colspan="7" style="text-align: center;">暂无学生数据</td></tr>
  </c:if>
  </tbody>
</table>

<script>
  // 页面加载完成后，延迟 500ms 自动弹出打印对话框（让样式先渲染完）
  window.onload = function() {
    setTimeout(function() {
      window.print();
    }, 500);
  };
</script>

</body>
</html>