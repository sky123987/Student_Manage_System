package com.lut.grade.dao;

import com.lut.grade.model.Teacher;
import com.lut.grade.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeacherDao {
    // 根据 user_id 查询教师信息（用于获取当前登录教师档案）
    public Teacher findByUserId(Integer userId) {
        String sql = "SELECT * FROM teacher WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Teacher t = new Teacher();
                    t.setId(rs.getInt("id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setName(rs.getString("name"));
                    t.setTeacherNumber(rs.getString("teacher_number"));
                    t.setTitle(rs.getString("title"));
                    return t;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    // 查询教师总数
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM teacher";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    // 查询所有教师
    public java.util.List<Teacher> findAll() {
        java.util.List<Teacher> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM teacher";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Teacher t = new Teacher();
                t.setId(rs.getInt("id"));
                t.setUserId(rs.getInt("user_id"));
                t.setTeacherNumber(rs.getString("teacher_number"));
                t.setName(rs.getString("name"));
                t.setTitle(rs.getString("title"));
                list.add(t);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 根据ID查询单个教师
    public Teacher findById(Integer id) {
        String sql = "SELECT * FROM teacher WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Teacher t = new Teacher();
                    t.setId(rs.getInt("id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setTeacherNumber(rs.getString("teacher_number"));
                    t.setName(rs.getString("name"));
                    t.setTitle(rs.getString("title"));
                    return t;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 添加教师
    public boolean insert(Teacher t) {
        try (Connection conn = DBUtil.getConnection()) {
            return insert(conn, t);
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * 添加教师（连接由调用方给）。
     * teacher.user_id 在库里是 NOT NULL，所以这里不再"允许为空"——
     * 原来的 setNull 写法一定会被 MySQL 拒绝，异常又被吞掉，才会出现"提示成功但没数据"。
     */
    public boolean insert(Connection conn, Teacher t) throws Exception {
        String sql = "INSERT INTO teacher (user_id, teacher_number, name, title) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (t.getUserId() == null) {
                throw new IllegalArgumentException("teacher.user_id 不能为空，请先创建登录账号");
            }
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getTeacherNumber());
            ps.setString(3, t.getName());
            ps.setString(4, t.getTitle());
            return ps.executeUpdate() > 0;
        }
    }

    // 工号是否已被占用（新增教师前查重）
    public boolean existsByTeacherNumber(String teacherNumber) {
        String sql = "SELECT 1 FROM teacher WHERE teacher_number = ? LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 修改教师
    public boolean update(Teacher t) {
        String sql = "UPDATE teacher SET teacher_number=?, name=?, title=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTeacherNumber());
            ps.setString(2, t.getName());
            ps.setString(3, t.getTitle());
            ps.setInt(4, t.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 删除教师
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM teacher WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}