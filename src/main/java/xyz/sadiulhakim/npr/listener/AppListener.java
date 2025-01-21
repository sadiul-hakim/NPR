package xyz.sadiulhakim.npr.listener;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class AppListener {

    @EventListener
    void serverInitialized(WebServerInitializedEvent event) {
        System.out.println("Server is running on :" + event.getWebServer().getPort());
    }
}
