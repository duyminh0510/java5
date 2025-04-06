package assignment_java5.java5.entitys;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import assignment_java5.java5.dto.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Payments")
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_date", nullable = false)
    private Date paymentDate;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 10, nullable = false)
    private String currency; // VND, USD, EUR...

    @Column(name = "transaction_id", unique = true, length = 255)
    private String transactionId; // Mã giao dịch từ cổng thanh toán

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway; // PayPal, VNPay, Stripe...

    @Column(name = "payer_name", length = 255)
    private String payerName;

    @Column(name = "payer_email", length = 255)
    private String payerEmail;

    @Column(name = "payer_phone", length = 20)
    private String payerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
