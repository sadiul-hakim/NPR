package xyz.sadiulhakim.npr.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.sadiulhakim.npr.user.UserService;
import xyz.sadiulhakim.npr.util.AuthenticatedUserUtil;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page")
    public String userPage(Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(userService.folder));
        return "dashboard";
    }
}
