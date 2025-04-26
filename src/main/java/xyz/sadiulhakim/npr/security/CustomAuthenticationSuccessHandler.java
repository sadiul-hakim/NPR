package xyz.sadiulhakim.npr.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.visitor.model.VisitorService;

import java.io.IOException;

@Component
class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final VisitorService visitorService;
    public CustomAuthenticationSuccessHandler(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        visitorService.createVisitor(user);

        response.sendRedirect("/");
    }
}
