package com.lut.grade.dao;

import com.lut.grade.model.GradeDetail;
import com.lut.grade.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminGradeDao {

    public List<Map<String, Object>> getClassStats() {
        List<Map<String, Object>> list = new ArrayList<>();
        // 注意：class 是 MySQL 关键字，最好用反引号包裹 `class`
        // 按平均分从高到低排序
        String sql = "SELECT c.id AS class_id, c.class_name, " +
                "IFNULL(AVG(g.total_score), 0) AS avg_score, " +
                "IFNULL(AVG(g.gpa), 0) AS avg_gpa, " +
                "COUNT(DISTINCT s.id) AS student_count " +
                "FROM `class` c " +
                "LEFT JOIN student s ON s.class_id = c.id " +
                "LEFT JOIN grade g ON g.student_id = s.id AND g.status = 'submitted' " +
                "GROUP BY c.id, c.class_name " +
                "ORDER BY avg_score DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("classId", rs.getInt("class_id"));
                map.put("className", rs.getString("class_name"));
                // 保留两位小数
                map.put("avgScore", Math.round(rs.getDouble("avg_score") * 100.0) / 100.0);
                map.put("avgGpa", Math.round(rs.getDouble("avg_gpa") * 100.0) / 100.0);
                map.put("studentCount", rs.getInt("student_count"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<GradeDetail> getStudentGradesByClassId(Integer classId) {
        List<GradeDetail> list = new ArrayList<>();

        String sql = "SELECT s.id AS student_id, s.name AS student_name, s.student_number, " +
                "c.course_name, g.total_score, g.gpa, g.final_exam_score, g.daily_score_json " +
                "FROM student s " +
                "LEFT JOIN grade g ON g.student_id = s.id AND g.status = 'submitted' " +
                "LEFT JOIN course c ON g.course_id = c.id " +
                "WHERE s.class_id = ? " +
                "ORDER BY g.total_score DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GradeDetail gd = new GradeDetail();
                    gd.setStudentId(rs.getInt("student_id"));
                    gd.setStudentName(rs.getString("student_name"));
                    gd.setStudentNumber(rs.getString("student_number"));
                    gd.setCourseName(rs.getString("course_name"));

                    // 处理总分可能为 NULL 的情况
                    double total = rs.getDouble("total_score");
                    gd.setTotalScore(rs.wasNull() ? null : total);

                    // 处理绩点可能为 NULL 的情况
                    double gpa = rs.getDouble("gpa");
                    gd.setGpa(rs.wasNull() ? null : Math.round(gpa * 100.0) / 100.0);

                    // 处理期末成绩和平时成绩JSON
                    double finalScore = rs.getDouble("final_exam_score");
                    gd.setFinalExamScore(rs.wasNull() ? null : finalScore);

                    gd.setDailyScoreJson(rs.getString("daily_score_json"));

                    list.add(gd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}