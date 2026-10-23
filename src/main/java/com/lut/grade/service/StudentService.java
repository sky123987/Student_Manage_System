package com.lut.grade.service;

import com.lut.grade.dao.StudentDao;
import com.lut.grade.model.Student;

import java.util.List;

public class StudentService {
    private StudentDao studentDao = new StudentDao();

    public List<Student> getAllStudents() {
        return studentDao.findAll();
    }

    public Student getStudentById(Integer id) {
        return studentDao.findById(id);
    }

    public boolean addStudent(Student student) {
        return studentDao.insert(student);
    }

    public boolean updateStudent(Student student) {
        return studentDao.update(student);
    }

    public boolean deleteStudent(Integer id) {
        return studentDao.deleteById(id);
    }
}