package com.lut.grade.model;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private String avatar;   // 头像相对路径，如 uploads/avatar/12.png；未升级数据库时可能为 null

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 给页面直接用的角色中文名，避免每个 JSP 都写一遍 c:choose
     */
    public String getRoleName() {
        if ("admin".equals(role)) return "管理员";
        if ("teacher".equals(role)) return "教师";
        if ("student".equals(role)) return "学生";
        return role == null ? "" : role;
    }
}