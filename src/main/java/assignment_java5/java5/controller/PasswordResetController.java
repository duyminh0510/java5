package assignment_java5.java5.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import assignment_java5.java5.service.PasswordResetService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "views/gdienUsers/forgotpassword";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            passwordResetService.sendResetPasswordEmail(email);
            model.addAttribute("message", "Một email đã được gửi để đặt lại mật khẩu.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "views/gdienUsers/forgotpassword";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "views/gdienUsers/forgotpassword";

    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password,
            Model model) {
        try {
            passwordResetService.resetPassword(token, password);
            model.addAttribute("message", "Mật khẩu của bạn đã được đặt lại thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "views/gdienUsers/forgotpassword";

    }
}
