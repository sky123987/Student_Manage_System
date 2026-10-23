package com.lut.grade.model;

/** 侧边栏菜单项：由角色权限动态过滤后生成 */
public class MenuItem {

    private final String label;
    private final String icon;
    private final String href;
    private final String code;
    private final boolean active;

    public MenuItem(String label, String icon, String href, String code, boolean active) {
        this.label = label;
        this.icon = icon;
        this.href = href;
        this.code = code;
        this.active = active;
    }

    public String getLabel() { return label; }
    public String getIcon() { return icon; }
    public String getHref() { return href; }
    public String getCode() { return code; }
    public boolean isActive() { return active; }
}