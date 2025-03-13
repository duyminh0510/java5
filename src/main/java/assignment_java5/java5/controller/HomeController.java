package assignment_java5.java5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import assignment_java5.java5.dao.CategoryDAO;
import assignment_java5.java5.dao.ProductDAO;
import assignment_java5.java5.entitys.Product;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    private ProductDAO dao;

    @Autowired
    private CategoryDAO categoryDAO; // Giả sử bạn có CategoryDAO để lấy danh sách danh mục

    @RequestMapping("/")
    public String home(Model model, HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String categoryId) { // Nhận categoryId từ request

        String role = (String) session.getAttribute("role");
        if (role == null) {
            role = "user";
            session.setAttribute("role", role);
        }
        model.addAttribute("role", role);

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Product> productPage;
        if (categoryId != null) {
            productPage = dao.findByCategoryId(categoryId, pageable);
        } else {
            productPage = dao.findAll(pageable);
        }

        if (page > productPage.getTotalPages() && productPage.getTotalPages() > 0) {
            page = productPage.getTotalPages();
            pageable = PageRequest.of(page - 1, size, sort);
            productPage = dao.findAll(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("categories", categoryDAO.findAll()); // Lấy danh sách danh mục
        model.addAttribute("selectedCategory", categoryId);

        return "views/gdienUsers/home";
    }
}
