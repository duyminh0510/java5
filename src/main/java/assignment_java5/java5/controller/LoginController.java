package assignment_java5.java5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.entitys.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserDAO userDAO;

    @RequestMapping("/dangnhap")
    public String loginPage() {
        return "views/gdienUsers/dangnhap";
    }

    @PostMapping("/dangnhap/form")
    public String login(Model model, HttpServletRequest request, HttpSession session) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập tài khoản!");
            return "views/gdienUsers/dangnhap";
        }
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập mật khẩu!");
            return "views/gdienUsers/dangnhap";
        }

        User user = userDAO.findByPhoneOrEmail(name, name);
        if (user == null) {
            model.addAttribute("error", "Tài khoản không tồn tại!");
            return "views/gdienUsers/dangnhap";
        }

        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Mật khẩu không chính xác!");
            return "views/gdienUsers/dangnhap";
        }

        if (!user.isActive()) {
            model.addAttribute("error", "Tài khoản đã bị vô hiệu hóa!");
            return "views/gdienUsers/dangnhap";
        }

        String role = user.getRole().toString();
        session.setAttribute("role", role);

        // Lưu thông tin vào session
        session.setAttribute("loggedInUser", user);
        session.setAttribute("username", user.getUsername()); // Đảm bảo tồn tại
        session.setAttribute("role", role);

        logger.info("User logged in: {}", user.getUsername());
        logger.info("Assigned role: {}", role);
        logger.info("Session saved - username: {}", session.getAttribute("username"));

        String redirectUri = (String) session.getAttribute("redirectAfterLogin");

        if (redirectUri != null && !redirectUri.equals("/dangnhap")) {
            session.removeAttribute("redirectAfterLogin");
            return "redirect:" + redirectUri;
        }

        if (role.equals("ADMIN")) {
            return "redirect:/";
        } else if (role.equals("SHIPPER")) {
            return "redirect:/shipperpage/home";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/dangnhap";
    }
}
