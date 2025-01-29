package xyz.sadiulhakim.npr.visitor.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.util.Optional;

@Service
@NamedInterface("visitor-service")
public class VisitorService {

    private final Logger LOGGER = LoggerFactory.getLogger(VisitorService.class);

    @Value("${default.pagination.size:0}")
    private int paginationSize;
    private final VisitorRepo visitorRepo;

    VisitorService(VisitorRepo visitorRepo) {
        this.visitorRepo = visitorRepo;
    }

    public void save(Visitor visitor) {
        try {
            LOGGER.info("VisitorService.save :: Saving visitor {}", visitor.getEmail());
            visitorRepo.save(visitor);
        } catch (Exception ex) {
            LOGGER.error("VisitorService.save :: error {}", ex.getMessage());
        }
    }

    public Optional<Visitor> getByMail(String mail) {

        LOGGER.info("VisitorService.getByMail :: Getting Visitor by mail {}", mail);
        return visitorRepo.findByEmail(mail);
    }

    private Visitor create(OAuth2User user) {
        var visitor = new Visitor();
        visitor.setName(user.getAttribute(AuthenticatedUserUtil.NAME));
        visitor.setEmail(user.getAttribute(AuthenticatedUserUtil.EMAIL));
        visitor.setPicture(user.getAttribute(AuthenticatedUserUtil.PICTURE));
        return visitor;
    }

    public void createVisitor(OAuth2User user) {
        Optional<Visitor> existingVisitor = getByMail(user.getAttribute(AuthenticatedUserUtil.EMAIL));
        if (existingVisitor.isEmpty()) {
            Visitor visitor = create(user);
            save(visitor);
        }
    }
}
