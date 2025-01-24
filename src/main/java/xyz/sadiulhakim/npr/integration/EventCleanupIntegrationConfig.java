package xyz.sadiulhakim.npr.integration;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.modulith.events.core.EventPublicationRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Configuration
class EventCleanupIntegrationConfig {

    private final EventPublicationRepository eventPublicationRepo;

    EventCleanupIntegrationConfig(EventPublicationRepository eventPublicationRepo) {
        this.eventPublicationRepo = eventPublicationRepo;
    }

    @Bean
    IntegrationFlow integrationFlow() {
        return IntegrationFlow.fromSupplier(() -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("cutoffDate", LocalDateTime.now().minusDays(2));
                    return new GenericMessage<>(params);
                }, e -> e.poller(p -> p.cron("0 * * * * *"))) // Every hour
                .handle(this::deleteOldEventPublications)
                .get();
    }

    @Transactional
    void deleteOldEventPublications(Message<?> message) {
        var payload = (Map<String, Object>) message.getPayload();
        var dateTime = (LocalDateTime) payload.get("cutoffDate");
        var instantDate = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        eventPublicationRepo.deleteCompletedPublicationsBefore(instantDate);
    }
}
