package assignment_java5.java5.admincontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import assignment_java5.java5.dao.CategoryDAO;
import assignment_java5.java5.dao.ProductDAO;
import assignment_java5.java5.entitys.Category;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryDAO dao;

    @Autowired
    private ProductDAO productdao;

    @RequestMapping("/category/index")
    public String index(Model model) {
        Category item = new Category();
        model.addAttribute("item", item);
        List<Category> items = dao.findAll();
        model.addAttribute("items", items);
        return "views/gdienAdmins/categories";
    }

    @RequestMapping("/category/edit/{id}")
    public String eidt(Model model, @PathVariable("id") String id) {
        Category item = dao.findById(id).get();
        model.addAttribute("item", item);
        List<Category> items = dao.findAll();
        model.addAttribute("items", items);
        return "views/gdienAdmins/categories";
    }

    @RequestMapping("/category/create")
    public String create(Category item, RedirectAttributes redirectAttributes) {
        try {
            if (item.getCategoryId().isEmpty() || item.getName().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            } else {
                dao.save(item);
                redirectAttributes.addFlashAttribute("success", "Đã thêm danh mục thành công!");
            }

        } catch (Exception e) {
            log.error("Lỗi khi thêm danh mục: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau!");
        }
        return "redirect:/category/index";
    }

    @RequestMapping("/category/update")
    public String update(Category item, RedirectAttributes redirectAttributes) {
        try {
            if (item.getCategoryId().isEmpty() || item.getName().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            } else {
                dao.save(item);
                redirectAttributes.addFlashAttribute("success", "Đã cập nhật danh mục thành công!");
            }

        } catch (Exception e) {
            log.error("Lỗi khi thêm danh mục: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau!");
        }
        return "redirect:/category/edit/" + item.getCategoryId();
    }

    @RequestMapping("/category/delete/{id}")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        Optional<Category> categoryOpt = dao.findById(id);
        if (!categoryOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Danh mục không tồn tại!");
            return "redirect:/category/index";
        }

        Category category = categoryOpt.get();
        if (productdao.existsByCategory(category)) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục vì còn sản phẩm liên quan!");
            return "redirect:/category/index";
        }

        dao.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa danh mục thành công!");
        return "redirect:/category/index";
    }

}
