package com.lut.grade.controller;

import com.lut.grade.model.Course;
import com.lut.grade.model.CourseGradeTemplate;
import com.lut.grade.model.TemplateItem;
import com.lut.grade.service.CourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员端 - 课程成绩模板（平时/期末比例、平时子项比例、是否允许补考）。
 */
@WebServlet("/admin/template")
public class AdminTemplateServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final CourseService courseService = new CourseService();

    public void toConfig(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/admin/course?method=list", "请先选择课程", "warning");
            return;
        }
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            redirect(req, resp, "/admin/course?method=list", "课程不存在", "danger");
            return;
        }
        req.setAttribute("course", course);
        req.setAttribute("template", courseService.getTemplateByCourseId(courseId));
        forward(req, resp, "admin/admin_template_config.jsp");
    }

    public void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer courseId = paramInt(req, "courseId");
        if (courseId == null) {
            redirect(req, resp, "/admin/course?method=list", "缺少课程 id", "danger");
            return;
        }
        String backTo = "/admin/template?method=toConfig&courseId=" + courseId;

        Integer dailyRatio = paramInt(req, "dailyRatio");
        Integer finalRatio = paramInt(req, "finalRatio");
        Integer allowMakeup = paramInt(req, "allowMakeup");
        if (dailyRatio == null || finalRatio == null) {
            redirect(req, resp, backTo, "平时比例和期末比例都要填写", "danger");
            return;
        }
        if (dailyRatio < 0 || finalRatio < 0 || dailyRatio + finalRatio != 100) {
            redirect(req, resp, backTo, "平时成绩比例与期末成绩比例之和必须等于 100%", "danger");
            return;
        }

        String[] itemNames = req.getParameterValues("itemName");
        String[] itemRatios = req.getParameterValues("itemRatio");
        List<TemplateItem> itemList = new ArrayList<>();
        if (itemNames != null && itemRatios != null) {
            int n = Math.min(itemNames.length, itemRatios.length);
            for (int i = 0; i < n; i++) {
                String name = itemNames[i] == null ? "" : itemNames[i].trim();
                String ratioStr = itemRatios[i] == null ? "" : itemRatios[i].trim();
                if (name.isEmpty() || ratioStr.isEmpty()) {
                    continue;
                }
                try {
                    TemplateItem item = new TemplateItem();
                    item.setItemName(name);
                    item.setItemRatio(Integer.parseInt(ratioStr));
                    itemList.add(item);
                } catch (NumberFormatException e) {
                    redirect(req, resp, backTo, "平时分项「" + name + "」的比例不是合法数字", "danger");
                    return;
                }
            }
        }

        int itemRatioSum = 0;
        for (TemplateItem item : itemList) {
            itemRatioSum += item.getItemRatio();
        }
        if (itemRatioSum != dailyRatio) {
            redirect(req, resp, backTo,
                    "平时分项比例之和（" + itemRatioSum + "%）必须等于平时成绩总比例（" + dailyRatio + "%）", "danger");
            return;
        }

        CourseGradeTemplate template = new CourseGradeTemplate();
        template.setCourseId(courseId);
        template.setDailyRatio(dailyRatio);
        template.setFinalRatio(finalRatio);
        template.setAllowMakeup(allowMakeup == null ? 1 : allowMakeup);
        template.setItemList(itemList);

        if (courseService.saveTemplate(template)) {
            redirect(req, resp, "/admin/course?method=list", "成绩模板已保存", "success");
        } else {
            redirect(req, resp, backTo, "保存失败，请稍后重试", "danger");
        }
    }
}