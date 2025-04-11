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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shipper")
public class AdminShipperController {
    @Autowired
    ShipperDAO shipperDAO;

    // hiển thị
    @RequestMapping("/pending")
    public String requestMethodName(Model model) {
        List<Shipper> pendingShippers = shipperDAO.findByStatus(Status.PENDING);
        model.addAttribute("pendingShippers", pendingShippers);
        return "views/gdienAdmins/shipper-approval";
    }

    // duyệt
    @RequestMapping("/approve/{id}")
    public String requestMethodName(Model model,
            @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Shipper shipper = shipperDAO.findById(id).orElse(null);
        if (shipper != null) {
            shipper.setStatus(Status.APPROVED);
            shipperDAO.save(shipper);

            String token = UUID.randomUUID().toString();
            Instant expiry = Instant.now().plus(10, ChronoUnit.MINUTES);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Shipper không tồn tại.");
            return "redirect:/admin/shipper/pending";
        }

        return "redirect:/admin/shipper/pending";
    }

    // từ chối
    @RequestMapping("/reject/{id}")
    public String reject(Model model,
            @PathVariable("id") Long id) {
        Shipper shipper = shipperDAO.findById(id).orElse(null);
        if (shipper != null) {
            shipper.setStatus(Status.REJECTED);
            shipperDAO.save(shipper);
        }
        return "redirect:/admin/shipper/pending";
    }
}
