package com.lut.grade.dao;

import com.lut.grade.model.User;
import com.lut.grade.model.UserAccount;
import com.lut.grade.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // 登录查询
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 根据用户名查询用户（用于注册查重、初始化检查）
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 根据主键查询用户
    public User findById(Integer id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 注册插入
    public boolean insertUser(User user) {
        try (Connection conn = DBUtil.getConnection()) {
            return insertUser(conn, user) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 插入用户并返回自增主键。由调用方提供连接，这样可以和档案表（student/teacher）
     * 的插入放进同一个事务，避免出现"账号建好了、档案没建上"的半截数据。
     *
     * @return 新用户 id；失败返回 -1
     */
    public int insertUser(Connection conn, User user) throws Exception {
        String sql = "INSERT INTO user(username, password, role) VALUES(?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            if (ps.executeUpdate() == 0) {
                return -1;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    // 根据用户ID更新密码
    public boolean updatePassword(Integer userId, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 更新头像（存相对路径，页面自己拼 contextPath）
    public boolean updateAvatar(Integer userId, String avatar) {
        String sql = "UPDATE user SET avatar = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, avatar);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 修改角色
    public boolean updateRole(Integer userId, String role) {
        String sql = "UPDATE user SET role = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 删除账号。student / teacher 表的外键是 ON DELETE CASCADE，档案会跟着一起删。
    public boolean deleteById(Integer userId) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 账号总数
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM user";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 某个角色下的账号数量（用于防止把最后一个管理员改掉）
    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM user WHERE role = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 账号总表：把 user 和它的学生/教师档案左连接出来。
     * 学生和教师都可能没有档案（历史数据），所以全部用 LEFT JOIN + COALESCE 兜底。
     */
    public List<UserAccount> findAllAccounts() {
        List<UserAccount> list = new ArrayList<>();
        String sql = "SELECT u.*, "
                + "s.id AS student_id, s.name AS student_name, s.student_number, s.class_id, c.class_name, "
                + "t.id AS teacher_id, t.name AS teacher_name, t.teacher_number "
                + "FROM user u "
                + "LEFT JOIN student s ON s.user_id = u.id "
                + "LEFT JOIN class c ON s.class_id = c.id "
                + "LEFT JOIN teacher t ON t.user_id = u.id "
                + "ORDER BY u.id";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserAccount a = new UserAccount();
                a.setId(rs.getInt("id"));
                a.setUsername(rs.getString("username"));
                a.setRole(rs.getString("role"));
                try {
                    a.setAvatar(rs.getString("avatar"));
                } catch (Exception ignored) {
                    // 未执行升级脚本时没有 avatar 列
                }
                Timestamp ts = rs.getTimestamp("create_time");
                a.setCreateTime(ts == null ? "" : sdf.format(ts));

                String studentName = rs.getString("student_name");
                String teacherName = rs.getString("teacher_name");
                String studentNumber = rs.getString("student_number");
                String teacherNumber = rs.getString("teacher_number");
                if (studentName != null || studentNumber != null) {
                    a.setRealName(studentName);
                    a.setNumber(studentNumber);
                    a.setClassName(rs.getString("class_name"));
                    a.setHasProfile(true);
                } else if (teacherName != null || teacherNumber != null) {
                    a.setRealName(teacherName);
                    a.setNumber(teacherNumber);
                    a.setHasProfile(true);
                } else {
                    a.setHasProfile(false);
                }
                list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private User mapUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        try {
            user.setAvatar(rs.getString("avatar"));
        } catch (Exception ignored) {
            // 未执行升级脚本时 user 表还没有 avatar 列，忽略即可
        }
        return user;
    }
}