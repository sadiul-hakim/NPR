package xyz.sadiulhakim.npr.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

@Controller
@RequestMapping("/dashboard")
class DashboardController {

    private final AppProperties appProperties;

    DashboardController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/page")
    String userPage(Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture",AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        return "dashboard";
    }
}
