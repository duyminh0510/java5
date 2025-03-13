package assignment_java5.java5.admincontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import assignment_java5.java5.dao.CartDAO;
import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.entitys.Cart;
import assignment_java5.java5.entitys.User;
import jakarta.validation.Valid;

@Controller
public class DangkyController {
    @Autowired
    private UserDAO dao;

    @Autowired
    private CartDAO daocart;

    @GetMapping(value = "/dangky", produces = "text/html; charset=UTF-8")
    public String showRegisterPage(Model model) {
        model.addAttribute("newusers", new User());
        return "views/gdienAdmins/dangky";
    }

    @PostMapping(value = "/dangky/form", produces = "text/html; charset=UTF-8")
    public String processRegistration(
            @Valid @ModelAttribute("newusers") User newusers,
            Errors errors, Model model) {

        // Nếu có lỗi validation, trả lại trang đăng ký ngay lập tức
        if (errors.hasErrors()) {
            model.addAttribute("message", "Vui lòng sửa các lỗi sau!");
            return "views/gdienAdmins/dangky";
        }

        try {
            User saveUser = dao.save(newusers);

            Cart newCart = Cart.builder()
                    .user(saveUser)
                    .build();

            daocart.save(newCart);
            model.addAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
            return "redirect:/dangnhap";
        } catch (Exception e) {
            model.addAttribute("errorrr", "Có lỗi xảy ra, vui lòng thử lại sau!");
            return "views/gdienAdmins/dangky";
        }
    }
}
