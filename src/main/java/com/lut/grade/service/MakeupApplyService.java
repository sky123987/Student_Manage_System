package com.lut.grade.service;

import com.lut.grade.dao.MakeupApplyDao;
import com.lut.grade.model.MakeupApply;

import java.util.List;

public class MakeupApplyService {
    // 注入 DAO 层
    private MakeupApplyDao makeupApplyDao = new MakeupApplyDao();

    // 1. 学生提交补考申请
    public boolean submitApply(Integer studentId, Integer courseId) {
        // 严谨的代码可以在这里判断一下：该学生是否真的不及格？是否已经申请过？
        // 但为了最快跑通流程，直接调用 DAO
        return makeupApplyDao.insertApply(studentId, courseId);
    }

    // 2. 管理员查询待审批列表
    public List<MakeupApply> getPendingApplies() {
        return makeupApplyDao.findPendingApplies();
    }

    // 3. 管理员批准申请
    public boolean approveApply(Integer applyId) {
        return makeupApplyDao.updateStatus(applyId, "approved");
    }

    // 4. 管理员驳回申请
    public boolean rejectApply(Integer applyId) {
        return makeupApplyDao.updateStatus(applyId, "rejected");
    }

    // 5. 教师查询自己课程下已批准的补考名单
    public List<MakeupApply> getApprovedAppliesByTeacherId(Integer teacherId) {
        return makeupApplyDao.findApprovedAppliesByTeacherId(teacherId);
    }

    // 6. 教师录入补考成绩（核心：包含成绩更新逻辑）
    public boolean submitMakeupScore(Integer applyId, Integer studentId, Integer courseId, Double makeupScore) {
        // 可以在这里加一层业务校验，比如补考成绩不能为空，不能超过100
        if (makeupScore == null || makeupScore < 0 || makeupScore > 100) {
            return false;
        }
        return makeupApplyDao.saveMakeupScore(applyId, studentId, courseId, makeupScore);
    }
    public java.util.List<com.lut.grade.model.MakeupApply> findAppliesByStudentId(Integer studentId) {
        return makeupApplyDao.findAppliesByStudentId(studentId);
    }
}