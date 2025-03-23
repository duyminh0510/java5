package assignment_java5.java5.service;

import assignment_java5.java5.entitys.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class VNPayService {

    public String createPaymentUrl(Order order) {
        // Giả lập tạo URL thanh toán
        return "https://sandbox.vnpayment.vn/payment?orderId=" + order.getId();
    }

    public int processPaymentResponse(HttpServletRequest request) {
        // Giả lập xử lý phản hồi từ VNPay
        String status = request.getParameter("vnp_ResponseCode");
        return "00".equals(status) ? 1 : 0;
    }
}
