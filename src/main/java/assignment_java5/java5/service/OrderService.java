package assignment_java5.java5.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import assignment_java5.java5.dao.OrderDAO;
import assignment_java5.java5.dao.OrderDetailDAO;
import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.dto.OrderStatus;
import assignment_java5.java5.entitys.CartItem;
import assignment_java5.java5.entitys.Order;
import assignment_java5.java5.entitys.OrderDetail;
import assignment_java5.java5.entitys.User;

@Service
public class OrderService {
    @Autowired
    private OrderDAO orderdao;

    @Autowired
    private OrderDetailDAO orderdetaildao;

    // ✅ Thêm phương thức này để `PaymentController` có thể sử dụng
    public Order createOrderForUser(User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        return orderdao.save(order);
    }

    public Order createOrder(User user, List<CartItem> cartItems) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PROCESSING");
        order = orderdao.save(order);

        double totalAmount = 0.0;

        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getProduct().getPrice());
            orderdetaildao.save(orderDetail);

            totalAmount += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        orderdao.save(order);
        return order;
    }

    public List<Order> getOrdersByUser(User user) {
        return orderdao.findByUser(user);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderdao.findById(orderId);
    }

    // ✅ Thêm phương thức này để cập nhật trạng thái đơn hàng từ `PaymentController`
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderdao.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        order.setStatus(status);
        orderdao.save(order);
    }

    // ✅ Thêm phương thức `findById(Long orderId)`
    public Order findById(Long orderId) {
        return orderdao.findById(orderId).orElse(null);
    }

    // order admin quản lý//////////////////////////////////////////////////
    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = orderdao.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        order.setStatus(status.name());
        orderdao.save(order);
    }

    public List<Order> getSortedOrders() {
        List<Order> orders = orderdao.findAll();
        orders.sort(Comparator.comparingInt(o -> {
            switch (o.getStatus()) {
                case "PROCESSING":
                    return 1;
                case "SHIPPED":
                    return 2;
                case "COMPLETED":
                    return 3;
                case "CANCELED":
                    return 4;
                default:
                    return 5;
            }
        }));
        return orders;
    }

    // Order người dùng/////////////////////////////////////////////////
    public List<Order> getOrdersByUserByCreatedAtDesc(User user) {
        List<Order> orders = orderdao.findByUserOrderByCreatedAtDesc(user);
        return sortOrdersByStatus(orders);
    }

    public List<Order> sortOrdersByStatus(List<Order> orders) {
        return orders.stream()
                .sorted(Comparator.comparingInt(o -> {
                    switch (o.getStatus()) {
                        case "PROCESSING":
                            return 1;
                        case "SHIPPED":
                            return 2;
                        case "COMPLETED":
                            return 3;
                        case "CANCELED":
                            return 4;
                        default:
                            return 5;
                    }
                }))
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersByStatus(User user, String status) {
        List<Order> orders = orderdao.findByUserAndStatus(user, status);
        return sortOrdersByStatus(orders);
    }
}
