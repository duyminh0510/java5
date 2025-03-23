package assignment_java5.java5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.dto.HosoUpdateDTO;
import assignment_java5.java5.entitys.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HosoController {

    @Autowired
    protected UserDAO dao;

    @RequestMapping("/hosocanhan")
    public String requestMethodName(Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/hosocanhan");
            return "redirect:/dangnhap";
        }

        HosoUpdateDTO dto = new HosoUpdateDTO();
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());

        model.addAttribute("hosoUpdateDTO", dto);
        return "views/gdienUsers/hosoupdate";
    }

    @SuppressWarnings("null")
    @RequestMapping("/hosocanhan/update")
    public String updatematkhau(
            @Valid @ModelAttribute("hosoUpdateDTO") HosoUpdateDTO dto,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            session.setAttribute("redirectAfterLogin", "/hosocanhan/update");
            return "redirect:/dangnhap";
        }

        if (result.hasErrors()) {
            model.addAttribute("hosoUpdateDTO", dto);
            model.addAttribute("error", result.getFieldError().getDefaultMessage());
            return "views/gdienUsers/hosoupdate";
        }

        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());

        dao.save(user);

        session.setAttribute("loggedInUser", user);

        redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        return "redirect:/hosocanhan";
    }

}
