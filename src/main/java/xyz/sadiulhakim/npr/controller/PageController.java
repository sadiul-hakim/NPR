package xyz.sadiulhakim.npr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.sadiulhakim.npr.util.UserUtil;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("user_name", UserUtil.get(UserUtil.NAME));
        model.addAttribute("photo", UserUtil.get(UserUtil.PICTURE));
        return "index";
    }

    @GetMapping("/login_page")
    public String loginPage() {
        return "auth/login_page";
    }

    @GetMapping("/register_page")
    public String register() {
        return "auth/register_page";
    }
}