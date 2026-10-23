package com.lut.grade.dao;

import com.lut.grade.model.Course;
import com.lut.grade.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDao {
    // 查询某位教师的所有课程
    public List<Course> findByTeacherId(Integer teacherId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE teacher_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setId(rs.getInt("id"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCredit(rs.getDouble("credit"));
                    c.setTeacherId(rs.getInt("teacher_id"));
                    c.setCourseHours(rs.getInt("course_hours"));
                    c.setCourseType(rs.getString("course_type"));
                    list.add(c);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    // 统计教师的授课课程总数
    public int countCoursesByTeacherId(Integer teacherId) {
        String sql = "SELECT COUNT(*) FROM course WHERE teacher_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // 统计该教师所授的学生总数（去重）
    public int countStudentsByTeacherId(Integer teacherId) {
        String sql = "SELECT COUNT(DISTINCT cs.student_id) FROM course_selection cs " +
                "JOIN course c ON cs.course_id = c.id WHERE c.teacher_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // 统计该教师还有多少份成绩未录入 (选课表中存在，但成绩表中无成绩或成绩为空)
    public int countPendingGradesByTeacherId(Integer teacherId) {
        String sql = "SELECT COUNT(*) FROM course_selection cs " +
                "JOIN course c ON cs.course_id = c.id " +
                "LEFT JOIN grade g ON cs.student_id = g.student_id AND cs.course_id = g.course_id " +
                "WHERE c.teacher_id = ? AND (g.total_score IS NULL)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    // 动态查询教师待办事项（课程名、班级名、待录入人数等）
    public List<Map<String, Object>> getPendingTasksByTeacherId(Integer teacherId) {
        List<Map<String, Object>> list = new ArrayList<>();
        // 核心SQL：联表查询课程、选课、学生、班级、成绩
        String sql = "SELECT c.id AS course_id, c.course_name, " +
                "GROUP_CONCAT(DISTINCT cl.class_name SEPARATOR ', ') AS class_names, " +
                "COUNT(DISTINCT cs.student_id) AS total_students, " +
                "COUNT(DISTINCT CASE WHEN g.total_score IS NULL THEN cs.student_id END) AS pending_students " +
                "FROM course c " +
                "LEFT JOIN course_selection cs ON c.id = cs.course_id " +
                "LEFT JOIN student s ON cs.student_id = s.id " +
                "LEFT JOIN class cl ON s.class_id = cl.id " +
                "LEFT JOIN grade g ON cs.student_id = g.student_id AND cs.course_id = g.course_id " +
                "WHERE c.teacher_id = ? " +
                "GROUP BY c.id, c.course_name";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseId", rs.getInt("course_id"));
                    map.put("courseName", rs.getString("course_name"));
                    map.put("className", rs.getString("class_names"));
                    map.put("totalStudents", rs.getInt("total_students"));
                    map.put("pendingStudents", rs.getInt("pending_students"));
                    list.add(map);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    // 在 com.lut.grade.dao.CourseDao 中添加
    // 查询所有课程，同时带出教师名字（用于管理员展示）
    public java.util.List<Course> findAllWithTeacher() {
        java.util.List<Course> list = new java.util.ArrayList<>();
        String sql = "SELECT c.*, t.name AS teacher_name FROM course c LEFT JOIN teacher t ON c.teacher_id = t.id";
        try (java.sql.Connection conn = com.lut.grade.util.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getInt("id"));
                c.setCourseName(rs.getString("course_name"));
                c.setCredit(rs.getDouble("credit"));
                c.setTeacherId(rs.getInt("teacher_id"));
                c.setCourseHours(rs.getInt("course_hours"));
                c.setCourseType(rs.getString("course_type"));
                // 临时把教师名字存到 className 字段里复用 (或者你在 Course 实体里加个 teacherName 字段)
                c.setTeacherName(rs.getString("teacher_name"));
                list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 插入课程（分配教师）
    public boolean insert(Course c) {
        String sql = "INSERT INTO course (course_name, credit, teacher_id, course_hours, course_type) VALUES (?, ?, ?, ?, ?)";
        try (java.sql.Connection conn = com.lut.grade.util.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setDouble(2, c.getCredit());
            ps.setInt(3, c.getTeacherId());
            ps.setInt(4, c.getCourseHours());
            ps.setString(5, c.getCourseType());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 根据ID查询课程
    public Course findById(Integer id) {
        String sql = "SELECT * FROM course WHERE id = ?";
        try (java.sql.Connection conn = com.lut.grade.util.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course c = new Course();
                    c.setId(rs.getInt("id"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCredit(rs.getDouble("credit"));
                    c.setTeacherId(rs.getInt("teacher_id"));
                    c.setCourseHours(rs.getInt("course_hours"));
                    c.setCourseType(rs.getString("course_type"));
                    return c;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 更新课程
    public boolean update(Course c) {
        String sql = "UPDATE course SET course_name=?, credit=?, teacher_id=?, course_hours=?, course_type=? WHERE id=?";
        try (java.sql.Connection conn = com.lut.grade.util.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setDouble(2, c.getCredit());
            ps.setInt(3, c.getTeacherId());
            ps.setInt(4, c.getCourseHours());
            ps.setString(5, c.getCourseType());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 删除课程
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM course WHERE id = ?";
        try (java.sql.Connection conn = com.lut.grade.util.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    // 查询课程总数
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM course";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}