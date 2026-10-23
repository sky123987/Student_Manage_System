package com.lut.grade.service;

import com.lut.grade.dao.CourseDao;
import com.lut.grade.dao.TeacherDao;
import com.lut.grade.model.Course;
import com.lut.grade.model.Teacher;
import com.lut.grade.dao.CourseGradeTemplateDao;
import com.lut.grade.model.CourseGradeTemplate;
import java.util.List;
import java.util.Map;

public class CourseService {
    // 实例化 DAO 层
    private CourseDao courseDao = new CourseDao();
    private TeacherDao teacherDao = new TeacherDao();
    private CourseGradeTemplateDao templateDao = new CourseGradeTemplateDao();
    // ==========================================
    // 1. 教师端相关的业务方法
    // ==========================================

    // 获取某位教师的授课列表
    public List<Course> getCoursesByTeacherId(Integer teacherId) {
        return courseDao.findByTeacherId(teacherId);
    }

    // 统计教师授课总数
    public int countCoursesByTeacherId(Integer teacherId) {
        return courseDao.countCoursesByTeacherId(teacherId);
    }

    // 统计教师所授学生总数
    public int countStudentsByTeacherId(Integer teacherId) {
        return courseDao.countStudentsByTeacherId(teacherId);
    }

    // 统计待录入成绩总数
    public int countPendingGradesByTeacherId(Integer teacherId) {
        return courseDao.countPendingGradesByTeacherId(teacherId);
    }

    // 获取教师待办任务列表（动态读取课程名、班级名、待录入人数）
    public List<Map<String, Object>> getPendingTasksByTeacherId(Integer teacherId) {
        return courseDao.getPendingTasksByTeacherId(teacherId);
    }

    // ==========================================
    // 2. 管理员端相关的业务方法
    // ==========================================

    // 查询所有课程（带教师名字，用于管理员列表展示）
    public List<Course> getAllCoursesWithTeacher() {
        return courseDao.findAllWithTeacher();
    }

    // 根据ID查询单门课程（用于修改页面回显）
    public Course getCourseById(Integer id) {
        return courseDao.findById(id);
    }

    // 新增课程（管理员分配教师）
    public boolean addCourse(Course c) {
        return courseDao.insert(c);
    }

    // 修改课程
    public boolean updateCourse(Course c) {
        return courseDao.update(c);
    }

    // 删除课程
    public boolean deleteCourse(Integer id) {
        return courseDao.deleteById(id);
    }

    // ==========================================
    // 3. 公共业务方法
    // ==========================================

    // 获取所有教师列表（用于下拉框选择）
    public List<Teacher> getAllTeachers() {
        return teacherDao.findAll();
    }
    public CourseGradeTemplate getTemplateByCourseId(Integer courseId) {
        return templateDao.getTemplateByCourseId(courseId);
    }

    // 保存/更新某门课程的成绩模板（管理员配置时调用）
    public boolean saveTemplate(CourseGradeTemplate template) {
        // 简单校验：平时比例 + 期末比例必须等于 100，且允许补考的标志位合法
        if (template.getDailyRatio() + template.getFinalRatio() != 100) {
            return false; // 比例不合法，拒绝保存
        }
        if (template.getAllowMakeup() == null || (template.getAllowMakeup() != 0 && template.getAllowMakeup() != 1)) {
            template.setAllowMakeup(1); // 默认允许补考
        }
        return templateDao.saveTemplate(template);
    }
}