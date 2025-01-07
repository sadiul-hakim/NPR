package xyz.sadiulhakim.npr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.sadiulhakim.npr.util.AuthenticatedUserUtil;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.get(AuthenticatedUserUtil.NAME));
        model.addAttribute("picture", AuthenticatedUserUtil.get(AuthenticatedUserUtil.PICTURE));
        return "index";
    }

    @GetMapping("/admin_login")
    public String loginPage() {
        return "auth/admin_login";
    }
}