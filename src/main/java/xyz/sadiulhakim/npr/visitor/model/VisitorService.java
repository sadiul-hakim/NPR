package xyz.sadiulhakim.npr.visitor.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.PageUtil;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;
import xyz.sadiulhakim.npr.visitor.event.VisitorEvent;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@NamedInterface("visitor-service")
public class VisitorService {

    private final Logger LOGGER = LoggerFactory.getLogger(VisitorService.class);
    private final VisitorRepo visitorRepo;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher eventPublisher;

    VisitorService(VisitorRepo visitorRepo, AppProperties appProperties, ApplicationEventPublisher eventPublisher) {
        this.visitorRepo = visitorRepo;
        this.appProperties = appProperties;
        this.eventPublisher = eventPublisher;
    }

    public void save(Visitor visitor) {
        try {
            LOGGER.info("VisitorService.save :: Saving visitor {}", visitor.getEmail());
            visitorRepo.save(visitor);
        } catch (Exception ex) {
            LOGGER.error("VisitorService.save :: error {}", ex.getMessage());
        }
    }

    public void updateInformation(Visitor visitor) {

        Optional<Visitor> exVisitor = getByMail(visitor.getEmail());
        if (exVisitor.isEmpty())
            return;

        Visitor v = exVisitor.get();
        v.setPicture(visitor.getPicture());
        v.setSub(visitor.getSub());
        v.setName(visitor.getName());
        v.setLastVisited(LocalDateTime.now());

        save(v);
    }

    public Optional<Visitor> getByMail(String mail) {

        LOGGER.info("VisitorService.getByMail :: Getting Visitor by mail {}", mail);
        return visitorRepo.findByEmail(mail);
    }

    public Optional<Visitor> getById(long id) {

        LOGGER.info("VisitorService.getById :: Getting Visitor by id {}", id);
        return visitorRepo.findById(id);
    }

    public Optional<Visitor> getBySub(String sub) {

        LOGGER.info("VisitorService.getBySub :: Getting Visitor by sub {}", sub);
        return visitorRepo.findBySub(sub);
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        return findAllPaginatedWithSize(pageNumber, appProperties.getPaginationSize());
    }

    public PaginationResult findAllPaginatedWithSize(int pageNumber, int size) {

        LOGGER.info("VisitorService.findAllPaginated :: finding visitor page : {}", pageNumber);
        Page<Visitor> page = visitorRepo.findAll(PageRequest.of(pageNumber, size, Sort.by("name")));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult search(String text, int pageNumber) {

        LOGGER.info("VisitorService.search :: search visitor by text : {}", text);
        Page<Visitor> page = visitorRepo.findAllByEmailContainingOrSubContainingOrNameContaining(text, text, text,
                PageRequest.of(pageNumber, 200));
        return PageUtil.prepareResult(page);
    }

    public long count() {
        return visitorRepo.numberOfBrands();
    }

    public byte[] getCsvData() {

        final int batchSize = 500;
        int batchNumber = 0;
        StringBuilder sb = new StringBuilder("Id,Name,Picture,Email,Sub\n");
        Page<Visitor> page;
        do {
            page = visitorRepo.findAll(PageRequest.of(batchNumber, batchSize));
            List<Visitor> visitors = page.getContent();
            for (Visitor visitor : visitors) {
                sb.append(visitor.getId())
                        .append(",")
                        .append(visitor.getName())
                        .append(",")
                        .append(visitor.getPicture())
                        .append(",")
                        .append(visitor.getEmail())
                        .append(",")
                        .append(visitor.getSub())
                        .append("\n");
            }
            batchNumber++;
        } while (page.hasNext());

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void forceDelete(Visitor visitor) {
        visitorRepo.delete(visitor);
    }

    @Transactional
    public void delete(long visitorId) {
        eventPublisher.publishEvent(new VisitorEvent(visitorId));
    }

    private Visitor create(OAuth2User user) {
        var visitor = new Visitor();
        visitor.setName(user.getAttribute(AuthenticatedUserUtil.NAME));
        visitor.setEmail(user.getAttribute(AuthenticatedUserUtil.EMAIL));
        visitor.setPicture(user.getAttribute(AuthenticatedUserUtil.PICTURE));
        visitor.setSub(user.getAttribute(AuthenticatedUserUtil.SUB));
        visitor.setLastVisited(LocalDateTime.now());

        return visitor;
    }

    public void createVisitor(OAuth2User user) {
        Optional<Visitor> existingVisitor = getByMail(user.getAttribute(AuthenticatedUserUtil.EMAIL));
        Visitor visitor = create(user);
        if (existingVisitor.isEmpty()) {
            save(visitor);
        } else {
            updateInformation(visitor);
        }
    }
}
