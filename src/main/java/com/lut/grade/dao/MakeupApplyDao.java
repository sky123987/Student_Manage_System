package com.lut.grade.dao;

import com.lut.grade.model.MakeupApply;
import com.lut.grade.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MakeupApplyDao {

    /**
     * 1. 学生提交补考申请
     */
    public boolean insertApply(Integer studentId, Integer courseId) {
        String sql = "INSERT INTO makeup_apply (student_id, course_id, status) VALUES (?, ?, 'pending')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * 2. 管理员查询所有待审批的补考申请
     */
    public List<MakeupApply> findPendingApplies() {
        List<MakeupApply> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS student_name, s.student_number, c.course_name, g.total_score AS original_score " +
                "FROM makeup_apply m " +
                "JOIN student s ON m.student_id = s.id " +
                "JOIN course c ON m.course_id = c.id " +
                "LEFT JOIN grade g ON g.student_id = m.student_id AND g.course_id = m.course_id " +
                "WHERE m.status = 'pending' " +
                "ORDER BY m.apply_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToMakeupApply(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * 3. 管理员审批（同意或驳回）
     */
    public boolean updateStatus(Integer applyId, String status) {
        String sql = "UPDATE makeup_apply SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applyId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * 4. 教师查询自己课程下已审批通过的补考申请
     */
    public List<MakeupApply> findApprovedAppliesByTeacherId(Integer teacherId) {
        List<MakeupApply> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS student_name, s.student_number, c.course_name, g.total_score AS original_score " +
                "FROM makeup_apply m " +
                "JOIN student s ON m.student_id = s.id " +
                "JOIN course c ON m.course_id = c.id " +
                "LEFT JOIN grade g ON g.student_id = m.student_id AND g.course_id = m.course_id " +
                "WHERE m.status = 'approved' AND c.teacher_id = ? " +
                "ORDER BY m.apply_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMakeupApply(rs));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * 5. 教师录入补考成绩（核心：包含事务，同时更新 makeup_apply 和 grade 表）
     */
    public boolean saveMakeupScore(Integer applyId, Integer studentId, Integer courseId, Double makeupScore) {
        Connection conn = null;
        PreparedStatement psApply = null;
        PreparedStatement psGrade = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 5.1 更新补考申请表的补考成绩
            String sqlApply = "UPDATE makeup_apply SET makeup_score = ? WHERE id = ?";
            psApply = conn.prepareStatement(sqlApply);
            psApply.setDouble(1, makeupScore);
            psApply.setInt(2, applyId);
            psApply.executeUpdate();

            // 5.2 计算新的总评和绩点
            double newTotal;
            double newGpa;
            if (makeupScore >= 60) {
                newTotal = 60.0; // 补考及格，总评最高按60算
                newGpa = 1.0;    // 绩点按及格线算
            } else {
                newTotal = makeupScore; // 补考不及格，按补考分算
                newGpa = 0.0;
            }

            // 5.3 更新成绩表
            // 注意：这里使用 UPDATE 而不是 INSERT，因为补考前提是该学生之前已有不及格成绩记录
            String sqlGrade = "UPDATE grade SET total_score = ?, gpa = ?, status = 'submitted' WHERE student_id = ? AND course_id = ?";
            psGrade = conn.prepareStatement(sqlGrade);
            psGrade.setDouble(1, newTotal);
            psGrade.setDouble(2, newGpa);
            psGrade.setInt(3, studentId);
            psGrade.setInt(4, courseId);
            psGrade.executeUpdate();

            conn.commit(); // 提交事务
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (psGrade != null) psGrade.close(); } catch (SQLException e) {}
            try { if (psApply != null) psApply.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }

    /**
     * 辅助方法：将 ResultSet 的一行映射成 MakeupApply 对象
     */
    private MakeupApply mapRowToMakeupApply(ResultSet rs) throws SQLException {
        MakeupApply apply = new MakeupApply();
        apply.setId(rs.getInt("id"));
        apply.setStudentId(rs.getInt("student_id"));
        apply.setCourseId(rs.getInt("course_id"));
        apply.setStatus(rs.getString("status"));

        double score = rs.getDouble("makeup_score");
        apply.setMakeupScore(rs.wasNull() ? null : score);

        apply.setApplyTime(rs.getTimestamp("apply_time"));
        apply.setStudentName(rs.getString("student_name"));
        apply.setStudentNumber(rs.getString("student_number"));
        apply.setCourseName(rs.getString("course_name"));

        double original = rs.getDouble("original_score");
        apply.setOriginalScore(rs.wasNull() ? null : original);

        return apply;
    }
    // 6. 学生查询自己的补考申请记录（去重放到 Java 层处理，SQL 只负责查全）
    public List<MakeupApply> findAppliesByStudentId(Integer studentId) {
        List<MakeupApply> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS student_name, s.student_number, c.course_name, g.total_score AS original_score " +
                "FROM makeup_apply m " +
                "JOIN student s ON m.student_id = s.id " +
                "LEFT JOIN course c ON m.course_id = c.id " +
                "LEFT JOIN grade g ON g.student_id = m.student_id AND g.course_id = m.course_id " +
                "WHERE m.student_id = ? " +
                "ORDER BY m.apply_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMakeupApply(rs));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}