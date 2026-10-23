package com.lut.grade.model;

import java.util.List;

public class CourseGradeTemplate {
    private Integer id;
    private Integer courseId;       // 关联的课程ID
    private Integer dailyRatio;     // 平时成绩总比例 (如 40)
    private Integer finalRatio;     // 期末成绩比例 (如 60)
    private Integer allowMakeup;    // 是否允许补考 (1允许，0不允许)

    // 附加属性：存放该模板下的所有平时分子项
    private List<TemplateItem> itemList;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public Integer getDailyRatio() { return dailyRatio; }
    public void setDailyRatio(Integer dailyRatio) { this.dailyRatio = dailyRatio; }
    public Integer getFinalRatio() { return finalRatio; }
    public void setFinalRatio(Integer finalRatio) { this.finalRatio = finalRatio; }
    public Integer getAllowMakeup() { return allowMakeup; }
    public void setAllowMakeup(Integer allowMakeup) { this.allowMakeup = allowMakeup; }
    public List<TemplateItem> getItemList() { return itemList; }
    public void setItemList(List<TemplateItem> itemList) { this.itemList = itemList; }
}