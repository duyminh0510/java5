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
import assignment_java5.java5.dao.ShipperDAO;
import assignment_java5.java5.dto.ChangePasswordDTO;
import assignment_java5.java5.entitys.User;
import assignment_java5.java5.entitys.Shipper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ChangePassController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ShipperDAO shipperDAO;

    @RequestMapping("/doimatkhau")
    public String showChangePasswordForm(Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Object loggedIn = session.getAttribute("loggedInUser");

        if (loggedIn == null) {
            session.setAttribute("redirectAfterLogin", "/doimatkhau");
            return "redirect:/dangnhap";
        }

        if (!model.containsAttribute("changePasswordDTO")) {
            model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        }

        return "views/gdienUsers/doimatkhau";
    }

    @PostMapping("/doimatkhau/update")
    public String changePassword(
            @Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO dto,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object loggedIn = session.getAttribute("loggedInUser");

        if (loggedIn == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            session.setAttribute("redirectAfterLogin", "/doimatkhau");
            return "redirect:/dangnhap";
        }

        // Kiểm tra lỗi validate
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO",
                    result);
            redirectAttributes.addFlashAttribute("changePasswordDTO", dto);
            return "redirect:/doimatkhau";
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/doimatkhau";
        }

        // Xử lý cho User
        if (loggedIn instanceof User user) {
            if (!user.getPassword().equals(dto.getOldPassword())) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không đúng!");
                return "redirect:/doimatkhau";
            }

            user.setPassword(dto.getNewPassword());
            userDAO.save(user);
            session.setAttribute("loggedInUser", user);

        }
        // Xử lý cho Shipper
        else if (loggedIn instanceof Shipper shipper) {
            if (!shipper.getPassword().equals(dto.getOldPassword())) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không đúng!");
                return "redirect:/doimatkhau";
            }

            shipper.setPassword(dto.getNewPassword());
            shipperDAO.save(shipper);
            session.setAttribute("loggedInUser", shipper);
        }

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/doimatkhau";
    }
}
