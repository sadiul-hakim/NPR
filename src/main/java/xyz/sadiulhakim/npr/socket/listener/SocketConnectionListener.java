package xyz.sadiulhakim.npr.socket.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Optional;


@Component
public class SocketConnectionListener {

    private final Logger LOGGER = LoggerFactory.getLogger(SocketConnectionListener.class);

    @EventListener({SessionConnectEvent.class})
    public void connectionListener(SessionConnectEvent event) {
        Optional.ofNullable(readUser(event)).ifPresent(user -> log(event, user));
    }

    @EventListener({SessionDisconnectEvent.class})
    public void disconnectionListener(SessionDisconnectEvent event) {
        Optional.ofNullable(event.getUser()).ifPresent(user ->
                LOGGER.info("USer {} disconnected from session id {}", user.getName(), event.getSessionId())
        );
    }

    private void log(SessionConnectEvent event, Principal user) {
        String sessionId = readSessionId(event);
        LOGGER.info("User {} connected to session id {}", user.getName(), sessionId);
    }

    String readSessionId(SessionConnectEvent event) {
        return SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders());
    }

    Principal readUser(SessionConnectEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        return SimpMessageHeaderAccessor.getUser(headers);
    }
}
