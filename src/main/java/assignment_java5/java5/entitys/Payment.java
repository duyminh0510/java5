package assignment_java5.java5.entitys;

import java.io.Serializable;
import java.util.Date;

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
    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "amount", precision = 10)
    private Double amount;

    @Column(name = "payment_method", columnDefinition = "NVARCHAR(255)")
    private String paymentMethod;

    @Column(name = "status", columnDefinition = "NVARCHAR(255)")
    private String status;

    @ManyToOne
    @JoinColumn(name = "id")
    private Order order;

}
