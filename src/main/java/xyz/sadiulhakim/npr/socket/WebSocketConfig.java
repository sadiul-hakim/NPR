package xyz.sadiulhakim.npr.socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${socket.application.destination.prefix:''}")
    private String destinationPrefix;

    @Value("${socket.user.destination.prefix:''}")
    private String userPrefix;

    @Value("${socket.register.endpoint:''}")
    private String registerEndpoint;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        SimpleAsyncTaskScheduler taskScheduler = new SimpleAsyncTaskScheduler();
        taskScheduler.setVirtualThreads(true);

        registry.enableSimpleBroker("/queue", "/topic")
                .setTaskScheduler(taskScheduler);
        registry.setApplicationDestinationPrefixes(destinationPrefix);
        registry.setUserDestinationPrefix(userPrefix);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(registerEndpoint)
                .withSockJS();
    }
}
