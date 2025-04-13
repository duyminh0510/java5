package assignment_java5.java5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import assignment_java5.java5.dao.ShipperDAO;
import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.dto.HosoUpdateDTO;
import assignment_java5.java5.entitys.Shipper;
import assignment_java5.java5.entitys.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HosoController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ShipperDAO shipperDAO;

    @RequestMapping("/hosocanhan")
    public String showProfileForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Object loggedIn = session.getAttribute("loggedInUser");

        if (loggedIn == null) {
            session.setAttribute("redirectAfterLogin", "/hosocanhan");
            return "redirect:/dangnhap";
        }

        HosoUpdateDTO dto = new HosoUpdateDTO();

        if (loggedIn instanceof User user) {
            dto.setUsername(user.getUsername());
            dto.setPhone(user.getPhone());
            dto.setEmail(user.getEmail());
            dto.setAddress(user.getAddress());
        } else if (loggedIn instanceof Shipper shipper) {
            dto.setUsername(shipper.getFullName());
            dto.setPhone(shipper.getPhoneNumber());
            dto.setEmail(shipper.getEmail());
            dto.setAddress(shipper.getArea());
        } else {
            redirectAttributes.addFlashAttribute("error", "Không xác định được người dùng.");
            return "redirect:/";
        }

        model.addAttribute("hosoUpdateDTO", dto);
        return "views/gdienUsers/hosoupdate";
    }

    @RequestMapping("/hosocanhan/update")
    public String updateProfile(
            @Valid @ModelAttribute("hosoUpdateDTO") HosoUpdateDTO dto,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        Object loggedIn = session.getAttribute("loggedInUser");

        if (loggedIn == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            session.setAttribute("redirectAfterLogin", "/hosocanhan/update");
            return "redirect:/dangnhap";
        }

        if (result.hasErrors()) {
            model.addAttribute("hosoUpdateDTO", dto);
            model.addAttribute("error", result.getFieldError().getDefaultMessage());
            return "views/gdienUsers/hosoupdate";
        }

        if (loggedIn instanceof User user) {
            user.setUsername(dto.getUsername());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setAddress(dto.getAddress());

            userDAO.save(user);
            session.setAttribute("loggedInUser", user);
        } else if (loggedIn instanceof Shipper shipper) {
            shipper.setFullName(dto.getUsername());
            shipper.setPhoneNumber(dto.getPhone());
            shipper.setEmail(dto.getEmail());
            shipper.setArea(dto.getAddress());

            shipperDAO.save(shipper);
            session.setAttribute("loggedInUser", shipper);
        } else {
            redirectAttributes.addFlashAttribute("error", "Không xác định được người dùng.");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        return "redirect:/hosocanhan";
    }
}
