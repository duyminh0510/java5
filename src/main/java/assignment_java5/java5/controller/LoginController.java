package assignment_java5.java5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.entitys.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    @Autowired
    private UserDAO dao;
    // @Autowired
    // HttpServletRequest req;
    // @Autowired
    // HttpServletResponse resp;
    // @Autowired
    // HttpSession session;

    @RequestMapping("/dangnhap")
    public String requestMethodName() {
        return "views/gdienUsers/dangnhap";
    }

    @PostMapping("/dangnhap/form")
    public String postMethodName(Model model, HttpServletRequest req,
            HttpServletResponse resp, HttpSession session) {
        String name = req.getParameter("name");
        String password = req.getParameter("password");

        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập tài khoản trước khi đăng nhập!");
            return "views/gdienUsers/dangnhap";
        }

        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập mật khẩu trước khi đăng nhập!");
            return "views/gdienUsers/dangnhap";
        }

        // Tìm người dùng theo số điện thoại hoặc email
        User user = dao.findByPhoneOrEmail(name, name);

        // Kiểm tra tài khoản có tồn tại không
        if (user == null) {
            model.addAttribute("error", "Tài khoản của bạn không hợp lệ!");
            return "views/gdienUsers/dangnhap";
        }

        // Kiểm tra mật khẩu có đúng không
        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Mật khẩu không chính xác!");
            return "views/gdienUsers/dangnhap";
        }

        if (!user.isActive()) {
            model.addAttribute("error", "Tài khoản của bạn đã bị vô hiệu hóa, vui lòng liên hệ quản trị viên!");
            return "views/gdienUsers/dangnhap";
        }

        session.setAttribute("loggedInUser", user);

        String role = (user.getRole() != null && user.getRole()) ? "admin" : "user";
        session.setAttribute("role", role);
        System.out.println("Role: " + role);

        String redirectUri = (String) session.getAttribute("redirectAfterLogin");

        if (redirectUri != null) {
            session.removeAttribute("redirectAfterLogin");
            return "redirect:" + redirectUri;
        }
        return "redirect:/";
    }

}
