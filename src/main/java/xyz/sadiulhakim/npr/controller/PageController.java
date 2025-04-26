package xyz.sadiulhakim.npr.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

@Controller
class PageController {

    @GetMapping("/")
    String home(Model model, Authentication authentication) {

        model.addAttribute("name", AuthenticatedUserUtil.get(AuthenticatedUserUtil.NAME));
        model.addAttribute("picture", AuthenticatedUserUtil.get(AuthenticatedUserUtil.PICTURE));
        String username = authentication == null ? "guest" : authentication.getName();
        model.addAttribute("username", username);
        return "index";
    }

    @GetMapping("/admin_login")
    String loginPage() {
        return "auth/admin_login";
    }
}