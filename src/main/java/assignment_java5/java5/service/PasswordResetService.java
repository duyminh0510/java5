package assignment_java5.java5.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import assignment_java5.java5.dao.UserDAO;
import assignment_java5.java5.entitys.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserDAO userRepository;
    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmailPass(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Email không tồn tại trong hệ thống!");
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        sendEmail(user.getEmail(), "Đặt lại mật khẩu", "Nhấn vào link để đặt lại mật khẩu: " + resetLink);
    }

    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Token không hợp lệ!");
        }

        User user = userOptional.get();
        user.setPassword(newPassword); // Cần mã hóa mật khẩu
        user.setResetToken(null); // Xóa token sau khi đặt lại mật khẩu
        userRepository.save(user);
    }
}
