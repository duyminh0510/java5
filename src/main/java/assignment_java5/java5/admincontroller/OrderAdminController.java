package assignment_java5.java5.admincontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import assignment_java5.java5.dao.OrderDAO;
import assignment_java5.java5.dto.OrderStatus;
import assignment_java5.java5.entitys.Order;
import assignment_java5.java5.service.OrderService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderAdminController {
    @Autowired
    private OrderDAO orderdao;

    @Autowired
    private OrderService orderService;

    @RequestMapping("/ordermanagement")
    public String orderManagement(Model model,
            @RequestParam(value = "status", required = false) String status) {
        List<Order> items;

        if (status != null && !status.isEmpty()) {
            items = orderdao.findByStatus(status);
        } else {
            items = orderService.getSortedOrders();
        }
        model.addAttribute("items", items);
        model.addAttribute("selectedStatus", status);
        return "views/gdienAdmins/orderadmin";
    }

    @RequestMapping("/admin/orders/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderdao.findById(id).orElse(null);

        if (order == null) {
            return "redirect:/ordermanagement";
        }
        model.addAttribute("order", order);

        return "views/gdienAdmins/order-detail";
    }

    @RequestMapping("/admin/orders/update-status")
    public String requestMethodName(@RequestParam("orderId") Long orderId,
            @RequestParam("status") OrderStatus status,
            RedirectAttributes redirectAttributes) {
        orderService.updateStatus(orderId, status);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        return "redirect:/ordermanagement";
    }
}