package assignment_java5.java5.shippercontroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeShipperController {
    @RequestMapping("/shipper")
    public String requestMethodName(Model model) {
        return "views/gdienShippers/homeshipper";
    }

}
