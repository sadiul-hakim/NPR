package xyz.sadiulhakim.npr.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;
import xyz.sadiulhakim.npr.visitor.Visitor;
import xyz.sadiulhakim.npr.visitor.VisitorRepo;

import java.io.IOException;
import java.util.Optional;

@Component
class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final VisitorRepo visitorRepo;

    public CustomAuthenticationSuccessHandler(VisitorRepo visitorRepo) {
        this.visitorRepo = visitorRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        Optional<Visitor> existingVisitor = visitorRepo.findByEmail(user.getAttribute(AuthenticatedUserUtil.EMAIL));
        if (existingVisitor.isEmpty()) {
            Visitor visitor = create(user);
            visitorRepo.save(visitor);
        }

        response.sendRedirect("/");
    }

    private Visitor create(OAuth2User user) {
        var visitor = new Visitor();
        visitor.setName(user.getAttribute(AuthenticatedUserUtil.NAME));
        visitor.setEmail(user.getAttribute(AuthenticatedUserUtil.EMAIL));
        visitor.setPicture(user.getAttribute(AuthenticatedUserUtil.PICTURE));
        return visitor;
    }
}
