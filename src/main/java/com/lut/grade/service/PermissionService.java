package com.lut.grade.service;

import com.lut.grade.model.Permission;
import com.lut.grade.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 动态权限（RBAC）服务。
 *
 * 设计要点：
 * 1) 权限码 = "模块:方法"，例如 admin.student:delete、teacher.grade:save。
 *    模块名直接用 Servlet 的路径去掉斜杠换来（/admin/student -> admin.student），
 *    所以每新增一个 Servlet 方法，天然就多一条可配置的权限，不需要到处登记。
 * 2) 权限目录（CATALOG）写在代码里，启动时同步进数据库 permission 表，
 *    这样"数据库里存着权限"和"代码里有哪些权限"永远不会对不上。
 * 3) 首次启动会把默认权限灌进 role_permission：管理员全给，教师给 teacher.*、
 *    学生给 student.*，另外三个角色都有 user:*（个人中心/改密码/退出）。
 * 4) 管理员在代码层面固定放行，避免管理员在页面上误把自己的权限全删掉之后再进不来。
 */
public class PermissionService {

    private static final PermissionService INSTANCE = new PermissionService();

    public static PermissionService getInstance() {
        return INSTANCE;
    }

    private static final long CACHE_TTL_MS = 30_000L;

    /** 权限目录，顺序就是页面上显示的顺序 */
    public static final List<Permission> CATALOG = new ArrayList<>();

    static {
        add("user",
                "toProfile", "个人中心",
                "uploadAvatar", "上传头像",
                "changePassword", "修改密码",
                "logout", "退出登录");

        add("admin.dashboard",
                "index", "管理员首页");

        add("admin.student",
                "list", "查看学生列表",
                "toAdd", "打开新增学生页",
                "add", "新增学生",
                "toEdit", "打开修改学生页",
                "update", "修改学生",
                "delete", "删除学生",
                "print", "打印学生名单");

        add("admin.teacher",
                "list", "查看教师列表",
                "toAdd", "打开新增教师页",
                "add", "新增教师",
                "toEdit", "打开修改教师页",
                "update", "修改教师",
                "delete", "删除教师",
                "print", "打印教师名单");

        add("admin.class",
                "list", "查看班级列表",
                "toAdd", "打开新增班级页",
                "add", "新增班级",
                "toEdit", "打开修改班级页",
                "update", "修改班级",
                "delete", "删除班级");

        add("admin.course",
                "list", "查看课程列表",
                "toAdd", "打开新增课程页",
                "add", "新增课程",
                "toEdit", "打开修改课程页",
                "update", "修改课程",
                "delete", "删除课程");

        add("admin.grade",
                "classStats", "班级成绩统计",
                "studentRanks", "班级学生排名",
                "exportClass", "导出班级成绩报表",
                "exportAll", "导出全校成绩汇总");

        add("admin.roster",
                "list", "查看选课名单",
                "remove", "移除选课学生");

        add("admin.template",
                "toConfig", "打开成绩模板配置",
                "save", "保存成绩模板");

        add("admin.approval",
                "list", "查看成绩修改申请",
                "approve", "同意成绩修改申请",
                "reject", "驳回成绩修改申请");

        add("admin.makeup",
                "list", "查看补考申请",
                "approve", "同意补考申请",
                "reject", "驳回补考申请");

        add("admin.user",
                "list", "查看账号列表",
                "toAdd", "打开新增账号页",
                "add", "新增账号",
                "resetPassword", "重置账号密码",
                "updateRole", "修改账号角色",
                "delete", "删除账号");

        add("admin.permission",
                "list", "查看角色权限",
                "save", "保存角色权限");

        add("teacher.course",
                "dashboard", "教师首页",
                "list", "查看我的课程");

        add("teacher.grade",
                "list", "打开成绩录入",
                "save", "保存成绩",
                "stats", "查看成绩统计",
                "export", "导出成绩 Excel",
                "importGrades", "批量导入成绩",
                "template", "下载导入模板");

        add("teacher.apply",
                "submit", "提交成绩修改申请");

        add("teacher.makeup",
                "list", "查看补考名单",
                "save", "录入补考成绩");

        add("teacher.roster",
                "list", "查看选课名单",
                "remove", "移除选课学生");

        add("student.dashboard",
                "index", "学生首页");

        add("student.course",
                "list", "查看可选课程",
                "select", "选课",
                "myCourses", "查看我的课程",
                "drop", "退课");

        add("student.grade",
                "list", "查看我的成绩",
                "export", "导出我的成绩单");

        add("student.makeup",
                "list", "查看我的补考申请",
                "apply", "申请补考");

        add("student.profile",
                "view", "查看我的信息",
                "save", "修改我的信息");
    }

    private static void add(String module, String... pairs) {
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            CATALOG.add(new Permission(module + ":" + pairs[i], pairs[i + 1], module));
        }
    }

    private volatile Map<String, Set<String>> cache;
    private volatile long cacheAt;

    private PermissionService() {
    }

    public List<Permission> catalog() {
        return Collections.unmodifiableList(CATALOG);
    }

    /** 某个角色的默认权限（首次初始化时写入数据库） */
    public Set<String> defaultPerms(String role) {
        Set<String> set = new HashSet<>();
        if ("admin".equals(role)) {
            for (Permission p : CATALOG) set.add(p.getCode());
            return set;
        }
        String prefix = ("teacher".equals(role)) ? "teacher." : "student.";
        for (Permission p : CATALOG) {
            if (p.getCode().startsWith(prefix) || p.getCode().startsWith("user:")) {
                set.add(p.getCode());
            }
        }
        return set;
    }

    public Set<String> permsOf(String role) {
        Map<String, Set<String>> snapshot = cache;
        if (snapshot == null || System.currentTimeMillis() - cacheAt > CACHE_TTL_MS) {
            snapshot = loadFromDb();
            cache = snapshot;
            cacheAt = System.currentTimeMillis();
        }
        Set<String> perms = snapshot.get(role);
        return perms == null ? Collections.<String>emptySet() : perms;
    }

    /**
     * 是否允许访问。管理员在代码里直接放行（超级用户），其余角色查 role_permission。
     */
    public boolean isAllowed(String role, String code) {
        if (role == null || role.isEmpty() || code == null || code.isEmpty()) {
            return false;
        }
        if ("admin".equals(role)) {
            return true;
        }
        Set<String> perms = permsOf(role);
        if (perms.contains("*") || perms.contains(code)) {
            return true;
        }
        int idx = code.indexOf(':');
        return idx > 0 && perms.contains(code.substring(0, idx) + ":*");
    }

    public void refresh() {
        cache = null;
    }

    /** 三个角色当前的权限集合，供权限管理页展示 */
    public Map<String, Set<String>> matrix() {
        Map<String, Set<String>> map = new LinkedHashMap<>();
        for (String role : new String[]{"admin", "teacher", "student"}) {
            map.put(role, new HashSet<>(permsOf(role)));
        }
        return map;
    }

    /** 覆盖保存某个角色的权限 */
    public boolean saveRolePerms(String role, Collection<String> codes) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        role = role.trim();
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM role_permission WHERE role = ?")) {
                del.setString(1, role);
                del.executeUpdate();
            }
            if (codes != null && !codes.isEmpty()) {
                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO role_permission(role, perm_code) VALUES(?, ?)")) {
                    for (String code : codes) {
                        if (code == null || code.trim().isEmpty()) continue;
                        ins.setString(1, role);
                        ins.setString(2, code.trim());
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
            }
            conn.commit();
            refresh();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignore) { /* ignore */ }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception ignore) { /* ignore */ }
                try { conn.close(); } catch (Exception ignore) { /* ignore */ }
            }
        }
    }

    private Map<String, Set<String>> loadFromDb() {
        Map<String, Set<String>> map = new HashMap<>();
        String sql = "SELECT role, perm_code FROM role_permission";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.computeIfAbsent(rs.getString("role"), k -> new HashSet<>())
                        .add(rs.getString("perm_code"));
            }
        } catch (Exception e) {
            // 表还没建出来（比如第一次部署、数据库刚好没起来）时退回内置默认，保证系统能用
            System.err.println("读取角色权限失败，使用内置默认权限：" + e.getMessage());
            for (String role : new String[]{"admin", "teacher", "student"}) {
                map.put(role, defaultPerms(role));
            }
        }
        return map;
    }

    /**
     * 启动时执行：建权限相关的表、给 user 表补 avatar 列、把权限目录同步进库、
     * 首次运行灌入默认角色权限。全部操作都是幂等的，重复启动没有副作用。
     */
    public void bootstrap() {
        try (Connection conn = DBUtil.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS `permission` ("
                        + "`id` INT NOT NULL AUTO_INCREMENT,"
                        + "`perm_code` VARCHAR(80) NOT NULL COMMENT '权限码 模块:方法',"
                        + "`perm_name` VARCHAR(80) NOT NULL COMMENT '权限名称',"
                        + "`module_name` VARCHAR(50) NOT NULL COMMENT '所属模块',"
                        + "PRIMARY KEY (`id`),"
                        + "UNIQUE KEY `uk_perm_code` (`perm_code`)"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表'");

                st.execute("CREATE TABLE IF NOT EXISTS `role_permission` ("
                        + "`id` INT NOT NULL AUTO_INCREMENT,"
                        + "`role` VARCHAR(20) NOT NULL COMMENT '角色：admin/teacher/student',"
                        + "`perm_code` VARCHAR(80) NOT NULL COMMENT '权限码',"
                        + "PRIMARY KEY (`id`),"
                        + "UNIQUE KEY `uk_role_perm` (`role`, `perm_code`)"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表'");
            }

            if (!hasColumn(conn, "user", "avatar")) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE `user` ADD COLUMN `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像相对路径'");
                }
                System.out.println("数据库升级：user 表已新增 avatar 列");
            }

            syncCatalog(conn);
            if (countRolePerms(conn) == 0) {
                seedDefaultRolePerms(conn);
                System.out.println("数据库初始化：默认角色权限写入完成（共 " + CATALOG.size() + " 条权限）");
            }
            refresh();
        } catch (Exception e) {
            System.err.println("权限体系初始化失败（系统会退回内置默认权限）：" + e.getMessage());
        }
    }

    private void syncCatalog(Connection conn) throws Exception {
        String sql = "INSERT INTO `permission`(perm_code, perm_name, module_name) VALUES(?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name), module_name = VALUES(module_name)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Permission p : CATALOG) {
                ps.setString(1, p.getCode());
                ps.setString(2, p.getName());
                ps.setString(3, p.getModule());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void seedDefaultRolePerms(Connection conn) throws Exception {
        String sql = "INSERT INTO `role_permission`(role, perm_code) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String role : new String[]{"admin", "teacher", "student"}) {
                for (String code : defaultPerms(role)) {
                    ps.setString(1, role);
                    ps.setString(2, code);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    private int countRolePerms(Connection conn) throws Exception {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM `role_permission`")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private boolean hasColumn(Connection conn, String table, String column) {
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}