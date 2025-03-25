package assignment_java5.java5.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gdienShippers")
public class HomeShipController {

    @GetMapping("/homeShip")
    public String homeShip() {
        return "/views/gdienShippers/homeShip"; // Chỉ cần từ "gdienShippers/" trở đi
    }
}
