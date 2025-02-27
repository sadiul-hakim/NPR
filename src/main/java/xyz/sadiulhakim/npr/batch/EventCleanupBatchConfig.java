package xyz.sadiulhakim.npr.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class EventCleanupBatchConfig {

    private final EventPublicationRepository eventPublicationRepo;

    public EventCleanupBatchConfig(EventPublicationRepository eventPublicationRepo) {
        this.eventPublicationRepo = eventPublicationRepo;
    }

    @Bean
    public Job cleanupJob(JobRepository jobRepository, Step cleanupStep) {
        return new JobBuilder("cleanupJob", jobRepository)
                .start(cleanupStep)
                .build();
    }

    @Bean
    public Step cleanupStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("cleanupStep", jobRepository)
                .<Instant, Instant>chunk(10, transactionManager)
                .reader(eventReader())
                .processor(eventProcessor())
                .writer(eventWriter())
                .build();
    }

    @Bean
    public ItemReader<Instant> eventReader() {
        Instant cutoffDate = LocalDateTime.now().minusDays(2).atZone(ZoneId.systemDefault()).toInstant();
        return new ListItemReader<>(List.of(cutoffDate));
    }

    @Bean
    public ItemProcessor<Instant, Instant> eventProcessor() {
        return instant -> instant; // No processing needed, just pass it through
    }

    @Bean
    public ItemWriter<Instant> eventWriter() {
        return items -> items.forEach(this::deleteOldEventPublications);
    }

    @Transactional
    public void deleteOldEventPublications(Instant cutoffDate) {
        eventPublicationRepo.deleteCompletedPublicationsBefore(cutoffDate);
    }
}