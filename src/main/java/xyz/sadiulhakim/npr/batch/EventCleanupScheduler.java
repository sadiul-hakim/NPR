package xyz.sadiulhakim.npr.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventCleanupScheduler {

    private final JobLauncher jobLauncher;
    private final Job cleanupJob;

    public EventCleanupScheduler(JobLauncher jobLauncher, Job cleanupJob) {
        this.jobLauncher = jobLauncher;
        this.cleanupJob = cleanupJob;
    }

    @Scheduled(cron = "0 0 0 * * *") // Cron expression to run daily at midnight
    public void runBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runTime", LocalDateTime.now().toString()) // Ensure a unique job instance
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(cleanupJob, jobParameters);
            System.out.println("Cleanup job started: " + execution.getStatus());
        } catch (Exception e) {
            System.err.println("Job execution failed: " + e.getMessage());
        }
    }
}
