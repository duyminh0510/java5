package assignment_java5.java5.entitys;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User implements Serializable {

    @Id
    @Column(name = "user_id", updatable = false, nullable = false, unique = true)
    private String userId;

    @NotBlank(message = "Chưa nhập họ và tên")
    @Column(name = "username", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 50, message = "Mật khẩu phải có ít nhất 8 ký tự và tối đa 50 ký tự")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$", message = "Mật khẩu phải có ít nhất một chữ cái viết hoa, một chữ cái viết thường, một số và một ký tự đặc biệt")
    @Column(name = "password", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String password;

    @NotBlank(message = "Chưa nhập email")
    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", columnDefinition = "NVARCHAR(1000)", unique = true)
    private String email;

    @NotBlank(message = "Chưa nhập số điện thoại")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại không đúng định dạng")
    @Column(name = "phone", length = 10)
    private String phone;

    @NotBlank(message = "Chưa nhập địa chỉ")
    @Column(name = "address", columnDefinition = "NVARCHAR(1000)")
    private String address;

    @Column(name = "role", nullable = false)
    @Builder.Default
    private Boolean role = false;

    @Column(name = "is_active", nullable = false, columnDefinition = "BIT DEFAULT 1")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "reset_token")
    private String resetToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cart> cart;

    // bi-directional many-to-one association to Share
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @PrePersist
    public void prePersist() {
        if (this.userId == null || this.userId.isEmpty()) {
            this.userId = UUID.randomUUID().toString().replace("-", ""); // Tạo UUID không có dấu "-"
        }
    }
}
