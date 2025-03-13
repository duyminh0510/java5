package assignment_java5.java5.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import assignment_java5.java5.dao.ProductDAO;
import assignment_java5.java5.entitys.Product;

@Controller
public class DetailedHomePageController {
    @Autowired
    private ProductDAO dao;

    @RequestMapping("/detailedpage/{id}")
    public String index(Model model, @PathVariable("id") String id) {
        Optional<Product> optionalProduct = dao.findById(Integer.parseInt(id));

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            model.addAttribute("item", product);
            model.addAttribute("sizes", product.getSizeproducts());
            model.addAttribute("itemss", dao.findAll());
            return "views/gdienUsers/detailedhomepage";
        }
        model.addAttribute("items", dao.findAll());
        return "views/gdienUsers/detailedhomepage";
    }

}
