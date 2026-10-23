package com.lut.grade.controller;

import com.lut.grade.model.Permission;
import com.lut.grade.service.PermissionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 管理员 - 角色权限。
 *
 * 页面上按"模块"分组列出全部权限码，三列（管理员/教师/学生）勾选，
 * 保存时覆盖写 role_permission 表，Filter 和 BaseServlet 下次判断即生效。
 */
@WebServlet("/admin/permission")
public class AdminPermissionServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final PermissionService permissionService = PermissionService.getInstance();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("moduleGroups", groupByModule());
        req.setAttribute("matrix", permissionService.matrix());
        req.setAttribute("totalPerms", permissionService.catalog().size());
        forward(req, resp, "admin/admin_permission.jsp");
    }

    public void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String role = param(req, "role");
        if (role == null || role.isEmpty()) {
            redirect(req, resp, "/admin/permission?method=list", "缺少角色参数", "danger");
            return;
        }
        if ("admin".equals(role)) {
            // 管理员是超级用户，代码里固定放行，页面上不允许改，避免把自己锁在门外
            redirect(req, resp, "/admin/permission?method=list",
                    "管理员权限固定为全部可用，不能修改", "warning");
            return;
        }

        String[] codes = req.getParameterValues("codes");
        List<String> list = new ArrayList<>();
        if (codes != null) {
            for (String c : codes) {
                if (c != null && !c.trim().isEmpty()) {
                    list.add(c.trim());
                }
            }
        }

        boolean ok = permissionService.saveRolePerms(role, list);
        if (ok) {
            redirect(req, resp, "/admin/permission?method=list",
                    "「" + AdminUserServlet.roleName(role) + "」的权限已保存，共 " + list.size() + " 项", "success");
        } else {
            redirect(req, resp, "/admin/permission?method=list", "保存失败", "danger");
        }
    }

    /** 模块 -> 该模块下的权限，保持权限目录里的顺序 */
    private Map<String, List<Permission>> groupByModule() {
        Map<String, List<Permission>> groups = new LinkedHashMap<>();
        for (Permission p : permissionService.catalog()) {
            groups.computeIfAbsent(p.getModule(), k -> new ArrayList<>()).add(p);
        }
        return groups;
    }

    @SuppressWarnings("unused")
    private static boolean contains(Set<String> set, String code) {
        return set != null && set.contains(code);
    }
}