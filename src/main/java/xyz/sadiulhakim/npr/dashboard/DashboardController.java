package xyz.sadiulhakim.npr.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.sadiulhakim.npr.util.AuthenticatedUserUtil;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("/page")
    public String userPage(Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture());
        return "dashboard";
    }
}
