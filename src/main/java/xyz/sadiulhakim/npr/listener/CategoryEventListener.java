package xyz.sadiulhakim.npr.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.category.event.CategoryEvent;
import xyz.sadiulhakim.npr.category.model.CategoryService;
import xyz.sadiulhakim.npr.event.EntityEventType;
import xyz.sadiulhakim.npr.notification.Notification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
class CategoryEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(CategoryEventListener.class);
    private final CategoryService categoryService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper;

    @Value("${application.broadcaster.channel:''}")
    private String broadcasterChannel;

    public CategoryEventListener(CategoryService categoryService, SimpMessagingTemplate messagingTemplate, ObjectMapper mapper) {
        this.categoryService = categoryService;
        this.messagingTemplate = messagingTemplate;
        this.mapper = mapper;
    }

    @ApplicationModuleListener
    void brandDeleteEvent(CategoryEvent event) {

        if (event.type().equals(EntityEventType.DELETED)) {
            delete(event);
        } else if (event.type().equals(EntityEventType.CREATED)) {
            sendNotification(event);
        }
    }

    void sendNotification(CategoryEvent event) {

        try {

            String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            Notification notification = new Notification("Category", "New category " + event.name() + " is now available!",
                    localDateTime, false);
            messagingTemplate.convertAndSend(broadcasterChannel, mapper.writeValueAsString(notification));
        } catch (Exception ex) {
            LOGGER.info("CategoryEventListener.sendNotification :: could not send notification on band create!");
        }
    }

    void delete(CategoryEvent event) {
        var category = categoryService.getByName(event.name());
        category.ifPresent(b -> {
            LOGGER.info("CategoryEventListener :: deleting category {}", event.name());
            categoryService.forceDelete(b);
        });
    }
}
