package com.lut.grade.controller;

import com.lut.grade.service.GradeModifyApplyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理员端 - 成绩修改申请审批。
 */
@WebServlet("/admin/approval")
public class AdminApprovalServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final GradeModifyApplyService applyService = new GradeModifyApplyService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("applyList", applyService.getPendingApplies());
        forward(req, resp, "admin/admin_approval_list.jsp");
    }

    public void approve(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer applyId = paramInt(req, "applyId");
        Integer courseId = paramInt(req, "courseId");
        if (applyId == null || courseId == null) {
            redirect(req, resp, "/admin/approval?method=list", "缺少申请或课程参数", "danger");
            return;
        }
        if (applyService.approve(applyId, courseId)) {
            redirect(req, resp, "/admin/approval?method=list", "已同意申请，该课程成绩已退回可修改状态", "success");
        } else {
            redirect(req, resp, "/admin/approval?method=list", "操作失败", "danger");
        }
    }

    public void reject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer applyId = paramInt(req, "applyId");
        Integer courseId = paramInt(req, "courseId");
        if (applyId == null || courseId == null) {
            redirect(req, resp, "/admin/approval?method=list", "缺少申请或课程参数", "danger");
            return;
        }
        if (applyService.reject(applyId, courseId)) {
            redirect(req, resp, "/admin/approval?method=list", "已驳回该申请", "success");
        } else {
            redirect(req, resp, "/admin/approval?method=list", "操作失败", "danger");
        }
    }
}