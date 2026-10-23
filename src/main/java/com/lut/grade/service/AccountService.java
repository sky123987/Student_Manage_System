package com.lut.grade.service;

import com.lut.grade.dao.StudentDao;
import com.lut.grade.dao.TeacherDao;
import com.lut.grade.dao.UserDao;
import com.lut.grade.model.Student;
import com.lut.grade.model.Teacher;
import com.lut.grade.model.User;
import com.lut.grade.model.UserAccount;
import com.lut.grade.util.DBUtil;
import com.lut.grade.util.MD5Util;

import java.sql.Connection;
import java.util.List;

/**
 * 账号 + 档案的联合创建/维护服务。
 *
 * 这里是整个系统原来最大的坑：user 表是登录用的，student/teacher 表是业务档案，
 * 两张表必须同时写入才是一个"能用的账号"。之前新增学生只插 student 且 user_id 传 null，
 * 被 MySQL 的 NOT NULL 约束顶掉，异常又被吞了，所以管理员看到的是"提示成功、列表没数据"。
 * 现在统一走这里，一个连接一个事务，要么都成、要么都不成。
 */
public class AccountService {

    /** 新建账号统一使用的初始密码 */
    public static final String DEFAULT_PASSWORD = "123456";

    private UserDao userDao = new UserDao();
    private StudentDao studentDao = new StudentDao();
    private TeacherDao teacherDao = new TeacherDao();

    /**
     * 新建学生账号（user + student 一起建）。
     *
     * @return null 表示成功；非 null 是给用户看的失败原因
     */
    public String createStudent(String username, String rawPassword, Student student) {
        return create(username, rawPassword, "student", student, null);
    }

    /**
     * 新建教师账号（user + teacher 一起建）。
     */
    public String createTeacher(String username, String rawPassword, Teacher teacher) {
        return create(username, rawPassword, "teacher", null, teacher);
    }

    /**
     * 自助注册。只建最基本的档案（编号=姓名=账号），详细信息留给管理员补全，
     * 这样注册完就能正常进学生端/教师端，不会卡在"档案不存在"。
     */
    public String selfRegister(String username, String rawPassword, String role) {
        if ("teacher".equals(role)) {
            Teacher t = new Teacher();
            t.setTeacherNumber(username);
            t.setName(username);
            return createTeacher(username, rawPassword, t);
        }
        Student s = new Student();
        s.setStudentNumber(username);
        s.setName(username);
        return createStudent(username, rawPassword, s);
    }

    private String create(String username, String rawPassword, String role, Student student, Teacher teacher) {
        if (isBlank(username)) {
            return "登录账号不能为空";
        }
        username = username.trim();
        String password = isBlank(rawPassword) ? DEFAULT_PASSWORD : rawPassword.trim();

        if (userDao.findByUsername(username) != null) {
            return "登录账号「" + username + "」已被占用，请换一个";
        }
        if (student != null && !isBlank(student.getStudentNumber())
                && studentDao.existsByStudentNumber(student.getStudentNumber().trim())) {
            return "学号「" + student.getStudentNumber().trim() + "」已存在，请检查是否重复添加";
        }
        if (teacher != null && !isBlank(teacher.getTeacherNumber())
                && teacherDao.existsByTeacherNumber(teacher.getTeacherNumber().trim())) {
            return "工号「" + teacher.getTeacherNumber().trim() + "」已存在，请检查是否重复添加";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Util.getMD5(password));
        user.setRole(role);

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int userId = userDao.insertUser(conn, user);
            if (userId <= 0) {
                throw new IllegalStateException("写入 user 表失败");
            }

            if (student != null) {
                student.setUserId(userId);
                if (isBlank(student.getStudentNumber())) student.setStudentNumber(username);
                if (isBlank(student.getName())) student.setName(username);
                if (!studentDao.insert(conn, student)) {
                    throw new IllegalStateException("写入 student 表失败");
                }
            }
            if (teacher != null) {
                teacher.setUserId(userId);
                if (isBlank(teacher.getTeacherNumber())) teacher.setTeacherNumber(username);
                if (isBlank(teacher.getName())) teacher.setName(username);
                if (!teacherDao.insert(conn, teacher)) {
                    throw new IllegalStateException("写入 teacher 表失败");
                }
            }

            conn.commit();
            return null;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignore) { /* 回滚失败也无能为力 */ }
            }
            e.printStackTrace();
            return "保存失败：" + e.getMessage();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception ignore) { /* ignore */ }
                try { conn.close(); } catch (Exception ignore) { /* ignore */ }
            }
        }
    }

    /**
     * 把密码重置为默认密码（管理员用）。
     */
    public boolean resetPassword(Integer userId) {
        return userDao.updatePassword(userId, MD5Util.getMD5(DEFAULT_PASSWORD));
    }

    /**
     * 修改角色。改完之后如果新角色没有对应档案，会补一条最小档案，
     * 否则这个人登录进去会直接卡在"档案不存在"。
     *
     * @return null 表示成功；非 null 是失败原因
     */
    public String changeRole(Integer userId, String newRole) {
        if (userId == null) return "缺少账号 id";
        if (isBlank(newRole)) return "角色不能为空";
        newRole = newRole.trim();
        if (!"admin".equals(newRole) && !"teacher".equals(newRole) && !"student".equals(newRole)) {
            return "非法角色：" + newRole;
        }

        User user = userDao.findById(userId);
        if (user == null) return "账号不存在";

        if (newRole.equals(user.getRole())) return null;

        // 别把最后一个管理员改掉，否则就没人能进后台了
        if ("admin".equals(user.getRole()) && userDao.countByRole("admin") <= 1) {
            return "系统至少需要保留一个管理员账号";
        }

        if (!userDao.updateRole(userId, newRole)) {
            return "修改角色失败";
        }
        ensureProfile(userId, user.getUsername(), newRole);
        return null;
    }

    /**
     * 删除账号。student/teacher 行由外键 ON DELETE CASCADE 自动清理。
     */
    public String deleteAccount(Integer userId) {
        if (userId == null) return "缺少账号 id";
        User user = userDao.findById(userId);
        if (user == null) return "账号不存在";
        if ("admin".equals(user.getRole()) && userDao.countByRole("admin") <= 1) {
            return "系统至少需要保留一个管理员账号";
        }
        return userDao.deleteById(userId) ? null : "删除失败";
    }

    /**
     * 登录时的兜底：如果角色对应的档案行不存在就补一条最小的，
     * 免得老账号一进学生端/教师端就弹"档案不存在"。
     */
    public void ensureProfile(User user) {
        if (user == null || user.getId() == null) {
            return;
        }
        if ("admin".equals(user.getRole())) {
            return;
        }
        ensureProfile(user.getId(), user.getUsername(), user.getRole());
    }

    private void ensureProfile(Integer userId, String username, String role) {
        try {
            if ("student".equals(role) && studentDao.findByUserId(userId) == null) {
                Student s = new Student();
                s.setUserId(userId);
                s.setStudentNumber(username);
                s.setName(username);
                studentDao.insert(s);
            } else if ("teacher".equals(role) && teacherDao.findByUserId(userId) == null) {
                Teacher t = new Teacher();
                t.setUserId(userId);
                t.setTeacherNumber(username);
                t.setName(username);
                teacherDao.insert(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UserAccount> listAccounts() {
        return userDao.findAllAccounts();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}