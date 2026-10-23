package com.lut.grade.model;

import java.util.Date;

public class GradeModifyApply {
    private Integer id;
    private Integer courseId;
    private Integer teacherId;
    private String reason;     // 申请理由
    private String status;     // 状态: pending-待审批, approved-已通过, rejected-已驳回
    private Date applyTime;    // 申请时间

    // 额外字段：用于在管理员页面展示课程名和教师名（方便前端直接读取）
    private String courseName;
    private String teacherName;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getApplyTime() { return applyTime; }
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
}