package com.lut.grade.service;

import com.lut.grade.dao.TeacherDao;
import com.lut.grade.model.Teacher;
import java.util.List;

public class TeacherService {
    private TeacherDao teacherDao = new TeacherDao();

    public List<Teacher> getAllTeachers() {
        return teacherDao.findAll();
    }

    public Teacher getTeacherById(Integer id) {
        return teacherDao.findById(id);
    }

    public boolean addTeacher(Teacher t) {
        return teacherDao.insert(t);
    }

    public boolean updateTeacher(Teacher t) {
        return teacherDao.update(t);
    }

    public boolean deleteTeacher(Integer id) {
        return teacherDao.deleteById(id);
    }
}