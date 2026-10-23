package com.lut.grade.controller;

import com.lut.grade.dao.StudentDao;
import com.lut.grade.model.SchoolClass;
import com.lut.grade.model.Student;
import com.lut.grade.model.User;
import com.lut.grade.service.ClassService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 学生端 - 个人信息查看与修改。
 * 学生只能改性别、电话、邮箱这类联系方式，学号/姓名/班级由管理员维护。
 */
@WebServlet("/student/profile")
public class StudentProfileServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StudentDao studentDao = new StudentDao();
    private final ClassService classService = new ClassService();

    public void view(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student student = currentStudent(req);
        if (student == null) {
            redirect(req, resp, "/student/dashboard?method=index",
                    "您的学生档案不存在，请联系管理员添加", "danger");
            return;
        }
        List<SchoolClass> classList = classService.getAllClasses();
        req.setAttribute("student", student);
        req.setAttribute("classList", classList);
        forward(req, resp, "student/student_profile.jsp");
    }

    public void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student student = currentStudent(req);
        if (student == null) {
            redirect(req, resp, "/student/dashboard?method=index",
                    "您的学生档案不存在，请联系管理员添加", "danger");
            return;
        }

        String gender = param(req, "gender");
        String phone = param(req, "phone");
        String email = param(req, "email");

        if (gender != null && !gender.isEmpty()
                && !"男".equals(gender) && !"女".equals(gender)) {
            redirect(req, resp, "/student/profile?method=view", "性别填写不正确", "danger");
            return;
        }
        if (phone != null && !phone.isEmpty() && !phone.matches("\\d{7,15}")) {
            redirect(req, resp, "/student/profile?method=view", "电话号码格式不正确", "danger");
            return;
        }
        if (email != null && !email.isEmpty()
                && !email.matches("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+")) {
            redirect(req, resp, "/student/profile?method=view", "邮箱格式不正确", "danger");
            return;
        }

        if (studentDao.updateContact(student.getId(), gender, phone, email)) {
            redirect(req, resp, "/student/profile?method=view", "个人信息已更新", "success");
        } else {
            redirect(req, resp, "/student/profile?method=view", "保存失败，请稍后重试", "danger");
        }
    }

    private Student currentStudent(HttpServletRequest req) {
        User user = currentUser(req);
        if (user == null) {
            return null;
        }
        return studentDao.findByUserId(user.getId());
    }
}