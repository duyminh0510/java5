package assignment_java5.java5.controller;

import assignment_java5.java5.dao.CartDAO;
import assignment_java5.java5.dao.CartItemDAO;
import assignment_java5.java5.entitys.Cart;
import assignment_java5.java5.entitys.CartItem;
import assignment_java5.java5.entitys.User;
import assignment_java5.java5.service.CartService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemDAO cartItemDAO;

    @Autowired
    private CartDAO cartDAO;

    @GetMapping("/index")
    public String getCart(Model model, Principal principal, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/cart/index");
            return "redirect:/dangnhap";
        }
        Optional<Cart> cartOpt = cartDAO.findByUser(user);

        double totalAmount = 0;
        List<CartItem> cartItems = Collections.emptyList();

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cartItems = cartItemDAO.findByCart(cart);
            // Tính tổng tiền mặc định (có thể bỏ qua nếu chỉ muốn tính khi chọn sản phẩm)
            for (CartItem item : cartItems) {
                totalAmount += item.getPrice() * item.getQuantity();
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "views/gdienUsers/carts";
    }

    @PostMapping("/update-total")
    @ResponseBody
    public String updateTotal(@RequestParam List<Long> selectedItems) {
        double total = 0;
        for (Long itemId : selectedItems) {
            CartItem item = cartItemDAO.findById(itemId).orElse(null);
            if (item != null) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        return String.valueOf((long) total); // Trả về số nguyên để tránh lỗi format
    }

    // số lượng giỏ hàng
    @PostMapping("/update-quantity")
    @ResponseBody
    public String updateQuantity(@RequestParam("itemId") Long itemId, @RequestParam("quantity") Long quantity) {
        Optional<CartItem> cartItemOpt = cartItemDAO.findById(itemId);

        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            if (quantity <= 0) {
                cartItemDAO.delete(cartItem); // Xóa nếu số lượng về 0
                return "deleted";
            } else {
                cartItem.setQuantity(quantity);
                cartItem.setTotalamount((long) (cartItem.getPrice() * quantity));

                cartItemDAO.save(cartItem);
                return String.valueOf(cartItem.getTotalamount()); // Trả về tổng tiền mới
            }
        }
        return "error";
    }

    @PostMapping("/add")
    @ResponseBody
    public String addToCart(@RequestParam("productId") Integer productId,
            @RequestParam("quantity") Long quantity,
            @RequestParam("size") String size,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        // Lấy user từ session
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "Vui lòng bạn đăng nhập để có thể thêm sản phẩm vào giỏ hàng!";
        }

        if (user.getRole().equals("ADMIN")) { // Nếu là admin
            return "Bạn chỉ có quyền quản lý, không có quyền thêm vào giỏ hàng!";
        }

        cartService.addToCart(user, productId, size, quantity);
        return "Sản phẩm đã được thêm vào giỏ hàng!";
    }
}