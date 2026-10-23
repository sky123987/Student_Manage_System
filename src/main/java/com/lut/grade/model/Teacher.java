package com.lut.grade.model;

public class Teacher {
    private Integer id;
    private Integer userId;
    private String teacherNumber;
    private String name;
    private String title; // 职称

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTeacherNumber() { return teacherNumber; }
    public void setTeacherNumber(String teacherNumber) { this.teacherNumber = teacherNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}