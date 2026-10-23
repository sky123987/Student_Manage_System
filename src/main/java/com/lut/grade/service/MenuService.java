package com.lut.grade.service;

import com.lut.grade.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 侧边栏菜单。每个菜单项都绑定一个权限码，没有权限的人看不到对应入口，
 * 页面本身在 BaseServlet 里还会再校验一次，菜单只是第一道过滤。
 */
public class MenuService {

    private static final MenuService INSTANCE = new MenuService();

    public static MenuService getInstance() {
        return INSTANCE;
    }

    private MenuService() {
    }

    public List<MenuItem> menusFor(String role, String currentCode) {
        List<MenuItem> all = new ArrayList<>();
        if ("admin".equals(role)) {
            all.add(new MenuItem("系统概览", "bi-speedometer2", "/admin/dashboard?method=index", "admin.dashboard:index", false));
            all.add(new MenuItem("学生管理", "bi-people", "/admin/student?method=list", "admin.student:list", false));
            all.add(new MenuItem("教师管理", "bi-person-badge", "/admin/teacher?method=list", "admin.teacher:list", false));
            all.add(new MenuItem("班级管理", "bi-building", "/admin/class?method=list", "admin.class:list", false));
            all.add(new MenuItem("课程管理", "bi-book", "/admin/course?method=list", "admin.course:list", false));
            all.add(new MenuItem("选课名单", "bi-person-check", "/admin/roster?method=list", "admin.roster:list", false));
            all.add(new MenuItem("成绩统计", "bi-bar-chart-line", "/admin/grade?method=classStats", "admin.grade:classStats", false));
            all.add(new MenuItem("审批中心", "bi-file-earmark-text", "/admin/approval?method=list", "admin.approval:list", false));
            all.add(new MenuItem("补考审批", "bi-arrow-repeat", "/admin/makeup?method=list", "admin.makeup:list", false));
            all.add(new MenuItem("账号管理", "bi-person-gear", "/admin/user?method=list", "admin.user:list", false));
            all.add(new MenuItem("权限管理", "bi-shield-lock", "/admin/permission?method=list", "admin.permission:list", false));
        } else if ("teacher".equals(role)) {
            all.add(new MenuItem("我的首页", "bi-house-door", "/teacher/course?method=dashboard", "teacher.course:dashboard", false));
            all.add(new MenuItem("我的课程", "bi-journal-bookmark", "/teacher/course?method=list", "teacher.course:list", false));
            all.add(new MenuItem("补考录入", "bi-arrow-repeat", "/teacher/makeup?method=list", "teacher.makeup:list", false));
            all.add(new MenuItem("个人信息", "bi-person-lines-fill", "/user?method=toProfile", "user:toProfile", false));
            all.add(new MenuItem("修改密码", "bi-key", "/views/change_password.jsp", "user:changePassword", false));
        } else if ("student".equals(role)) {
            all.add(new MenuItem("我的首页", "bi-house-door", "/student/dashboard?method=index", "student.dashboard:index", false));
            all.add(new MenuItem("个人信息", "bi-person-lines-fill", "/student/profile?method=view", "student.profile:view", false));
            all.add(new MenuItem("选课中心", "bi-check2-square", "/student/course?method=list", "student.course:list", false));
            all.add(new MenuItem("我的课表", "bi-journal-bookmark", "/student/course?method=myCourses", "student.course:myCourses", false));
            all.add(new MenuItem("我的成绩", "bi-file-earmark-bar-graph", "/student/grade?method=list", "student.grade:list", false));
            all.add(new MenuItem("补考申请", "bi-arrow-repeat", "/student/makeup?method=list", "student.makeup:list", false));
            all.add(new MenuItem("修改密码", "bi-key", "/views/change_password.jsp", "user:changePassword", false));
        }

        PermissionService perm = PermissionService.getInstance();
        List<MenuItem> visible = new ArrayList<>();
        for (MenuItem item : all) {
            if (!perm.isAllowed(role, item.getCode())) {
                continue;
            }
            boolean active = item.getCode().equals(currentCode);
            visible.add(new MenuItem(item.getLabel(), item.getIcon(), item.getHref(), item.getCode(), active));
        }
        return visible;
    }
}