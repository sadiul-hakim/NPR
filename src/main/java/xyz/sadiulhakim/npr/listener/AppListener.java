package xyz.sadiulhakim.npr.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
class AppListener {

    @Async("defaultTaskExecutor")
    @EventListener
    void serverInitialized(WebServerInitializedEvent event) {
        System.out.println("Server is running on :" + event.getWebServer().getPort());
    }

    @Async("defaultTaskExecutor")
    @EventListener
    void applicationReady(ApplicationReadyEvent event) {
        System.out.println("Application is started in " + event.getTimeTaken().getSeconds() + " seconds");
    }

    @Async("defaultTaskExecutor")
    @EventListener
    void applicationClosed(ContextClosedEvent event) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        System.out.println("Application is closed at " + formatter.format(LocalDateTime.now()));
    }
}
