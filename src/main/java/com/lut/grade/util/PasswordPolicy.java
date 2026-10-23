package com.lut.grade.util;

/**
 * 密码强度规则（注册 / 修改密码共用）。
 *
 * 规则：长度至少 6 位，并且必须同时包含「字母」和「数字」。
 * 例：abc123、A1b2c3 通过；123456、abcdef、abc12 不通过。
 *
 * 注意：前端的 register.jsp / change_password.jsp 里有一份等价的 JavaScript 校验，
 * 只负责弹出提示、提升体验；真正的把关点始终是这里（后端）。
 * 两边规则若要调整，必须同时改，否则会出现"前端放行、后端报错"的不一致。
 */
public final class PasswordPolicy {

    /** 密码最小长度 */
    public static final int MIN_LENGTH = 6;

    /** 给页面展示的规则说明，前端文案与之保持一致 */
    public static final String RULE_TEXT = "密码至少 6 位，且必须同时包含字母和数字";

    private PasswordPolicy() {
    }

    /**
     * 校验密码。
     *
     * @param password 明文密码
     * @return null 表示符合规则；否则返回给用户看的失败原因（可直接拼在"新"字后面）
     */
    public static String check(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空";
        }
        String pwd = password.trim();
        if (pwd.length() < MIN_LENGTH) {
            return "密码长度不能少于 " + MIN_LENGTH + " 位";
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < pwd.length(); i++) {
            char c = pwd.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetter = true;
            } else if (c >= '0' && c <= '9') {
                hasDigit = true;
            }
        }
        if (!hasLetter || !hasDigit) {
            return "密码必须同时包含字母和数字，例如 abc123";
        }
        return null;
    }

    /** 是否符合密码规则 */
    public static boolean isValid(String password) {
        return check(password) == null;
    }
}