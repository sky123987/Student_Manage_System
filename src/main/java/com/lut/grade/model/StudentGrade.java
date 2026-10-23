package com.lut.grade.model;

public class StudentGrade {
    private Integer studentId;
    private String studentNumber; // 学号
    private String studentName;   // 姓名
    private Double score;         // 旧的字段，暂时保留兼容（不再使用）

    // 新增字段：适配新成绩结构
    private String dailyScoreJson;  // 平时成绩 JSON 明细
    private Double finalExamScore;  // 期末成绩
    private Double totalScore;      // 总评成绩
    private Double gpa;             // 绩点
    private String status;
    private String className;       // 学生所在班级（选课名单页用）
    // Getters & Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getDailyScoreJson() { return dailyScoreJson; }
    public void setDailyScoreJson(String dailyScoreJson) { this.dailyScoreJson = dailyScoreJson; }
    public Double getFinalExamScore() { return finalExamScore; }
    public void setFinalExamScore(Double finalExamScore) { this.finalExamScore = finalExamScore; }
    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}