package com.lut.grade.service;

import com.lut.grade.dao.GradeDao;
import com.lut.grade.model.GradeDetail;
import com.lut.grade.model.StudentGrade;
import java.util.List;

public class GradeService {
    private GradeDao gradeDao = new GradeDao();

    public List<StudentGrade> getStudentsByCourse(Integer courseId) {
        return gradeDao.findStudentsByCourseId(courseId);
    }

    public boolean saveGrades(List<StudentGrade> grades, Integer courseId, String status) {
        return gradeDao.saveGrades(grades, courseId, status);
    }
    // 获取学生成绩，并在内存中计算绩点和等级
    // 获取学生成绩，并在内存中计算绩点和等级
    public java.util.List<GradeDetail> getStudentGradesWithStats(Integer studentId) {
        java.util.List<GradeDetail> list = gradeDao.findGradesByStudentId(studentId);
        for (GradeDetail gd : list) {
            Double totalScore = gd.getTotalScore(); // 替换这里

            // 如果数据库里有总分，则计算等级；如果没有（未录入），跳过
            if (totalScore != null) {
                // 1. 计算等级
                if (totalScore >= 90) gd.setGradeLevel("优");
                else if (totalScore >= 80) gd.setGradeLevel("良");
                else if (totalScore >= 70) gd.setGradeLevel("中");
                else if (totalScore >= 60) gd.setGradeLevel("及格");
                else gd.setGradeLevel("不及格");

                // 2. 绩点若为空则补算 (成绩-50)/10
                if (gd.getGpa() == null) {
                    double gpa = (totalScore - 50) / 10.0;
                    gd.setGpa(gpa < 0 ? 0.0 : Math.round(gpa * 100.0) / 100.0);
                }
            }
        }
        return list;
    }

    // 计算个人统计：平均分、总学分、平均绩点
    public java.util.Map<String, Object> calculateStudentStats(java.util.List<GradeDetail> list) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        if (list == null || list.isEmpty()) {
            stats.put("avgScore", 0.0);
            stats.put("totalCredit", 0.0);
            stats.put("avgGpa", 0.0);
            return stats;
        }

        double totalScore = 0;
        double totalCredit = 0;
        double weightedGpaSum = 0;
        int validCount = 0;

        for (GradeDetail gd : list) {
            if (gd.getTotalScore() != null) {
                totalScore += gd.getTotalScore(); // 平均分包含所有已出成绩的课程
                validCount++;

                // 核心修复：只有及格（>=60）才计算学分和绩点
                if (gd.getTotalScore() >= 60) {
                    totalCredit += gd.getCredit(); // 及格才能获得学分
                    if (gd.getGpa() != null) {
                        weightedGpaSum += gd.getGpa() * gd.getCredit(); // 及格才参与绩点加权
                    }
                }
            }
        }

        double avgScore = validCount > 0 ? totalScore / validCount : 0;
        double avgGpa = totalCredit > 0 ? weightedGpaSum / totalCredit : 0;

        stats.put("avgScore", Math.round(avgScore * 100.0) / 100.0);
        stats.put("totalCredit", Math.round(totalCredit * 100.0) / 100.0);
        stats.put("avgGpa", Math.round(avgGpa * 100.0) / 100.0);
        return stats;
    }
    /**
     * 单科成绩统计：计算平均分、最高分、最低分、及格率、各分数段人数
     * @param courseId 课程ID
     * @return 封装好的 GradeStats 对象
     */
    public com.lut.grade.model.GradeStats getCourseStats(Integer courseId) {
        // 1. 从数据库拉出该课程所有已提交的分数
        java.util.List<Double> scores = gradeDao.findScoresByCourseId(courseId);

        com.lut.grade.model.GradeStats stats = new com.lut.grade.model.GradeStats();
        stats.setTotalCount(scores.size());

        // 2. 如果无成绩，直接返回默认的 0 值对象
        if (scores.isEmpty()) {
            stats.setAvgScore(0.0);
            stats.setMaxScore(0.0);
            stats.setMinScore(0.0);
            stats.setPassRate(0.0);
            return stats;
        }

        // 3. 一次遍历计算所有指标（性能最优）
        double sum = 0;
        double max = scores.get(0);
        double min = scores.get(0);
        int passCount = 0;
        int cBelow60 = 0, c60to69 = 0, c70to79 = 0, c80to89 = 0, c90to100 = 0;

        for (Double score : scores) {
            sum += score;

            // 最高/最低分
            if (score > max) max = score;
            if (score < min) min = score;

            // 及格率（>=60 算及格）
            if (score >= 60) passCount++;

            // 分数段归类
            if (score < 60) cBelow60++;
            else if (score < 70) c60to69++;
            else if (score < 80) c70to79++;
            else if (score < 90) c80to89++;
            else c90to100++;
        }

        // 4. 计算平均分和及格率，保留两位小数
        double avg = sum / scores.size();
        double passRate = (passCount * 100.0) / scores.size();

        stats.setAvgScore(Math.round(avg * 100.0) / 100.0);
        stats.setMaxScore(max);
        stats.setMinScore(min);
        stats.setPassRate(Math.round(passRate * 100.0) / 100.0);
        stats.setCountBelow60(cBelow60);
        stats.setCount60to69(c60to69);
        stats.setCount70to79(c70to79);
        stats.setCount80to89(c80to89);
        stats.setCount90to100(c90to100);

        return stats;
    }
}