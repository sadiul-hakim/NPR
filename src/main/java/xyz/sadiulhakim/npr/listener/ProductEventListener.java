package xyz.sadiulhakim.npr.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.event.EntityEventType;
import xyz.sadiulhakim.npr.notification.Notification;
import xyz.sadiulhakim.npr.product.event.ProductEvent;
import xyz.sadiulhakim.npr.product.model.ProductService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
class ProductEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductEventListener.class);

    @Value("${application.broadcaster.channel:''}")
    private String broadcasterChannel;

    private final ProductService productService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper;

    ProductEventListener(ProductService productService, SimpMessagingTemplate messagingTemplate, ObjectMapper mapper) {
        this.productService = productService;
        this.messagingTemplate = messagingTemplate;
        this.mapper = mapper;
    }

    @Async("defaultTaskExecutor")
    @EventListener
    void brandDeleteEvent(ProductEvent event) {

        if (event.type().equals(EntityEventType.DELETED)) {
            deleteBrand(event);
        } else if (event.type().equals(EntityEventType.CREATED)) {
            sendNotification(event);
        }
    }

    void deleteBrand(ProductEvent event) {
        var brand = productService.getByName(event.name());
        brand.ifPresent(b -> {
            LOGGER.info("ProductEventListener :: deleting product {}", event.name());
            productService.forceDelete(b);
        });
    }

    void sendNotification(ProductEvent event) {

        try {
            String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            Notification notification = new Notification("Product", "New product " + event.name() + " is now available!",
                    localDateTime, false);
            messagingTemplate.convertAndSend(broadcasterChannel, mapper.writeValueAsString(notification));
        } catch (Exception ex) {
            LOGGER.info("ProductEventListener.sendNotification :: could not send notification on product creation!");
        }
    }
}
