package com.lut.grade.controller;

import com.lut.grade.model.Student;
import com.lut.grade.service.AccountService;
import com.lut.grade.service.ClassService;
import com.lut.grade.service.StudentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 管理员 - 学生管理。
 * 新增/删除都是"账号 + 档案"一起处理，不再只动 student 表。
 */
@WebServlet("/admin/student")
public class AdminStudentServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StudentService studentService = new StudentService();
    private final ClassService classService = new ClassService();
    private final AccountService accountService = new AccountService();

    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("studentList", studentService.getAllStudents());
        forward(req, resp, "admin/admin_student_list.jsp");
    }

    public void toAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("classList", classService.getAllClasses());
        req.setAttribute("defaultPassword", AccountService.DEFAULT_PASSWORD);
        forward(req, resp, "admin/admin_student_add.jsp");
    }

    public void add(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student student = extractStudent(req);
        String username = param(req, "username");
        String password = param(req, "password");

        if (isBlank(student.getStudentNumber())) {
            backToForm(req, resp, "学号不能为空", student, false);
            return;
        }
        if (isBlank(student.getName())) {
            backToForm(req, resp, "姓名不能为空", student, false);
            return;
        }
        if (isBlank(username)) {
            username = student.getStudentNumber();
        }

        String error = accountService.createStudent(username, password, student);
        if (error != null) {
            backToForm(req, resp, error, student, false);
            return;
        }
        redirect(req, resp, "/admin/student?method=list", "学生「" + student.getName() + "」添加成功", "success");
    }

    public void toEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = paramInt(req, "id");
        Student student = (id == null) ? null : studentService.getStudentById(id);
        if (student == null) {
            redirect(req, resp, "/admin/student?method=list", "学生不存在", "danger");
            return;
        }
        req.setAttribute("student", student);
        req.setAttribute("classList", classService.getAllClasses());
        forward(req, resp, "admin/admin_student_edit.jsp");
    }

    public void update(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = paramInt(req, "id");
        Student student = (id == null) ? null : studentService.getStudentById(id);
        if (student == null) {
            redirect(req, resp, "/admin/student?method=list", "学生不存在", "danger");
            return;
        }
        student.setStudentNumber(param(req, "studentNumber", student.getStudentNumber()));
        student.setName(param(req, "name", student.getName()));
        student.setClassId(paramInt(req, "classId"));
        student.setGender(param(req, "gender", student.getGender()));
        student.setPhone(param(req, "phone", student.getPhone()));
        student.setEmail(param(req, "email", student.getEmail()));

        if (studentService.updateStudent(student)) {
            redirect(req, resp, "/admin/student?method=list", "学生信息已更新", "success");
        } else {
            redirect(req, resp, "/admin/student?method=toEdit&id=" + id, "更新失败，请检查学号是否重复", "danger");
        }
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = paramInt(req, "id");
        Student student = (id == null) ? null : studentService.getStudentById(id);
        if (student == null) {
            redirect(req, resp, "/admin/student?method=list", "学生不存在", "danger");
            return;
        }
        // 有账号就删账号（student 行由外键级联删除），没有账号就直接删档案
        if (student.getUserId() != null) {
            String error = accountService.deleteAccount(student.getUserId());
            if (error != null) {
                redirect(req, resp, "/admin/student?method=list", error, "danger");
                return;
            }
        } else {
            studentService.deleteStudent(id);
        }
        redirect(req, resp, "/admin/student?method=list", "学生「" + student.getName() + "」已删除", "success");
    }

    public void print(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("studentList", studentService.getAllStudents());
        req.setAttribute("printDate", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
        forward(req, resp, "admin/print_student_list.jsp");
    }

    private Student extractStudent(HttpServletRequest req) {
        Student s = new Student();
        s.setStudentNumber(param(req, "studentNumber"));
        s.setName(param(req, "name"));
        s.setClassId(paramInt(req, "classId"));
        s.setGender(param(req, "gender", "男"));
        s.setPhone(param(req, "phone"));
        s.setEmail(param(req, "email"));
        return s;
    }

    private void backToForm(HttpServletRequest req, HttpServletResponse resp,
                            String msg, Student student, boolean edit)
            throws ServletException, IOException {
        req.setAttribute("msg", msg);
        req.setAttribute("msgType", "danger");
        req.setAttribute("student", student);
        req.setAttribute("classList", classService.getAllClasses());
        req.setAttribute("defaultPassword", AccountService.DEFAULT_PASSWORD);
        forward(req, resp, edit ? "admin/admin_student_edit.jsp" : "admin/admin_student_add.jsp");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}