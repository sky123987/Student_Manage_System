<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>教师名单 - 打印</title>
    <style>
        body { font-family: "Microsoft YaHei", Arial, sans-serif; padding: 20px; color: #000; }
        .print-header { text-align: center; margin-bottom: 30px; }
        .print-header h2 { margin: 0 0 10px 0; font-size: 24px; }
        .print-header p { margin: 5px 0; font-size: 14px; color: #555; }
        table { width: 100%; border-collapse: collapse; font-size: 14px; }
        th, td { border: 1px solid #000; padding: 8px; text-align: center; }
        th { background-color: #f2f2f2; font-weight: bold; }
        @media print { .no-print { display: none; } body { padding: 0; } @page { margin: 1cm; } }
        .print-btn { display: block; margin: 20px auto; padding: 10px 30px; font-size: 16px; background-color: #0d6efd; color: white; border: none; border-radius: 5px; cursor: pointer; }
        .print-btn:hover { background-color: #0b5ed7; }
    </style>
</head>
<body>
<button class="print-btn no-print" onclick="window.print()">🖨️ 点击打印</button>
<div class="print-header">
    <h2>学生成绩管理系统 - 教师名单</h2>
    <p>打印时间：${printDate}</p>
</div>
<table>
    <thead>
    <tr>
        <th>序号</th>
        <th>工号</th>
        <th>姓名</th>
        <th>职称</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${teacherList}" var="t" varStatus="status">
        <tr>
            <td>${status.count}</td>
            <td>${t.teacherNumber}</td>
            <td>${t.name}</td>
            <td>${t.title}</td>
        </tr>
    </c:forEach>
    <c:if test="${empty teacherList}">
        <tr><td colspan="4" style="text-align: center;">暂无教师数据</td></tr>
    </c:if>
    </tbody>
</table>
<script>
    window.onload = function() { setTimeout(function() { window.print(); }, 500); };
</script>
</body>
</html>