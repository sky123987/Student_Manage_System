package com.lut.grade.util;

import java.security.MessageDigest;

public class MD5Util {
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 加盐 (Salt) 提高安全性
            md.update("lut_grade_system".getBytes());
            byte[] digest = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}