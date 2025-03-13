package assignment_java5.java5.entitys;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Products")
public class Product implements Serializable {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(name = "product_name", columnDefinition = "NVARCHAR(1000)")
    private String productName;

    @Column(name = "description", columnDefinition = "NVARCHAR(1000)")
    private String description;

    @Column(name = "price", precision = 10)
    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "image_url", columnDefinition = "NVARCHAR(1000)")
    private String imageUrl;

    // @ElementCollection
    // @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name =
    // "product_id"))
    // @Column(name = "image_url")
    // private List<String> imageUrls;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sizeproduct> sizeproducts;

    public List<Sizeproduct> getSizeproducts() {
        return sizeproducts;
    }

    public void setSizeproducts(List<Sizeproduct> sizeproducts) {
        this.sizeproducts = sizeproducts;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCategoryId() {
        return category != null ? category.getCategoryId() : null;
    }

    @Override
    public String toString() {
        return "Product{id=" + productId + ", productName='" + productName + "'}";
        // Không in sizeproductList ở đây
    }
}
