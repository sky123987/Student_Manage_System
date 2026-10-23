package com.lut.grade.dao;

import com.lut.grade.model.Course;
import com.lut.grade.model.StudentGrade;
import com.lut.grade.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CourseSelectionDao {

    // 1. 查询所有可选课程（并标识当前学生是否已经选过）
    public List<Course> findAllCoursesWithSelectionStatus(Integer studentId) {
        List<Course> list = new ArrayList<>();
        // 使用 IF 函数判断是否已选
        String sql = "SELECT c.*, t.name AS teacher_name, " +
                "IF(cs.id IS NULL, '未选', '已选') AS status " +
                "FROM course c " +
                "LEFT JOIN teacher t ON c.teacher_id = t.id " +
                "LEFT JOIN course_selection cs ON c.id = cs.course_id AND cs.student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setId(rs.getInt("id"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCredit(rs.getDouble("credit"));
                    c.setTeacherId(rs.getInt("teacher_id"));
                    c.setCourseHours(rs.getInt("course_hours"));
                    c.setCourseType(rs.getString("course_type"));
                    c.setTeacherName(rs.getString("teacher_name"));
                    c.setStatus(rs.getString("status"));
                    list.add(c);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. 执行选课
    public boolean insertSelection(Integer studentId, Integer courseId) {
        String sql = "INSERT INTO course_selection (student_id, course_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 3. 查询该学生已选的课程
    public List<Course> findMySelectedCourses(Integer studentId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, t.name AS teacher_name " +
                "FROM course_selection cs " +
                "JOIN course c ON cs.course_id = c.id " +
                "LEFT JOIN teacher t ON c.teacher_id = t.id " +
                "WHERE cs.student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setId(rs.getInt("id"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCredit(rs.getDouble("credit"));
                    c.setTeacherName(rs.getString("teacher_name"));
                    c.setCourseHours(rs.getInt("course_hours"));
                    c.setCourseType(rs.getString("course_type"));
                    list.add(c);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 4. 退课
    public boolean deleteSelection(Integer studentId, Integer courseId) {
        String sql = "DELETE FROM course_selection WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 5. 查询某门课的选课名单（教师/管理员看名单用）
    public List<StudentGrade> findRosterByCourseId(Integer courseId) {
        List<StudentGrade> list = new ArrayList<>();
        String sql = "SELECT cs.student_id, s.student_number, s.name, cl.class_name, " +
                "g.total_score, g.status " +
                "FROM course_selection cs " +
                "JOIN student s ON cs.student_id = s.id " +
                "LEFT JOIN class cl ON s.class_id = cl.id " +
                "LEFT JOIN grade g ON g.student_id = cs.student_id AND g.course_id = cs.course_id " +
                "WHERE cs.course_id = ? " +
                "ORDER BY s.student_number";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentGrade sg = new StudentGrade();
                    sg.setStudentId(rs.getInt("student_id"));
                    sg.setStudentNumber(rs.getString("student_number"));
                    sg.setStudentName(rs.getString("name"));
                    sg.setClassName(rs.getString("class_name"));
                    double total = rs.getDouble("total_score");
                    sg.setTotalScore(rs.wasNull() ? null : total);
                    sg.setStatus(rs.getString("status"));
                    list.add(sg);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 6. 统计某门课的选课人数
    public int countByCourseId(Integer courseId) {
        String sql = "SELECT COUNT(*) FROM course_selection WHERE course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}