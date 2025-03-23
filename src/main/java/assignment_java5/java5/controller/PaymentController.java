package assignment_java5.java5.controller;

import assignment_java5.java5.entitys.CartItem;
import assignment_java5.java5.entitys.Order;
import assignment_java5.java5.entitys.Payment;
import assignment_java5.java5.entitys.Product;
import assignment_java5.java5.entitys.User;
import assignment_java5.java5.service.CartService;
import assignment_java5.java5.service.OrderService;
import assignment_java5.java5.service.PaymentService;
import assignment_java5.java5.service.ProductService;
import assignment_java5.java5.service.VNPayService;
import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.dto.OrderStatus;
import assignment_java5.java5.dto.PaymentStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
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
    private PaymentService paymentService;

    @Autowired
    private VNPayService vnPayService;

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

        Product product = productService.findById(productId);
        if (product == null) {
            return "redirect:/";
        }

        CartItem tempCartItem = new CartItem();
        tempCartItem.setProduct(product);
        tempCartItem.setQuantity(quantity);
        tempCartItem.setSize(size);
        tempCartItem.setTotalamount(Math.round(product.getPrice() * quantity));

        session.setAttribute("selectedCartItems", List.of(tempCartItem));
        model.addAttribute("cartItems", List.of(tempCartItem));
        return "views/gdienUsers/payments";
    }

    @SuppressWarnings("unchecked")
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

        List<Long> selectedItemIds = (List<Long>) session.getAttribute("selectedItemIds");
        List<CartItem> cartItems;

        if (selectedItemIds != null && !selectedItemIds.isEmpty()) {
            cartItems = cartService.getCartItemsByIds(selectedItemIds);
        } else {
            cartItems = (List<CartItem>) session.getAttribute("selectedCartItems");
        }

        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        Order order = orderService.createOrder(user, cartItems);

        if (selectedItemIds != null) {
            cartService.removeCartItemsByIds(selectedItemIds);
        }

        session.removeAttribute("selectedItemIds");
        session.removeAttribute("selectedCartItems");

        redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đang chờ xử lý!");
        return "redirect:/order/history";
    }

    @GetMapping("/vnpay")
    public String createVNPayPayment(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/payment/vnpay");
            return "redirect:/dangnhap";
        }

        Order order = orderService.createOrderForUser(user);
        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo đơn hàng.");
            return "redirect:/cart";
        }

        String paymentUrl = vnPayService.createPaymentUrl(order);
        return "redirect:" + paymentUrl;
    }

    @GetMapping("/vnpay-return")
    public String handleVNPayReturn(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        int paymentStatus = vnPayService.processPaymentResponse(request);
        Long orderId = Long.valueOf(request.getParameter("vnp_TxnRef"));

        if (paymentStatus == 1) {
            orderService.updateOrderStatus(orderId, PaymentStatus.COMPLETED.name());
            Payment payment = new Payment();
            payment.setOrder(orderService.findById(orderId));
            payment.setAmount(BigDecimal.valueOf(Double.parseDouble(request.getParameter("vnp_Amount")) / 100));
            payment.setPaymentGateway("VNPay"); // 🔹 Thay thế setPaymentMethod()
            payment.setPaymentDate(new Date());
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentService.save(payment);
            redirectAttributes.addFlashAttribute("message", "Thanh toán thành công!");
        } else {
            orderService.updateOrderStatus(orderId, PaymentStatus.FAILED.name());
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán thất bại!");
        }
        return "redirect:/order/history";
    }
}
