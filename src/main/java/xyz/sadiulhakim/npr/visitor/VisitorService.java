package xyz.sadiulhakim.npr.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VisitorService {

    private final Logger LOGGER = LoggerFactory.getLogger(VisitorService.class);

    @Value("${default.pagination.size:0}")
    private int paginationSize;
    private final VisitorRepo visitorRepo;

    public VisitorService(VisitorRepo visitorRepo) {
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
}
