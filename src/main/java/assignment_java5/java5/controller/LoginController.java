package assignment_java5.java5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import assignment_java5.java5.dao.ShipperDAO;
import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.entitys.Shipper;
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

    @Autowired
    private ShipperDAO shipperDAO;

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
        Shipper shipper = shipperDAO.findByPhoneNumberOrEmail(name, name);

        if (user == null && shipper == null) {
            model.addAttribute("error", "Tài khoản không tồn tại!");
            return "views/gdienUsers/dangnhap";
        }

        if (user != null) {
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
            session.setAttribute("loggedInUser", user);
            session.setAttribute("username", user.getUsername());

            logger.info("User logged in: {}", user.getUsername());
            logger.info("Assigned role: {}", role);

            // Check redirect after login
            String redirectUri = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUri != null && !redirectUri.equals("/dangnhap")) {
                session.removeAttribute("redirectAfterLogin");
                return "redirect:" + redirectUri;
            }

            if (role.equals("ADMIN")) {
                return "redirect:/";
            } else if (role.equals("SHIPPER")) {
                return "redirect:/shipper/order";
            } else {
                return "redirect:/";
            }
        }

        if (shipper != null) {
            if (!shipper.getPassword().equals(password)) {
                model.addAttribute("error", "Mật khẩu không chính xác!");
                return "views/gdienUsers/dangnhap";
            }

            if (shipper.getStatus() == Shipper.Status.PENDING) {
                model.addAttribute("error", "Tài khoản của bạn chưa được xác nhận, vui lòng quay lại sau!");
                return "views/gdienUsers/dangnhap";
            }

            String role = shipper.getRole().toString();
            session.setAttribute("role", role);
            session.setAttribute("loggedInUser", shipper);
            session.setAttribute("username", shipper.getFullName());

            logger.info("Shipper logged in: {}", shipper.getEmail());
            logger.info("Assigned role: {}", role);

            String redirectUri = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUri != null && !redirectUri.equals("/dangnhap")) {
                session.removeAttribute("redirectAfterLogin");
                return "redirect:" + redirectUri;
            }

            return "redirect:/shipper/order";
        }

        model.addAttribute("error", "Đăng nhập thất bại!");
        return "views/gdienUsers/dangnhap";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/dangnhap";
    }
}
