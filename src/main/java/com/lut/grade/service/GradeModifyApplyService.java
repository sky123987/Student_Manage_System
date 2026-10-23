package com.lut.grade.service;

import com.lut.grade.dao.GradeModifyApplyDao;
import com.lut.grade.model.GradeModifyApply;
import java.util.List;

public class GradeModifyApplyService {
    private GradeModifyApplyDao applyDao = new GradeModifyApplyDao();

    public boolean submitApply(GradeModifyApply apply) {
        return applyDao.insertApply(apply);
    }

    public List<GradeModifyApply> getPendingApplies() {
        return applyDao.findPendingApplies();
    }

    public boolean approve(Integer applyId, Integer courseId) {
        return applyDao.handleApproval(applyId, courseId, "approved");
    }

    public boolean reject(Integer applyId, Integer courseId) {
        return applyDao.handleApproval(applyId, courseId, "rejected");
    }
}