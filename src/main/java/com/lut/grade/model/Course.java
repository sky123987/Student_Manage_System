package com.lut.grade.model;

public class Course {
    private Integer id;
    private String courseName;    // 课程名称
    private Double credit;        // 学分
    private Integer teacherId;    // 授课教师ID
    private Integer courseHours;  // 学时
    private String courseType;    // 课程类型(必修/选修)

    // 在 Course.java 中确认或添加
    private String teacherName;
    private String status; // 选课状态：已选/未选

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getCredit() { return credit; }
    public void setCredit(Double credit) { this.credit = credit; }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }

    public Integer getCourseHours() { return courseHours; }
    public void setCourseHours(Integer courseHours) { this.courseHours = courseHours; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }


}