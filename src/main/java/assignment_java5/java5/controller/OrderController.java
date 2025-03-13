package assignment_java5.java5.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import assignment_java5.java5.entitys.Order;
import assignment_java5.java5.entitys.User;
import assignment_java5.java5.service.OrderService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/history")
    public String orderHistory(HttpSession session, Model model,
            @RequestParam(value = "status", required = false) String status) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/order/history");
            return "redirect:/dangnhap";
        }

        List<Order> orders;

        if (status == null || status.isEmpty()) {
            orders = orderService.getOrdersByUserByCreatedAtDesc(user);
        } else {
            orders = orderService.getOrdersByStatus(user, status);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        return "views/gdienUsers/orders";
    }

}