package assignment_java5.java5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.dto.ChangePasswordDTO;
import assignment_java5.java5.entitys.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ChangePassController {

    @Autowired
    private UserDAO userdao;

    @RequestMapping("/doimatkhau")
    public String requestMethodName(Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/doimatkhau");
            return "redirect:/dangnhap";
        }
        return "views/gdienUsers/doimatkhau";
    }

    @SuppressWarnings("null")
    @PostMapping("/doimatkhau/update")
    public String changePassword(
            @Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO dto,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            session.setAttribute("redirectAfterLogin", "/doimatkhau");
            return "redirect:/dangnhap";
        }

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "redirect:/doimatkhau";
        }

        // Kiểm tra mật khẩu cũ (cần sử dụng PasswordEncoder nếu đã mã hóa)
        if (!dto.getOldPassword().equals(user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không đúng!");
            return "redirect:/doimatkhau";
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/doimatkhau";
        }

        // Cập nhật mật khẩu mới
        user.setPassword(dto.getNewPassword());
        userdao.save(user);

        // Cập nhật session
        session.setAttribute("loggedInUser", user);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/doimatkhau";
    }
}
