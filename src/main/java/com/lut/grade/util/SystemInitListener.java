package com.lut.grade.util;

import com.lut.grade.dao.UserDao;
import com.lut.grade.model.User;
import com.lut.grade.service.PermissionService;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * 应用启动时的初始化：
 * 1) 升级数据库结构（permission / role_permission 表、user.avatar 列）并灌入默认权限；
 * 2) 保证存在默认管理员账号 admin / 123456。
 * 两步都是幂等的，重复启动不会重复插入。
 */
@WebListener
public class SystemInitListener implements ServletContextListener {

    private final UserDao userDao = new UserDao();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 先做权限体系初始化（建表 + 同步权限目录 + 默认角色权限）
        PermissionService.getInstance().bootstrap();

        try {
            if (userDao.findByUsername("admin") == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(MD5Util.getMD5("123456"));
                admin.setRole("admin");
                userDao.insertUser(admin);
                System.out.println("系统初始化：默认管理员 admin 创建成功！");
            }
        } catch (Exception e) {
            System.err.println("系统初始化：默认管理员创建失败 —— " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 无需清理
    }
}