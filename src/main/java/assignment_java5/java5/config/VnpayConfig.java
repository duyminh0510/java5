package assignment_java5.java5.config;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class VnpayConfig {
    public static String vnp_TmnCode = "5U2EWNSB";
    public static String vnp_HashSecret = "9X1RN6UJ4V02Y53A7NEFU7A1G344V3A0";
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:8080/api/payment/vnpay-return"; // điều hướng sau khi thanh
                                                                                           // toán
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static String getRandomNumber(int len) {
        String chars = "0123456789";
        StringBuilder result = new StringBuilder();
        Random rnd = new Random();
        while (result.length() < len) {
            int index = (int) (rnd.nextFloat() * chars.length());
            result.append(chars.charAt(index));
        }
        return result.toString();
    }
}
