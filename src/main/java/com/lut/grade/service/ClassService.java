package com.lut.grade.service;

import com.lut.grade.dao.ClassDao;
import com.lut.grade.model.SchoolClass;
import java.util.List;

public class ClassService {
    private ClassDao classDao = new ClassDao();

    public List<SchoolClass> getAllClasses() {
        return classDao.findAll();
    }

    public SchoolClass getClassById(Integer id) {
        return classDao.findById(id);
    }

    public boolean addClass(SchoolClass c) {
        return classDao.insert(c);
    }

    public boolean updateClass(SchoolClass c) {
        return classDao.update(c);
    }

    public boolean deleteClass(Integer id) {
        return classDao.deleteById(id);
    }
}