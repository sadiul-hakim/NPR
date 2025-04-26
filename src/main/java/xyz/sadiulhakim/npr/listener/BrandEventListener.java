package xyz.sadiulhakim.npr.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.brand.event.BrandEvent;
import xyz.sadiulhakim.npr.brand.model.BrandService;
import xyz.sadiulhakim.npr.event.EntityEventType;
import xyz.sadiulhakim.npr.notification.Notification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
class BrandEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(BrandEventListener.class);

    @Value("${application.broadcaster.channel:''}")
    private String broadcasterChannel;

    private final BrandService brandService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper;

    public BrandEventListener(BrandService brandService, SimpMessagingTemplate messagingTemplate, ObjectMapper mapper) {
        this.brandService = brandService;
        this.messagingTemplate = messagingTemplate;
        this.mapper = mapper;
    }

    @Async("defaultTaskExecutor")
    @EventListener
    void brandDeleteEvent(BrandEvent event) {

        if (event.type().equals(EntityEventType.DELETED)) {
            deleteBrand(event);
        } else if (event.type().equals(EntityEventType.CREATED)) {
            sendNotification(event);
        }
    }

    void deleteBrand(BrandEvent event) {
        var brand = brandService.getByName(event.name());
        brand.ifPresent(b -> {
            LOGGER.info("BrandEventListener :: deleting brand {}", event.name());
            brandService.forceDelete(b);
        });
    }

    void sendNotification(BrandEvent event) {

        try {
            String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            Notification notification = new Notification("Brand", "New brand " + event.name() + " is now available!",
                    localDateTime, false);
            messagingTemplate.convertAndSend(broadcasterChannel, mapper.writeValueAsString(notification));
        } catch (Exception ex) {
            LOGGER.info("BrandEventListener.sendNotification :: could not send notification on band create!");
        }
    }

}
