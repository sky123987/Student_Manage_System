package com.lut.grade.util;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel 导出的公共逻辑。
 * 之前教师端导出成绩单是直接写在 Servlet 里的，管理员导出班级报表、
 * 学生导出个人成绩单又要写一遍，所以抽出来，统一表头样式和下载响应头。
 */
public final class ExcelWriter {

    private ExcelWriter() {
    }

    /**
     * 把一张二维表写成 xlsx 并直接写回浏览器。
     *
     * @param resp      响应对象
     * @param fileName  下载文件名（不含扩展名），会做 URL 编码兼容中文
     * @param sheetName 工作表名
     * @param title     大标题，为空则不加标题行
     * @param headers   表头
     * @param rows      数据行，每行长度应与表头一致
     */
    public static void write(HttpServletResponse resp, String fileName, String sheetName,
                             String title, String[] headers, List<String[]> rows) throws Exception {

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet(sheetName);
            CellStyle headStyle = wb.createCellStyle();
            Font headFont = wb.createFont();
            headFont.setBold(true);
            headStyle.setFont(headFont);
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headStyle.setBorderBottom(BorderStyle.THIN);

            int rowIndex = 0;
            if (title != null && !title.isEmpty()) {
                Row titleRow = sheet.createRow(rowIndex++);
                Cell cell = titleRow.createCell(0);
                cell.setCellValue(title);
                CellStyle titleStyle = wb.createCellStyle();
                Font titleFont = wb.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 14);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        titleRow.getRowNum(), titleRow.getRowNum(), 0,
                        Math.max(0, headers.length - 1)));
                rowIndex++;
            }

            Row head = sheet.createRow(rowIndex++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = head.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headStyle);
                sheet.setColumnWidth(i, 18 * 256);
            }

            for (String[] row : rows) {
                Row r = sheet.createRow(rowIndex++);
                for (int i = 0; i < headers.length; i++) {
                    String v = (row != null && i < row.length) ? row[i] : "";
                    r.createCell(i).setCellValue(v == null ? "" : v);
                }
            }

            String encoded = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8.name())
                    .replace("+", "%20");
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded);
            try (OutputStream out = resp.getOutputStream()) {
                wb.write(out);
                out.flush();
            }
        }
    }

    /** 空值显示成“—”，避免表格里出现 null。 */
    public static String nvl(Object v) {
        return v == null ? "—" : String.valueOf(v);
    }
}