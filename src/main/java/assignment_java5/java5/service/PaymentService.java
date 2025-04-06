package assignment_java5.java5.service;

import assignment_java5.java5.entitys.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public void save(Payment payment) {
        // Logic lưu thông tin thanh toán vào cơ sở dữ liệu
        System.out.println("Lưu thông tin thanh toán: " + payment);
    }
}
