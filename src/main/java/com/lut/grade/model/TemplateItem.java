package com.lut.grade.model;

public class TemplateItem {
    private Integer id;
    private Integer templateId;  // 关联的模板ID
    private String itemName;     // 子项名称 (如 "考勤", "作业")
    private Integer itemRatio;   // 子项比例 (如 20, 30)

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTemplateId() { return templateId; }
    public void setTemplateId(Integer templateId) { this.templateId = templateId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public Integer getItemRatio() { return itemRatio; }
    public void setItemRatio(Integer itemRatio) { this.itemRatio = itemRatio; }
}