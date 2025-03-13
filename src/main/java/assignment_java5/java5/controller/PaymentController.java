package assignment_java5.java5.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.entitys.CartItem;
import assignment_java5.java5.entitys.Order;
import assignment_java5.java5.entitys.Product;
import assignment_java5.java5.entitys.User;
import assignment_java5.java5.service.CartService;
import assignment_java5.java5.service.OrderService;
import assignment_java5.java5.service.ProductService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserDAO userdao;

    @RequestMapping("/index")
    public String showPayment(@RequestParam(name = "selectedItems", required = false) List<Long> selectedItemIds,
            HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/payment/index");
            return "redirect:/dangnhap";
        }

        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            return "redirect:/cart/index";
        }

        session.setAttribute("selectedItemIds", selectedItemIds);

        List<CartItem> selectedCartItems = cartService.getCartItemsByIds(selectedItemIds);
        model.addAttribute("cartItems", selectedCartItems);
        return "views/gdienUsers/payments";
    }

    @RequestMapping("/placeanorder")
    public String showPayment(
            @RequestParam(name = "productId") Integer productId,
            @RequestParam(name = "quantity") Long quantity,
            @RequestParam(name = "size") String size,
            HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            String redirectUrl = "/payment/placeanorder?productId=" + productId + "&quantity=" + quantity + "&size="
                    + size;
            session.setAttribute("redirectAfterLogin", redirectUrl);
            return "redirect:/dangnhap";
        }

        if (user.getRole() == true) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn chỉ có quyền quản lý, không thể đặt hàng!");
            return "redirect:/detailedpage/" + productId;
        }

        // Lấy sản phẩm theo ID từ ProductService
        Product product = productService.findById(productId);
        if (product == null) {
            return "redirect:/"; // Nếu sản phẩm không tồn tại, quay về trang chủ
        }

        // Tạo đối tượng tạm thời để hiển thị trên trang thanh toán
        CartItem tempCartItem = new CartItem();
        tempCartItem.setProduct(product);
        tempCartItem.setQuantity(quantity);
        tempCartItem.setSize(size);
        tempCartItem.setTotalamount(Math.round(product.getPrice() * quantity));

        // Lưu vào session để có thể lấy lại khi đặt hàng
        session.setAttribute("selectedCartItems", List.of(tempCartItem));

        model.addAttribute("cartItems", List.of(tempCartItem)); // Hiển thị sản phẩm vừa mua

        return "views/gdienUsers/payments";
    }

    @RequestMapping("/place-order")
    public String placeOrder(@RequestParam("username") String username,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/payment/place-order");
            return "redirect:/dangnhap";
        }

        user.setUsername(username);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAddress(address);
        userdao.save(user);

        // Lấy danh sách sản phẩm từ giỏ hàng (nếu có)
        List<Long> selectedItemIds = (List<Long>) session.getAttribute("selectedItemIds");
        List<CartItem> cartItems;

        if (selectedItemIds != null && !selectedItemIds.isEmpty()) {
            cartItems = cartService.getCartItemsByIds(selectedItemIds);
        } else {
            // Nếu không có selectedItemIds, thử lấy sản phẩm mua trực tiếp
            cartItems = (List<CartItem>) session.getAttribute("selectedCartItems");
        }

        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        Order order = orderService.createOrder(user, cartItems);

        // Nếu là sản phẩm từ giỏ hàng, xóa nó đi
        if (selectedItemIds != null) {
            cartService.removeCartItemsByIds(selectedItemIds);
        }

        // Xóa session sau khi đặt hàng thành công
        session.removeAttribute("selectedItemIds");
        session.removeAttribute("selectedCartItems");

        redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đang chờ xử lý!");
        return "redirect:/order/history";
    }

    // @RequestMapping("/placeanorder")
    // public String showPayment(
    // @RequestParam(name = "productId") Integer productId,
    // @RequestParam(name = "quantity") Long quantity,
    // @RequestParam(name = "size") String size,
    // HttpSession session, Model model) {

    // User user = (User) session.getAttribute("loggedInUser");
    // if (user == null) {
    // return "redirect:/dangnhap";
    // }

    // // Lấy sản phẩm theo ID từ ProductService
    // Product product = productService.findById(productId);
    // if (product == null) {
    // return "redirect:/"; // Nếu sản phẩm không tồn tại, quay về trang chủ
    // }

    // // Tạo đối tượng tạm thời để hiển thị trên trang thanh toán
    // CartItem tempCartItem = new CartItem();
    // tempCartItem.setProduct(product);
    // tempCartItem.setQuantity(quantity);
    // tempCartItem.setSize(size);
    // tempCartItem.setTotalamount(Math.round(product.getPrice() * quantity));

    // model.addAttribute("cartItems", List.of(tempCartItem)); // Hiển thị sản phẩm
    // vừa mua

    // return "views/gdienUsers/payments";
    // }

    // @RequestMapping("/place-order")
    // public String requestMethodName(HttpSession session,
    // RedirectAttributes redirectAttributes) {
    // User user = (User) session.getAttribute("loggedInUser");
    // if (user == null) {
    // return "redirect:/dangnhap";
    // }
    // List<Long> selectedItemIds = (List<Long>)
    // session.getAttribute("selectedItemIds");
    // if (selectedItemIds == null || selectedItemIds.isEmpty()) {
    // return "redirect:/cart";
    // }

    // List<CartItem> cartItems = cartService.getCartItemsByIds(selectedItemIds);
    // if (cartItems.isEmpty()) {
    // return "redirect:/cart";
    // }

    // Order order = orderService.createOrder(user, cartItems);
    // cartService.removeCartItemsByIds(selectedItemIds);

    // session.removeAttribute("selectedItemIds");
    // // return "redirect:/order/details/" + order.getId();
    // redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đang chờ xử
    // lý!");
    // return "redirect:/order/history";
    // }
}
