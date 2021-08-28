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
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.file.dsl.Files;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Configuration
public class IntegrationConfiguration {

    @Bean
    public FileToJobRequestTransformer fileToJobLaunchRequestTransformer(Job job) {
        return new FileToJobRequestTransformer(job, "filename");
    }

    @Bean
    public JobLaunchingMessageHandler jobLaunchingMessageHandler(JobLauncher jobLauncher) {
        return new JobLaunchingMessageHandler(jobLauncher);
    }

    @Bean
    public IntegrationFlow fileToBatchFlow(FileToJobRequestTransformer fileToJobLaunchRequestTransformer
            , JobLaunchingMessageHandler jobLaunchingMessageHandler
            , @Value("file:${user.home}/customerstoimport/new/") File directory) {
        return IntegrationFlows.from(
                Files.inboundAdapter(directory).patternFilter("customers-*.txt"), new Consumer<SourcePollingChannelAdapterSpec>() {
					public void accept(SourcePollingChannelAdapterSpec s) {
						s.poller(Pollers.fixedRate(10, TimeUnit.SECONDS));
					}
				})
                .transform(fileToJobLaunchRequestTransformer)
                .handle(jobLaunchingMessageHandler)
                .get();
    }
}