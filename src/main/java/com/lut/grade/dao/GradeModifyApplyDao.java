package com.lut.grade.dao;

import com.lut.grade.model.GradeModifyApply;
import com.lut.grade.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeModifyApplyDao {

    // 1. 教师提交申请
    public boolean insertApply(GradeModifyApply apply) {
        String sql = "INSERT INTO grade_modify_apply (course_id, teacher_id, reason, status) VALUES (?, ?, ?, 'pending')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, apply.getCourseId());
            ps.setInt(2, apply.getTeacherId());
            ps.setString(3, apply.getReason());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 2. 管理员查询所有待审批的申请（联查课程名和教师名）
    public List<GradeModifyApply> findPendingApplies() {
        List<GradeModifyApply> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name, t.name AS teacher_name " +
                "FROM grade_modify_apply a " +
                "LEFT JOIN course c ON a.course_id = c.id " +
                "LEFT JOIN teacher t ON a.teacher_id = t.id " +
                "WHERE a.status = 'pending' " +
                "ORDER BY a.apply_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GradeModifyApply apply = new GradeModifyApply();
                apply.setId(rs.getInt("id"));
                apply.setCourseId(rs.getInt("course_id"));
                apply.setTeacherId(rs.getInt("teacher_id"));
                apply.setReason(rs.getString("reason"));
                apply.setStatus(rs.getString("status"));
                apply.setApplyTime(rs.getTimestamp("apply_time"));
                apply.setCourseName(rs.getString("course_name"));
                apply.setTeacherName(rs.getString("teacher_name"));
                list.add(apply);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. 审批操作（同意或驳回）—— 包含事务，同意时需解锁成绩
    public boolean handleApproval(Integer applyId, Integer courseId, String status) {
        Connection conn = null;
        PreparedStatement psUpdateApply = null;
        PreparedStatement psUnlockGrade = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 3.1 更新申请状态
            String sqlUpdate = "UPDATE grade_modify_apply SET status = ? WHERE id = ?";
            psUpdateApply = conn.prepareStatement(sqlUpdate);
            psUpdateApply.setString(1, status);
            psUpdateApply.setInt(2, applyId);
            psUpdateApply.executeUpdate();

            // 3.2 如果是“同意”，将该课程的所有成绩状态改回 'draft'（允许修改）
            if ("approved".equals(status)) {
                String sqlUnlock = "UPDATE grade SET status = 'draft' WHERE course_id = ?";
                psUnlockGrade = conn.prepareStatement(sqlUnlock);
                psUnlockGrade.setInt(1, courseId);
                psUnlockGrade.executeUpdate();
            }

            conn.commit(); // 提交事务
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (psUnlockGrade != null) psUnlockGrade.close(); } catch (SQLException e) {}
            try { if (psUpdateApply != null) psUpdateApply.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
}