package com.lut.grade.service;

import com.lut.grade.dao.UserDao;
import com.lut.grade.model.User;
import com.lut.grade.util.MD5Util;

public class UserService {
    private UserDao userDao = new UserDao();

    public User login(String username, String password) {
        // 将前端传来的明文密码，加密后去数据库比对
        String md5Pwd = MD5Util.getMD5(password);
        return userDao.findByUsernameAndPassword(username, md5Pwd);
    }

    // 在 UserService.java 中修改 register 方法
    public boolean register(User user) {
        // 1. 先检查用户名是否已存在
        if (userDao.findByUsername(user.getUsername()) != null) {
            return false; // 账号已存在，注册失败
        }

        // 2. 密码加密
        user.setPassword(MD5Util.getMD5(user.getPassword()));

        // 3. 插入数据库
        return userDao.insertUser(user);
    }
    /**
     * 修改密码（带旧密码校验）
     * @param user   当前登录的 User 对象（从 Session 中获取，包含加密后的密码）
     * @param oldPwd 用户输入的旧密码（明文）
     * @param newPwd 用户输入的新密码（明文）
     * @return true 修改成功；false 旧密码错误或修改失败
     */
    public boolean changePassword(User user, String oldPwd, String newPwd) {
        // 1. 校验旧密码：将用户输入的旧密码 MD5 加密后，与 Session 中的密码比对
        if (!user.getPassword().equals(MD5Util.getMD5(oldPwd))) {
            return false; // 旧密码输入错误
        }

        // 2. 更新数据库中的密码
        return userDao.updatePassword(user.getId(), MD5Util.getMD5(newPwd));
    }

}