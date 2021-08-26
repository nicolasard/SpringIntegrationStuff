package ar.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Configuration
public class IntegrationConfiguration {

    @Bean
    public FileToJobLaunchRequestTransformer fileToJobLaunchRequestTransformer(Job job) {
        return new FileToJobLaunchRequestTransformer(job, "filename");
    }

    @Bean
    public JobLaunchingMessageHandler jobLaunchingMessageHandler(JobLauncher jobLauncher) {
        return new JobLaunchingMessageHandler(jobLauncher);
    }

    @Bean
    public IntegrationFlow fileToBatchFlow(FileToJobLaunchRequestTransformer fileToJobLaunchRequestTransformer
            , JobLaunchingMessageHandler jobLaunchingMessageHandler
            , @Value("file:${user.home}/customerstoimport/new/") File directory) {
        return IntegrationFlows.from(
                Files.inboundAdapter(directory).patternFilter("customers-*.txt"), s -> s.poller(Pollers.fixedRate(10, TimeUnit.SECONDS)))
                .transform(fileToJobLaunchRequestTransformer)
                .handle(jobLaunchingMessageHandler)
                .get();
    }
}