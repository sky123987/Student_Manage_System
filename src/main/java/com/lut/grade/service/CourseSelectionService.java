package com.lut.grade.service;

import com.lut.grade.dao.CourseSelectionDao;
import com.lut.grade.model.Course;
import java.util.List;

public class CourseSelectionService {
    private CourseSelectionDao csDao = new CourseSelectionDao();

    public List<Course> getAvailableCourses(Integer studentId) {
        return csDao.findAllCoursesWithSelectionStatus(studentId);
    }

    public boolean selectCourse(Integer studentId, Integer courseId) {
        return csDao.insertSelection(studentId, courseId);
    }

    public List<Course> getMyCourses(Integer studentId) {
        return csDao.findMySelectedCourses(studentId);
    }

    public boolean dropCourse(Integer studentId, Integer courseId) {
        return csDao.deleteSelection(studentId, courseId);
    }
}