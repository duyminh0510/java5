package assignment_java5.java5.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HosoUpdateDTO {

    @NotBlank(message = "Họ và tên không được để trống!")
    @Size(min = 3, max = 50, message = "Họ và tên phải có độ dài từ 3 đến 50 ký tự!")
    private String username;

    @NotBlank(message = "Số điện thoại không được để trống!")
    @Pattern(regexp = "^(0[35789][0-9]{8})$", message = "Số điện thoại không hợp lệ! (VD: 0987654321)")
    private String phone;

    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ!")
    private String email;

    @NotBlank(message = "Địa chỉ không được để trống!")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự!")
    private String address;
}
