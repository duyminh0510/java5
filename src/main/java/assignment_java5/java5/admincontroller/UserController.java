package assignment_java5.java5.admincontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.entitys.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private UserDAO dao;

    @RequestMapping("/user/index")
    public String index(Model model) {
        User item = new User();
        model.addAttribute("item", item);
        List<User> items = dao.findAll();
        model.addAttribute("items", items);
        return "views/gdienAdmins/users";
    }

    @RequestMapping("/user/edit/{id}")
    public String eidt(Model model, @PathVariable("id") String id) {
        User item = dao.findById(id).orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + id));

        model.addAttribute("item", item);
        List<User> items = dao.findAll();
        model.addAttribute("items", items);
        return "views/gdienAdmins/users";
    }

    @RequestMapping("/user/create")
    public String create(RedirectAttributes redirectAttributes, Model model,
            @Valid @ModelAttribute("item") User item,
            Errors errors) {
        try {
            if (errors.hasErrors()) {
                model.addAttribute("item", item);
                return "views/gdienAdmins/users"; // Trả về trang thay vì redirect
            } else {
                dao.save(item);
                redirectAttributes.addFlashAttribute("success", "Thêm thông tin thành công!");
            }
        } catch (Exception e) {
            log.error("Lỗi khi Thêm thông tin: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau!");
        }
        return "redirect:/user/index";
    }

    @RequestMapping("/user/update")
    public String update(RedirectAttributes redirectAttributes, Model model,
            @Valid @ModelAttribute("item") User item, Errors errors) {
        try {

            if (errors.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng sửa các lỗi sau!");
                return "views/gdienAdmins/users";
            }

            // Kiểm tra xem user có tồn tại trong database không
            User existingUser = dao.findById(item.getUserId())
                    .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + item.getUserId()));

            User userWithEmail = dao.findByEmail(item.getEmail());
            if (userWithEmail != null && !userWithEmail.getUserId().equals(item.getUserId())) {
                redirectAttributes.addFlashAttribute("error", "Email này đã được sử dụng!");
                return "redirect:/user/index";
            }

            // Cập nhật thông tin của user hiện tại
            existingUser.setUsername(item.getUsername());
            existingUser.setEmail(item.getEmail());
            existingUser.setPhone(item.getPhone());
            existingUser.setAddress(item.getAddress());
            existingUser.setActive(item.isActive());
            existingUser.setRole(item.getRole());

            // Nếu mật khẩu không bị thay đổi, giữ nguyên
            if (item.getPassword() != null && !item.getPassword().isEmpty()) {
                existingUser.setPassword(item.getPassword());
            }

            dao.save(existingUser);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");

        } catch (Exception e) {
            log.error("Lỗi khi cập nhật thông tin: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau!");
        }
        return "redirect:/user/index";
    }

    @RequestMapping("/user/deactivate/{id}")
    public String delete(RedirectAttributes redirectAttributes, @PathVariable("id") String id) {
        User item = dao.findById(id).get();
        if (item != null) {
            item.setActive(false);
            dao.save(item);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật thông tin thành công!");
        }
        return "redirect:/user/index";
    }

    @RequestMapping("/user/truectivate/{id}")
    public String deleteitem(RedirectAttributes redirectAttributes, @PathVariable("id") String id) {
        User item = dao.findById(id).get();
        if (item != null) {
            item.setActive(true);
            dao.save(item);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật thông tin thành công!");
        }
        return "redirect:/user/index";
    }

}
