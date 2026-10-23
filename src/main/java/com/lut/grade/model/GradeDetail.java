package com.lut.grade.model;

public class GradeDetail {

    private Integer studentId;
    private String studentName;
    private String studentNumber;
    private Double totalScore;       // 对应数据库 total_score
    private Double finalExamScore;   // 对应数据库 final_exam_score
    private String dailyScoreJson;
    private Integer courseId;
    private String courseName;
    private Double credit;
    private String teacherName;
    private Double score;

    // 以下字段由 Service 层根据 score 动态计算
    private Double gpa;         // 绩点
    private String gradeLevel;  // 等级
    // 新增字段：用于在成绩列表展示补考状态
    private String makeupStatus;  // 状态：pending, approved, rejected
    private Double makeupScore;   // 补考成绩

    // 补全 Getter 和 Setter
    public String getMakeupStatus() { return makeupStatus; }
    public void setMakeupStatus(String makeupStatus) { this.makeupStatus = makeupStatus; }
    public Double getMakeupScore() { return makeupScore; }
    public void setMakeupScore(Double makeupScore) { this.makeupScore = makeupScore; }
    // Getters & Setters
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
    public Double getFinalExamScore() { return finalExamScore; }
    public void setFinalExamScore(Double finalExamScore) { this.finalExamScore = finalExamScore; }
    public String getDailyScoreJson() { return dailyScoreJson; }
    public void setDailyScoreJson(String dailyScoreJson) { this.dailyScoreJson = dailyScoreJson; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Double getCredit() { return credit; }
    public void setCredit(Double credit) { this.credit = credit; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }
    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
}