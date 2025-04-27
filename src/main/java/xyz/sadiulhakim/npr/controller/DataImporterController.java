package xyz.sadiulhakim.npr.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/data_importer")
public class DataImporterController {

    @Qualifier("brandAndCategoryImporter")
    private final Job brandAndCategoryImporter;
    private final JobLauncher jobLauncher;
    private final AppProperties appProperties;

    public DataImporterController(Job brandAndCategoryImporter, JobLauncher jobLauncher,
                                  AppProperties appProperties) {
        this.brandAndCategoryImporter = brandAndCategoryImporter;
        this.jobLauncher = jobLauncher;
        this.appProperties = appProperties;
    }

    @GetMapping("/page")
    String importerPage(Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        return "importer_page";
    }

    @GetMapping("/brand-and-category")
    String importBrandAndCategory() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {
        JobParameters parameters = new JobParametersBuilder()
                .addLocalDateTime("startAt", LocalDateTime.now())
                .addString("id", UUID.randomUUID().toString())
                .toJobParameters();
        jobLauncher.run(brandAndCategoryImporter, parameters);
        return "importer_page";
    }
}
