package assignment_java5.java5.admincontroller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import assignment_java5.java5.dao.ShipperDAO;
import assignment_java5.java5.entitys.Shipper;
import assignment_java5.java5.entitys.Shipper.Status;
import assignment_java5.java5.service.MailerService;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shipper")
public class AdminShipperController {
    @Autowired
    ShipperDAO shipperDAO;

    @Autowired
    MailerService mailerService;

    // hiển thị
    @RequestMapping("/pending")
    public String requestMethodName(Model model) {
        List<Shipper> pendingShippers = shipperDAO.findByStatus(Status.PENDING);
        model.addAttribute("pendingShippers", pendingShippers);
        return "views/gdienAdmins/shipper-approval";
    }

    @RequestMapping("/approve/{id}")
    public String approve(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Shipper shipper = shipperDAO.findById(id).orElse(null);
        if (shipper != null) {
            shipper.setStatus(Status.APPROVED);
            shipperDAO.save(shipper);

            String subject = "Tài khoản shipper của bạn đã được duyệt";
            String html = "<h3>Chào " + shipper.getFullName() + ",</h3>"
                    + "<p>Tài khoản shipper của bạn đã <strong style='color:green;'>được duyệt</strong> thành công.</p>"
                    + "<p>Bạn có thể truy cập hệ thống để bắt đầu nhận đơn hàng.</p>"
                    + "<p style='margin-top:20px;'>Trân trọng,<br>Đội ngũ hỗ trợ</p>";

            mailerService.sendHtmlEmail(shipper.getEmail(), subject, html);

            redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt shipper và gửi email HTML.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Shipper không tồn tại.");
        }

        return "redirect:/admin/shipper/pending";
    }

    // từ chối
    @RequestMapping("/reject/{id}")
    public String reject(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Shipper shipper = shipperDAO.findById(id).orElse(null);
        if (shipper != null) {
            String subject = "Đơn đăng ký shipper bị từ chối";
            String html = "<h3>Chào " + shipper.getFullName() + ",</h3>"
                    + "<p>Rất tiếc, đơn đăng ký shipper của bạn đã <strong style='color:red;'>bị từ chối</strong>.</p>"
                    + "<p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ đội ngũ hỗ trợ của chúng tôi.</p>"
                    + "<p style='margin-top:20px;'>Trân trọng,<br>Đội ngũ hỗ trợ</p>";

            mailerService.sendHtmlEmail(shipper.getEmail(), subject, html);

            shipperDAO.delete(shipper);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối shipper và gửi email HTML.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Shipper không tồn tại.");
        }

        return "redirect:/admin/shipper/pending";
    }

}
