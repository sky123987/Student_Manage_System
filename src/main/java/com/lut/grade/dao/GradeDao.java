package com.lut.grade.dao;

import com.lut.grade.model.GradeDetail;
import com.lut.grade.model.StudentGrade;
import com.lut.grade.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GradeDao {

    /**
     * 1. 教师端：查询某门课程的选课学生及成绩（适配新字段）
     */
    public List<StudentGrade> findStudentsByCourseId(Integer courseId) {
        List<StudentGrade> list = new ArrayList<>();
        // 修改点：在最后加上 g.status
        String sql = "SELECT s.id AS student_id, s.student_number, s.name, " +
                "g.daily_score_json, g.final_exam_score, g.total_score, g.gpa, g.status " +
                "FROM course_selection cs " +
                "JOIN student s ON cs.student_id = s.id " +
                "LEFT JOIN grade g ON g.student_id = s.id AND g.course_id = cs.course_id " +
                "WHERE cs.course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentGrade sg = new StudentGrade();
                    sg.setStudentId(rs.getInt("student_id"));
                    sg.setStudentNumber(rs.getString("student_number"));
                    sg.setStudentName(rs.getString("name"));
                    sg.setDailyScoreJson(rs.getString("daily_score_json"));
                    sg.setFinalExamScore(rs.getObject("final_exam_score") != null ? rs.getDouble("final_exam_score") : null);
                    sg.setTotalScore(rs.getObject("total_score") != null ? rs.getDouble("total_score") : null);
                    sg.setGpa(rs.getObject("gpa") != null ? rs.getDouble("gpa") : null);
                    // 新增：把状态取出来塞进实体类
                    sg.setStatus(rs.getString("status"));
                    list.add(sg);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * 2. 教师端：批量保存/更新成绩（适配新字段及ON DUPLICATE KEY UPDATE）
     */
    // 修改前（2个参数）
    // public boolean saveGrades(List<StudentGrade> grades, Integer courseId) { ... }

    // 修改后（3个参数）
    public boolean saveGrades(List<StudentGrade> grades, Integer courseId, String status) {
        // 1. 修改 SQL：把所有写死的 'submitted' 替换成 ?
        String sql = "INSERT INTO grade (student_id, course_id, daily_score_json, final_exam_score, total_score, gpa, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "daily_score_json=?, final_exam_score=?, total_score=?, gpa=?, status=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (StudentGrade sg : grades) {
                if (sg.getTotalScore() == null) continue;

                ps.setInt(1, sg.getStudentId());
                ps.setInt(2, courseId);
                ps.setString(3, sg.getDailyScoreJson() == null ? "{}" : sg.getDailyScoreJson());
                ps.setObject(4, sg.getFinalExamScore());
                ps.setObject(5, sg.getTotalScore());
                ps.setObject(6, sg.getGpa());
                ps.setString(7, status); // 第7个参数：动态状态

                // ON DUPLICATE KEY UPDATE 的对应参数
                ps.setString(8, sg.getDailyScoreJson() == null ? "{}" : sg.getDailyScoreJson());
                ps.setObject(9, sg.getFinalExamScore());
                ps.setObject(10, sg.getTotalScore());
                ps.setObject(11, sg.getGpa());
                ps.setString(12, status); // 第12个参数：动态状态

                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * 3. 学生端：查询某个学生的成绩（适配新字段）
     */
    public List<GradeDetail> findGradesByStudentId(Integer studentId) {
        List<GradeDetail> list = new ArrayList<>();
        String sql = "SELECT c.id AS course_id, c.course_name, c.credit, t.name AS teacher_name, " +
                "g.total_score, g.gpa, g.final_exam_score, g.daily_score_json " +
                "FROM grade g " +
                "JOIN course c ON g.course_id = c.id " +
                "LEFT JOIN teacher t ON c.teacher_id = t.id " +
                "WHERE g.student_id = ? AND g.status = 'submitted' AND g.total_score IS NOT NULL";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GradeDetail gd = new GradeDetail();
                    gd.setCourseId(rs.getInt("course_id"));
                    gd.setCourseName(rs.getString("course_name"));
                    gd.setCredit(rs.getDouble("credit"));
                    gd.setTeacherName(rs.getString("teacher_name"));

                    gd.setTotalScore(rs.getDouble("total_score"));
                    gd.setGpa(rs.getDouble("gpa"));
                    gd.setFinalExamScore(rs.getDouble("final_exam_score"));
                    gd.setDailyScoreJson(rs.getString("daily_score_json"));
                    list.add(gd);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    /**
     * 4. 教师端：查询某门课程所有已提交的最终成绩（用于统计）
     * 只查状态为 submitted 且成绩不为空的记录，避免草稿数据干扰统计
     */
    public List<Double> findScoresByCourseId(Integer courseId) {
        List<Double> scoreList = new ArrayList<>();
        String sql = "SELECT total_score FROM grade " +
                "WHERE course_id = ? AND status = 'submitted' AND total_score IS NOT NULL";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scoreList.add(rs.getDouble("total_score"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scoreList;
    }
}