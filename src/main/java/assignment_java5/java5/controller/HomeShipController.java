package assignment_java5.java5.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import assignment_java5.java5.dao.OrderDAO;
import assignment_java5.java5.dto.OrderStatus;
import assignment_java5.java5.entitys.Order;
import assignment_java5.java5.service.OrderService;

@Controller
public class HomeShipController {
    @Autowired
    private OrderDAO orderdao;

    @Autowired
    private OrderService orderService;

    @RequestMapping("/orderShip")
    public String orderManagement(Model model,
            @RequestParam(value = "status", required = false) String status) {
        List<Order> items;

        if (status != null && !status.isEmpty()) {
            items = orderdao.findByStatus(status);
        } else {
            items = orderdao.findByStatusIn(List.of("COMPLETED", "SHIPPED"));
        }
        model.addAttribute("items", items);
        model.addAttribute("selectedStatus", status);
        return "views/gdienShippers/homeShip";
    }

     @RequestMapping("/shipper/orders/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderdao.findById(id).orElse(null);

        if (order == null) {
            return "redirect:/orderShip";
        }
        model.addAttribute("order", order);

        return "views/gdienShippers/order-detail";
    }

    @RequestMapping("/shipper/orders/update-status")
    public String requestMethodName(@RequestParam("orderId") Long orderId,
            @RequestParam("status") OrderStatus status,
            RedirectAttributes redirectAttributes) {
        orderService.updateStatus(orderId, status);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        return "redirect:/orderShip";
    }
}
