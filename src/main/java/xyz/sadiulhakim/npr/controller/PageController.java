package xyz.sadiulhakim.npr.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

@Controller
class PageController {

    private final AppProperties appProperties;

    PageController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/")
    String home(Model model, Authentication authentication) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        String username = authentication == null ? "guest" : authentication.getName();
        model.addAttribute("username", username);
        return "index";
    }

    @GetMapping("/admin_login")
    String loginPage() {
        return "auth/admin_login";
    }
}