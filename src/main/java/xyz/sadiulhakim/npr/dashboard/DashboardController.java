package xyz.sadiulhakim.npr.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/dashboard")
class DashboardController {

    private final AppProperties appProperties;
    private final DashboardService dashboardService;

    DashboardController(AppProperties appProperties, DashboardService dashboardService) {
        this.appProperties = appProperties;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/page")
    String userPage(Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        return "dashboard";
    }

    @GetMapping("/subscribe")
    SseEmitter subscribe() {
        return dashboardService.subscribe(AuthenticatedUserUtil.getName());
    }
}