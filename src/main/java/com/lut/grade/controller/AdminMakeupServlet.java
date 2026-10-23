package com.lut.grade.controller;

import com.lut.grade.service.MakeupApplyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理员端 - 补考申请审批。
 * 注意：这里用的域属性名是 makeupApplyList，和成绩修改审批的 applyList 区分开，
 * 两个页面原来共用 applyList 但字段结构不同，容易看晕。
 */
@WebServlet("/admin/makeup")
public class AdminMakeupServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final MakeupApplyService makeupApplyService = new MakeupApplyService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("makeupApplyList", makeupApplyService.getPendingApplies());
        forward(req, resp, "admin/admin_makeup_list.jsp");
    }

    public void approve(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer applyId = paramInt(req, "applyId");
        if (applyId == null) {
            redirect(req, resp, "/admin/makeup?method=list", "缺少申请 id", "danger");
            return;
        }
        if (makeupApplyService.approveApply(applyId)) {
            redirect(req, resp, "/admin/makeup?method=list", "已同意补考申请", "success");
        } else {
            redirect(req, resp, "/admin/makeup?method=list", "操作失败", "danger");
        }
    }

    public void reject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer applyId = paramInt(req, "applyId");
        if (applyId == null) {
            redirect(req, resp, "/admin/makeup?method=list", "缺少申请 id", "danger");
            return;
        }
        if (makeupApplyService.rejectApply(applyId)) {
            redirect(req, resp, "/admin/makeup?method=list", "已驳回补考申请", "success");
        } else {
            redirect(req, resp, "/admin/makeup?method=list", "操作失败", "danger");
        }
    }
}