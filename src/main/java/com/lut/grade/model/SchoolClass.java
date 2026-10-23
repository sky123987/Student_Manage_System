package com.lut.grade.model;

public class SchoolClass {
    private Integer id;
    private String className;  // 班级名称
    private String gradeYear;  // 年级（如 2021级）

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getGradeYear() { return gradeYear; }
    public void setGradeYear(String gradeYear) { this.gradeYear = gradeYear; }
}