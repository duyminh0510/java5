package assignment_java5.java5.entitys;

import assignment_java5.java5.dto.Role;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shippers")
public class Shipper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String area; // Khu vực hoạt động

    private String cccdImagePath; // Ảnh CCCD
    private String licenseImagePath; // Ảnh bằng lái
    private String cavatxeImagePath;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @Builder.Default
    @Enumerated(EnumType.STRING)

    @Column(nullable = false)
    private Role role = Role.SHIPPER;
}
