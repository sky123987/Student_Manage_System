package com.lut.grade.model;

/**
 * 一条权限：权限码（模块:方法）、中文名、所属模块。
 * 只用于权限管理页面的展示和排序。
 */
public class Permission {
    private final String code;
    private final String name;
    private final String module;

    public Permission(String code, String name, String module) {
        this.code = code;
        this.name = name;
        this.module = module;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getModule() { return module; }
}