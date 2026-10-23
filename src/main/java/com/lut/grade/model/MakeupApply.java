package com.lut.grade.model;

import java.util.Date;

public class MakeupApply {
    private Integer id;
    private Integer studentId;
    private Integer courseId;
    private String status;       // pending-待审批, approved-已批准, rejected-已驳回
    private Double makeupScore;  // 补考成绩
    private Date applyTime;      // 申请时间

    // 以下为方便前端展示的扩展字段
    private String studentName;    // 学生姓名
    private String studentNumber;  // 学号
    private String courseName;     // 课程名称
    private Double originalScore;  // 原总评成绩

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getMakeupScore() { return makeupScore; }
    public void setMakeupScore(Double makeupScore) { this.makeupScore = makeupScore; }
    public Date getApplyTime() { return applyTime; }
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Double getOriginalScore() { return originalScore; }
    public void setOriginalScore(Double originalScore) { this.originalScore = originalScore; }
}