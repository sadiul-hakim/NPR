package xyz.sadiulhakim.npr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
class PageController {

    @GetMapping("/")
    String home(Model model, Authentication authentication) throws JsonProcessingException {

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