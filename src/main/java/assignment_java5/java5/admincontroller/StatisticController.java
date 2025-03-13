package assignment_java5.java5.admincontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import assignment_java5.java5.dao.ProductDAO;
import assignment_java5.java5.dao.UserDAO;

@Controller
public class StatisticController {
    @Autowired
    private ProductDAO productdao;

    @Autowired
    private UserDAO userdao;

    @RequestMapping("/statistics")
    public String requestMethodName(Model model) {
        model.addAttribute("categories", productdao.getRevenueByCategory());
        model.addAttribute("vipCustomers", userdao.getTop10VipCustomers());
        return "views/gdienAdmins/statistics";
    }
}
