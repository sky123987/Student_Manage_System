package com.lut.grade.model;

public class GradeStats {
    private double avgScore;      // 平均分
    private double maxScore;      // 最高分
    private double minScore;      // 最低分
    private double passRate;      // 及格率 (百分比，如 85.5 表示 85.5%)
    private int totalCount;       // 总人数

    // 分数段人数统计
    private int countBelow60;     // 不及格 (<60)
    private int count60to69;      // 及格 (60-69)
    private int count70to79;      // 中等 (70-79)
    private int count80to89;      // 良好 (80-89)
    private int count90to100;     // 优秀 (90-100)

    // Getters & Setters
    public double getAvgScore() { return avgScore; }
    public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    public double getMinScore() { return minScore; }
    public void setMinScore(double minScore) { this.minScore = minScore; }
    public double getPassRate() { return passRate; }
    public void setPassRate(double passRate) { this.passRate = passRate; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getCountBelow60() { return countBelow60; }
    public void setCountBelow60(int countBelow60) { this.countBelow60 = countBelow60; }
    public int getCount60to69() { return count60to69; }
    public void setCount60to69(int count60to69) { this.count60to69 = count60to69; }
    public int getCount70to79() { return count70to79; }
    public void setCount70to79(int count70to79) { this.count70to79 = count70to79; }
    public int getCount80to89() { return count80to89; }
    public void setCount80to89(int count80to89) { this.count80to89 = count80to89; }
    public int getCount90to100() { return count90to100; }
    public void setCount90to100(int count90to100) { this.count90to100 = count90to100; }
}