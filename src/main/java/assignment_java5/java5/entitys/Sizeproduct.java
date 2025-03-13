package assignment_java5.java5.entitys;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sizeproducts")
public class Sizeproduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "size", columnDefinition = "NVARCHAR(20)")
    private String size;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Constructor không có ID (dành cho khi tạo mới)
    public Sizeproduct(String size, Product product) {
        this.size = size;
        this.product = product;
    }

    @Override
    public String toString() {
        return "Sizeproduct{id=" + id + ", sizeName='" + size + "'}";
        // Không in product ở đây
    }
}