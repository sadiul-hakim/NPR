package xyz.sadiulhakim.npr.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.visitor.event.VisitorEvent;
import xyz.sadiulhakim.npr.visitor.model.VisitorService;

@Component
public class VisitorListener {

    private final Logger LOGGER = LoggerFactory.getLogger(VisitorListener.class);
    private final VisitorService visitorService;

    public VisitorListener(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @Async("defaultTaskExecutor")
    @EventListener
    void deleteRole(VisitorEvent event) {

        var visitor = visitorService.getById(event.id());
        visitor.ifPresent(visitorService::forceDelete);
        LOGGER.info("VisitorEventListener :: deleting visitor {}", event.id());
    }
}
