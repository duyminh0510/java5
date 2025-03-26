package assignment_java5.java5.controller;

import assignment_java5.java5.dao.CartDAO;
import assignment_java5.java5.dao.CartItemDAO;
import assignment_java5.java5.dao.ProductDAO;
import assignment_java5.java5.entitys.Cart;
import assignment_java5.java5.entitys.CartItem;
import assignment_java5.java5.entitys.User;
import assignment_java5.java5.service.CartService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartDAO cartDAO;

    @Autowired
    private CartItemDAO cartItemDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CartService cartService;

    // ✅ API lấy giỏ hàng
    @GetMapping("/index")
    public Map<String, Object> getCart(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return Collections.singletonMap("error", "Vui lòng đăng nhập!");
        }

        Optional<Cart> cartOpt = cartDAO.findByUser(user);
        double totalAmount = 0;
        List<CartItem> cartItems = new ArrayList<>();

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cartItems = cartItemDAO.findByCart(cart);
            for (CartItem item : cartItems) {
                totalAmount += item.getPrice() * item.getQuantity();
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("cartItems", cartItems);
        response.put("totalAmount", totalAmount);
        return response;
    }

    // ✅ API cập nhật tổng tiền
    @PostMapping("/update-total")
    public Map<String, Object> updateTotal(@RequestParam List<Long> selectedItems) {
        double total = 0;
        for (Long itemId : selectedItems) {
            CartItem item = cartItemDAO.findById(itemId).orElse(null);
            if (item != null) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        return Collections.singletonMap("totalAmount", total);
    }

    @PostMapping("/update-quantity")
    public Map<String, Object> updateQuantity(@RequestParam("itemId") Long itemId,
            @RequestParam("quantity") Long quantity) {
        Optional<CartItem> cartItemOpt = cartItemDAO.findById(itemId);

        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            if (quantity <= 0) {
                cartItemDAO.delete(cartItem); // Xóa nếu số lượng về 0
                return Collections.singletonMap("status", "deleted");
            } else {
                cartItem.setQuantity(quantity);
                cartItem.setTotalamount((long) (cartItem.getPrice() * quantity));

                cartItemDAO.save(cartItem);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "updated");
                response.put("totalAmount", cartItem.getTotalamount());
                return response;
            }
        }
        return Collections.singletonMap("status", "error");
    }

    // ✅ API thêm vào giỏ hàng
    @PostMapping("/add")
    public Map<String, Object> addToCart(@RequestParam("productId") Integer productId,
            @RequestParam("quantity") Long quantity,
            @RequestParam("size") String size,
            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return Collections.singletonMap("error", "Vui lòng đăng nhập!");
        }

        if (user.getRole()) { // Nếu là admin
            return Collections.singletonMap("error", "Bạn chỉ có quyền quản lý, không được thêm vào giỏ hàng!");
        }

        cartService.addToCart(user, productId, size, quantity);
        return Collections.singletonMap("message", "Sản phẩm đã được thêm vào giỏ hàng!");
    }
}