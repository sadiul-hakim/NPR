package xyz.sadiulhakim.npr.data_importer_batch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import xyz.sadiulhakim.npr.brand.model.Brand;
import xyz.sadiulhakim.npr.category.model.Category;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.FileUtil;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;

@EnableBatchProcessing
@Configuration
public class BrandAndCategoryImporterJob extends DefaultBatchConfiguration {


    @Value("file:F:\\NPR\\importer\\Brand.csv")
    Resource brandData;

    @Value("file:F:\\NPR\\importer\\Category.csv")
    Resource categoryData;

    private final AppProperties appProperties;

    public BrandAndCategoryImporterJob(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    ItemReader<Brand> brandItemReader() {
        return new FlatFileItemReaderBuilder<Brand>()
                .name("brandItemReadr")
                .resource(brandData)
                .linesToSkip(1)
                .lineMapper((line, lineNumber) -> {
                    String[] split = line.split(",");
                    return new Brand(0, split[1], split[2]);
                })
                .build();
    }

    @Bean
    ItemReader<Category> categoryItemReader() {
        return new FlatFileItemReaderBuilder<Category>()
                .name("categoryItemReadr")
                .resource(categoryData)
                .linesToSkip(1)
                .lineMapper((line, lineNumber) -> {
                    String[] split = line.split(",");
                    return new Category(0, split[1], split[2]);
                })
                .build();
    }

    @Bean
    ItemProcessor<Brand, Brand> processBrand() {
        return item -> {

            File photo = new File(item.getPicture());
            if (photo.getName().equals(appProperties.getDefaultBrandPhotoName()) || !photo.exists()) {
                item.setPicture(appProperties.getDefaultBrandPhotoName());
                return item;
            }
            try (FileInputStream is = new FileInputStream(photo)) {
                String fileName = FileUtil.uploadFile(appProperties.getBrandImageFolder(), photo.getName(),
                        is);
                if (fileName.isEmpty()) {
                    item.setPicture(appProperties.getDefaultBrandPhotoName());
                } else {
                    item.setPicture(fileName);
                }
            }
            return item;
        };
    }

    @Bean
    ItemProcessor<Category, Category> processCategory() {
        return item -> {

            File photo = new File(item.getPicture());
            if (photo.getName().equals(appProperties.getDefaultCategoryPhotoName()) || !photo.exists()) {
                item.setPicture(appProperties.getDefaultCategoryPhotoName());
                return item;
            }
            try (FileInputStream is = new FileInputStream(photo)) {
                String fileName = FileUtil.uploadFile(appProperties.getCategoryImageFolder(), photo.getName(),
                        is);
                if (fileName.isEmpty()) {
                    item.setPicture(appProperties.getDefaultCategoryPhotoName());
                } else {
                    item.setPicture(fileName);
                }
            }
            return item;
        };
    }

    @Bean
    ItemWriter<Brand> brandItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Brand>()
                .dataSource(dataSource)
                .sql("insert into brand(name,picture,active) values(?,?,?) ON CONFLICT(name) DO NOTHING")
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setString(1, item.getName());
                    ps.setString(2, item.getPicture());
                    ps.setBoolean(3, item.isActive());
                })
                .build();
    }

    @Bean
    ItemWriter<Category> categoryItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Category>()
                .dataSource(dataSource)
                .sql("insert into category(name,picture,active) values(?,?,?) ON CONFLICT(name) DO NOTHING")
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setString(1, item.getName());
                    ps.setString(2, item.getPicture());
                    ps.setBoolean(3, item.isActive());
                })
                .build();
    }

    @Bean
    Step brandImporterStep(JobRepository repository, PlatformTransactionManager transactionManager,
                           @Qualifier("brandItemReader") ItemReader<Brand> brandItemReader,
                           @Qualifier("processBrand") ItemProcessor<Brand, Brand> processBrand,
                           @Qualifier("brandItemWriter") ItemWriter<Brand> brandItemWriter) {
        return new StepBuilder("brandImporterStep", repository)
                .<Brand, Brand>chunk(20, transactionManager)
                .reader(brandItemReader)
                .processor(processBrand)
                .writer(brandItemWriter)
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .skipLimit(100)
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        System.out.println("Done importing Brand Data");
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .listener(new SkipListener<>() {
                    @Override
                    public void onSkipInWrite(Object item, Throwable t) {
                        System.out.printf("Skipped error %s on item %s\n", t.getClass().getName(), item);
                        SkipListener.super.onSkipInWrite(item, t);
                    }
                })
                .build();
    }

    @Bean
    Step categoryImporterStep(JobRepository repository, PlatformTransactionManager transactionManager,
                              @Qualifier("categoryItemReader") ItemReader<Category> categoryItemReader,
                              @Qualifier("processCategory") ItemProcessor<Category, Category> processCategory,
                              @Qualifier("categoryItemWriter") ItemWriter<Category> categoryItemWriter) {
        return new StepBuilder("categoryImporterStep", repository)
                .<Category, Category>chunk(20, transactionManager)
                .reader(categoryItemReader)
                .processor(processCategory)
                .writer(categoryItemWriter)
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .skipLimit(100)
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        System.out.println("Done importing Category Data");
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .listener(new SkipListener<>() {
                    @Override
                    public void onSkipInWrite(Object item, Throwable t) {
                        System.out.printf("Skipped error %s on item %s\n", t.getClass().getName(), item);
                        SkipListener.super.onSkipInWrite(item, t);
                    }
                })
                .build();
    }

    @Bean
    Job brandAndCategoryImporter(@Qualifier("categoryImporterStep") Step categoryImporterStep,
                                 @Qualifier("brandImporterStep") Step brandImporterStep,
                                 JobRepository jobRepository) {
        SimpleFlow brandImporterFlow = new FlowBuilder<SimpleFlow>("brandImporterFlow")
                .start(brandImporterStep)
                .build();
        SimpleFlow categoryImporterFlow = new FlowBuilder<SimpleFlow>("categoryImporterFlow")
                .start(categoryImporterStep)
                .build();

        SimpleAsyncTaskScheduler taskScheduler = new SimpleAsyncTaskScheduler();
        taskScheduler.setVirtualThreads(true);

        SimpleFlow parallelFlow = new FlowBuilder<SimpleFlow>("parallelFlow")
                .split(taskScheduler)
                .add(brandImporterFlow, categoryImporterFlow)
                .build();

        return new JobBuilder("brandAndCategoryImporter", jobRepository)
                .start(parallelFlow)
                .build()
                .build();
    }
}
