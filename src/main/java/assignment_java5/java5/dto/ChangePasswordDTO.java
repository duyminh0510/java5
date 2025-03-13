package assignment_java5.java5.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {

    @NotBlank(message = "Mật khẩu cũ không được để trống!")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống!")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 số và 1 ký tự đặc biệt!")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống!")
    private String confirmPassword;
}
