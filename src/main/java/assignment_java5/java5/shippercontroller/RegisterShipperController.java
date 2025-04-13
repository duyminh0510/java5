package assignment_java5.java5.shippercontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import assignment_java5.java5.config.CloudinaryConfig;
import assignment_java5.java5.dao.ShipperDAO;
import assignment_java5.java5.entitys.Shipper;
import assignment_java5.java5.entitys.Shipper.Status;
import assignment_java5.java5.service.CloudinaryService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

@Controller
public class RegisterShipperController {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    ShipperDAO dao;

    @RequestMapping("/registershipper")
    public String requestMethodName(Model model) {
        model.addAttribute("newshipper", new Shipper());
        return "views/gdienShippers/registershipper";

    }

    @RequestMapping("/shipper/register")
    public String requestMethodName(Model model,
            @RequestParam("fullName") String fullName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("area") String area,
            @RequestParam("cccdImage") MultipartFile cccdImage,
            @RequestParam("licenseImage") MultipartFile licenseImage,
            @RequestParam("cavatxeImage") MultipartFile cavatxeImage) {
        try {
            String cccdUrl = cloudinaryService.uploadImage(cccdImage);
            String licenseUrl = cloudinaryService.uploadImage(licenseImage);
            String cavatxeUrl = cloudinaryService.uploadImage(cavatxeImage);

            Shipper shipper = Shipper.builder()
                    .fullName(fullName)
                    .phoneNumber(phoneNumber)
                    .area(area)
                    .email(email)
                    .password(password)
                    .cccdImagePath(cccdUrl)
                    .licenseImagePath(licenseUrl)
                    .cavatxeImagePath(cavatxeUrl)
                    .status(Status.PENDING)
                    .build();

            dao.save(shipper);
            model.addAttribute("successMessage", "Đăng ký shipper thành công! Chờ admin duyệt.");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi đăng ký: " + e.getMessage());
        }
        return "views/gdienShippers/registershipper";
    }

}
