package assignment_java5.java5.admincontroller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import assignment_java5.java5.dao.CategoryDAO;
import assignment_java5.java5.dao.ProductDAO;
import assignment_java5.java5.dao.SizeproductDAO;
import assignment_java5.java5.entitys.Category;
import assignment_java5.java5.entitys.Product;
import assignment_java5.java5.entitys.Sizeproduct;
import assignment_java5.java5.service.CloudinaryService;
import assignment_java5.java5.service.ProductService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ProductController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductDAO dao;

    @Autowired
    private SizeproductDAO sizedao;

    @Autowired
    private CategoryDAO categorydao;

    @Autowired
    private ProductService productService;

    @RequestMapping("/product/index")
    public String index(Model model) {
        Product item = new Product();
        model.addAttribute("item", item);
        List<Product> items = dao.findAll();
        model.addAttribute("items", items);
        model.addAttribute("categories", categorydao.findAll());
        return "views/gdienAdmins/products";
    }

    // @RequestMapping("/product/edit/{id}")
    // public String edit(Model model, @PathVariable("id") Integer id,
    // RedirectAttributes redirectAttributes) {
    // Optional<Product> optionalProduct = dao.findById(id);
    // if (optionalProduct.isPresent()) {
    // Product item = optionalProduct.get();
    // if (item.getCategory() != null) {
    // model.addAttribute("selectedCategoryId", item.getCategory().getCategoryId());
    // }
    // if (item.getSizeproducts() != null) {
    // List<String> selectedSizes = item.getSizeproducts().stream()
    // .map(Sizeproduct::getSize)
    // .collect(Collectors.toList());
    // model.addAttribute("selectedSizes", selectedSizes);

    // }
    // model.addAttribute("item", item);
    // List<Category> categories = categorydao.findAll();
    // model.addAttribute("categories", categories);
    // } else {
    // redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
    // return "redirect:/product/index";
    // }
    // List<Product> items = dao.findAll();
    // model.addAttribute("items", items);

    // return "views/gdienAdmins/products";
    // }

    @GetMapping("/rest/products/{id}")
    public ResponseEntity<Product> getMethodName(@PathVariable("id") Integer id) {
        Product product = productService.findById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping("/product/createAndUpdate")
    public String uploadProduct(RedirectAttributes redirectAttributes,
            @ModelAttribute Product product,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "sizes", required = false) List<String> sizes) {
        try {
            boolean isNewProduct = (product.getProductId() == null);

            // Nếu là cập nhật, kiểm tra sản phẩm đã tồn tại chưa
            Product existingProduct = null;
            if (!isNewProduct) {
                existingProduct = dao.findById(product.getProductId()).orElse(null);
                if (existingProduct == null) {
                    redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
                    return "redirect:/product/index";
                }
            }

            // Xử lý hình ảnh
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                product.setImageUrl(imageUrl);
            } else if (!isNewProduct) {
                product.setImageUrl(existingProduct.getImageUrl()); // Giữ nguyên ảnh cũ
            }

            // Gán danh mục
            Category category = categorydao.findById(product.getCategory().getCategoryId()).orElse(null);
            product.setCategory(category);

            if (sizes != null && !sizes.isEmpty()) {
                // Xóa các size hiện tại nếu là cập nhật
                if (!isNewProduct) {
                    sizedao.deleteByProductId(product.getProductId());
                }

                List<Sizeproduct> sizeList = sizes.stream()
                        .map(size -> new Sizeproduct(null, size, product))
                        .toList();
                product.setSizeproducts(sizeList);
            } else if (!isNewProduct) {
                product.setSizeproducts(existingProduct.getSizeproducts());
            }

            // Lưu sản phẩm vào DB
            dao.save(product);
            redirectAttributes.addFlashAttribute("success",
                    "Sản phẩm đã được " + (isNewProduct ? "thêm mới" : "cập nhật") + " thành công!");
            return "redirect:/product/index";
        } catch (Exception e) {
            log.error("Lỗi khi lưu sản phẩm: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi lưu sản phẩm!");
            return "redirect:/product/index";
        }
    }

    @RequestMapping("/product/delete/{id}")
    public String deleteById(Model model, @PathVariable("id") String id,
            RedirectAttributes redirectAttributes) {
        if (dao.existsById(Integer.parseInt(id))) {
            dao.deleteById(Integer.parseInt(id));
            redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm thành công!");
        } else {
            log.warn("Không tìm thấy sản phẩm để xóa với ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");

        }
        return "redirect:/product/index";
    }

    // seach sản phẩm
    @RequestMapping("/product/search")
    public String searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Product> productPage = productService.searchByName(keyword, pageable);

        if (page > productPage.getTotalPages() && productPage.getTotalPages() > 0) {
            page = productPage.getTotalPages();
            pageable = PageRequest.of(page - 1, size, sort);
            productPage = productService.searchByName(keyword, pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("keyword", keyword);

        return "views/gdienUsers/product-list";
    }

}
