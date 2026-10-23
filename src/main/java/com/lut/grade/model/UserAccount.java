package com.lut.grade.model;

/**
 * 账号总表的一行：user 表 + 它挂着的学生/教师档案信息。
 * 只用于管理员「用户管理」页面的展示，不参与登录逻辑。
 */
public class UserAccount {
    private Integer id;          // user.id
    private String username;     // 登录账号
    private String role;
    private String roleName;
    private String realName;     // 学生姓名或教师姓名，没有档案时为 null
    private String number;       // 学号或工号
    private String className;    // 只有学生有
    private String createTime;
    private String avatar;
    private boolean hasProfile;  // 是否已建档案

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRoleName() {
        if (roleName != null) return roleName;
        if ("admin".equals(role)) return "管理员";
        if ("teacher".equals(role)) return "教师";
        if ("student".equals(role)) return "学生";
        return role == null ? "" : role;
    }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public boolean isHasProfile() { return hasProfile; }
    public void setHasProfile(boolean hasProfile) { this.hasProfile = hasProfile; }
}