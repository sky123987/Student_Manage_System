package com.lut.grade.dao;

import com.lut.grade.model.Student;
import com.lut.grade.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    // 1. 根据 user_id 查询学生档案（用于获取当前登录学生的 student_id）
    public Student findByUserId(Integer userId) {
        Student student = null;
        String sql = "SELECT s.*, c.class_name FROM student s " +
                "LEFT JOIN class c ON s.class_id = c.id " +
                "WHERE s.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = mapRowToStudent(rs);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return student;
    }

    // 2. 查询所有学生（联查班级表获取班级名）
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, c.class_name FROM student s LEFT JOIN class c ON s.class_id = c.id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. 根据ID查询单个学生
    public Student findById(Integer id) {
        String sql = "SELECT s.*, c.class_name FROM student s LEFT JOIN class c ON s.class_id = c.id WHERE s.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToStudent(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 4. 添加学生
    public boolean insert(Student s) {
        try (Connection conn = DBUtil.getConnection()) {
            return insert(conn, s);
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * 4b. 添加学生（连接由调用方给）。
     * 需要这个重载，是因为 student 行必须和 user 行在同一个事务里写入：
     * 先插 user 拿到自增 id，再把它作为 user_id 写进 student。
     */
    public boolean insert(Connection conn, Student s) throws Exception {
        String sql = "INSERT INTO student(user_id, student_number, name, class_id, gender, phone, email) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, s.getUserId());
            ps.setString(2, s.getStudentNumber());
            ps.setString(3, s.getName());
            ps.setObject(4, s.getClassId());
            ps.setString(5, s.getGender());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getEmail());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 4c. 学生本人维护联系方式。
     * 故意不复用 update()：update() 会一起写 student_number / name / class_id，
     * 学生端没有这些字段，传 null 就会把自己的学号姓名清空。
     */
    public boolean updateContact(Integer id, String gender, String phone, String email) {
        String sql = "UPDATE student SET gender = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gender);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 4d. 学号是否已被占用（新增学生前查重，避免撞唯一索引）
    public boolean existsByStudentNumber(String studentNumber) {
        String sql = "SELECT 1 FROM student WHERE student_number = ? LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 5. 修改学生
    public boolean update(Student s) {
        String sql = "UPDATE student SET student_number=?, name=?, class_id=?, gender=?, phone=?, email=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentNumber());
            ps.setString(2, s.getName());
            ps.setObject(3, s.getClassId());
            ps.setString(4, s.getGender());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getEmail());
            ps.setInt(7, s.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 6. 删除学生
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 7. 提取公共的封装方法
    private Student mapRowToStudent(ResultSet rs) throws Exception {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setStudentNumber(rs.getString("student_number"));
        s.setName(rs.getString("name"));

        // 注意：class_id 可能为 null，需要做容错处理
        int classId = rs.getInt("class_id");
        s.setClassId(rs.wasNull() ? null : classId);

        s.setGender(rs.getString("gender"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));

        // 联表查询出来的班级名（可能为 null）
        s.setClassName(rs.getString("class_name"));
        return s;
    }
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM student";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}