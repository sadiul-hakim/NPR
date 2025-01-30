package xyz.sadiulhakim.npr.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;

@Service
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    static final String SESSION_KEY_HEADER = "simpSessionId";
    static final String WS_ID_HEADER = "ws-id";

    private final Logger LOGGER = LoggerFactory.getLogger(WebSocketChannelInterceptor.class);

    /**
     * Processes a message before sending it
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = readHeaderAccessor(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {

            String wsId = readWebSocketIdHeader(accessor); // userId
            String sessionId = readSessionId(accessor);

            // authenticate the user and if that's successful add their user information to the headers.
            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(wsId, null);
            accessor.setUser(user);
            accessor.setHeader("connection-time", LocalDateTime.now().toString());
            LOGGER.debug("User with ws-id {} session {} make a WebSocket connection and generated the user {}",
                    wsId, sessionId, user);
        }
        return message;
    }

    /**
     * Instantiate an object for retrieving the STOMP headers
     */
    private StompHeaderAccessor readHeaderAccessor(Message<?> message) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new AuthenticationCredentialsNotFoundException("Fail to read headers.");
        }
        return accessor;
    }

    private String readWebSocketIdHeader(StompHeaderAccessor accessor) {
        final String wsId = accessor.getFirstNativeHeader(WS_ID_HEADER);
        if (wsId == null || wsId.trim().isEmpty())
            throw new AuthenticationCredentialsNotFoundException("Web Socket ID Header not found");
        return wsId;
    }

    private String readSessionId(StompHeaderAccessor accessor) {
        return ofNullable(accessor.getMessageHeaders().get(SESSION_KEY_HEADER))
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Session header not found")).toString();
    }
}
