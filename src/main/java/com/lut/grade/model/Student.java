package com.lut.grade.model;

public class Student {
    private Integer id;
    private Integer userId;
    private String studentNumber; // 学号
    private String name;          // 姓名
    private Integer classId;      // 班级ID
    private String gender;        // 性别
    private String phone;         // 电话
    private String email;         // 邮箱

    // 额外字段：方便前端显示班级名称（从class表查出来的）
    private String className;

    // 生成 Getter 和 Setter (在IDEA里按 Alt+Insert 自动生成)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getClassId() { return classId; }
    public void setClassId(Integer classId) { this.classId = classId; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}