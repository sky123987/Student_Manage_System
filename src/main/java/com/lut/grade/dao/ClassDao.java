package com.lut.grade.dao;

import com.lut.grade.model.SchoolClass;
import com.lut.grade.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClassDao {

    // 查询所有班级
    public List<SchoolClass> findAll() {
        List<SchoolClass> list = new ArrayList<>();
        String sql = "SELECT * FROM `class`";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SchoolClass c = new SchoolClass();
                c.setId(rs.getInt("id"));
                c.setClassName(rs.getString("class_name"));
                c.setGradeYear(rs.getString("grade_year"));
                list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 根据ID查询班级
    public SchoolClass findById(Integer id) {
        String sql = "SELECT * FROM `class` WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SchoolClass c = new SchoolClass();
                    c.setId(rs.getInt("id"));
                    c.setClassName(rs.getString("class_name"));
                    c.setGradeYear(rs.getString("grade_year"));
                    return c;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 新增班级
    public boolean insert(SchoolClass c) {
        String sql = "INSERT INTO `class` (class_name, grade_year) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getClassName());
            ps.setString(2, c.getGradeYear());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 修改班级
    public boolean update(SchoolClass c) {
        String sql = "UPDATE `class` SET class_name=?, grade_year=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getClassName());
            ps.setString(2, c.getGradeYear());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 删除班级
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM `class` WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}