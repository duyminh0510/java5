package assignment_java5.java5.entitys;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Order_Details")
public class OrderDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderdetailid")
    private Long orderDetailId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "price", precision = 10)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
