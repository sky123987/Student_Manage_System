package com.lut.grade.dao;

import com.lut.grade.model.CourseGradeTemplate;
import com.lut.grade.model.TemplateItem;
import com.lut.grade.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseGradeTemplateDao {

    /**
     * 1. 根据课程ID查询该课程的模板及子项
     * （用于管理员修改模板，以及教师端获取比例计算成绩）
     */
    public CourseGradeTemplate getTemplateByCourseId(Integer courseId) {
        CourseGradeTemplate template = null;
        String sql = "SELECT * FROM course_grade_template WHERE course_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    template = new CourseGradeTemplate();
                    template.setId(rs.getInt("id"));
                    template.setCourseId(rs.getInt("course_id"));
                    template.setDailyRatio(rs.getInt("daily_ratio"));
                    template.setFinalRatio(rs.getInt("final_ratio"));
                    template.setAllowMakeup(rs.getInt("allow_makeup"));

                    // 查询该模板的子项
                    template.setItemList(getItemsByTemplateId(template.getId(), conn));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return template;
    }

    /**
     * 辅助方法：根据模板ID查询子项列表
     */
    private List<TemplateItem> getItemsByTemplateId(Integer templateId, Connection conn) throws SQLException {
        List<TemplateItem> list = new ArrayList<>();
        String sql = "SELECT * FROM template_item WHERE template_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TemplateItem item = new TemplateItem();
                    item.setId(rs.getInt("id"));
                    item.setTemplateId(rs.getInt("template_id"));
                    item.setItemName(rs.getString("item_name"));
                    item.setItemRatio(rs.getInt("item_ratio"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    /**
     * 2. 保存（或更新）课程模板
     * 逻辑：先删除该课程旧的模板和子项（外键级联删除会自动清理子表），再插入新模板和子项
     * 使用事务保证原子性
     */
    public boolean saveTemplate(CourseGradeTemplate template) {
        Connection conn = null;
        PreparedStatement psDel = null;
        PreparedStatement psInsTemplate = null;
        PreparedStatement psInsItem = null;
        ResultSet rsKeys = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 删除旧的模板（外键 ON DELETE CASCADE 会自动删除 template_item 中的子项）
            String sqlDel = "DELETE FROM course_grade_template WHERE course_id = ?";
            psDel = conn.prepareStatement(sqlDel);
            psDel.setInt(1, template.getCourseId());
            psDel.executeUpdate();

            // 2. 插入新的模板
            String sqlInsTemplate = "INSERT INTO course_grade_template (course_id, daily_ratio, final_ratio, allow_makeup) VALUES (?, ?, ?, ?)";
            psInsTemplate = conn.prepareStatement(sqlInsTemplate, Statement.RETURN_GENERATED_KEYS);
            psInsTemplate.setInt(1, template.getCourseId());
            psInsTemplate.setInt(2, template.getDailyRatio());
            psInsTemplate.setInt(3, template.getFinalRatio());
            psInsTemplate.setInt(4, template.getAllowMakeup());
            psInsTemplate.executeUpdate();

            // 获取刚插入的模板ID（自增主键）
            rsKeys = psInsTemplate.getGeneratedKeys();
            int newTemplateId = 0;
            if (rsKeys.next()) {
                newTemplateId = rsKeys.getInt(1);
            }

            // 3. 插入平时分的所有子项
            if (template.getItemList() != null && !template.getItemList().isEmpty()) {
                String sqlInsItem = "INSERT INTO template_item (template_id, item_name, item_ratio) VALUES (?, ?, ?)";
                psInsItem = conn.prepareStatement(sqlInsItem);
                for (TemplateItem item : template.getItemList()) {
                    psInsItem.setInt(1, newTemplateId);
                    psInsItem.setString(2, item.getItemName());
                    psInsItem.setInt(3, item.getItemRatio());
                    psInsItem.addBatch(); // 批量添加
                }
                psInsItem.executeBatch(); // 执行批量插入
            }

            conn.commit(); // 提交事务
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // 发生异常则回滚
            }
            e.printStackTrace();
        } finally {
            // 释放资源
            try { if (rsKeys != null) rsKeys.close(); } catch (SQLException e) {}
            try { if (psInsItem != null) psInsItem.close(); } catch (SQLException e) {}
            try { if (psInsTemplate != null) psInsTemplate.close(); } catch (SQLException e) {}
            try { if (psDel != null) psDel.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
}