package ar.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Configuration
@ComponentScan(basePackageClasses = FileProcessingJob.class)
public class IntegrationConfiguration {
	
    private static final MessageChannel messageChannel = null;
    
	Logger logger = LoggerFactory.getLogger(IntegrationConfiguration.class);
	
    @Bean
    public FileToJobRequestTransformer fileToJobLaunchRequestTransformer(FileProcessingJob fileProcessingJob) {
    	logger.info("Launching job...");
        return new FileToJobRequestTransformer(fileProcessingJob.getJob(), "filename");
     }

    @Bean
    public JobLaunchingMessageHandler jobLaunchingMessageHandler(JobLauncher jobLauncher) {
        return new JobLaunchingMessageHandler(jobLauncher);
    }

    @Bean
    public IntegrationFlow fileToBatchFlow(FileToJobRequestTransformer fileToJobLaunchRequestTransformer
            , JobLaunchingMessageHandler jobLaunchingMessageHandler
            , @Value("file:${user.home}/customerstoimport/new/") final File directory
            , @Value("10") final long pollingtime) {
    	logger.info(String.format("Spring Integration Inbound File Adapter directory: %s , pooling time: %d", directory.toString(), pollingtime));
        return IntegrationFlows
        		.from(
                Files.inboundAdapter(directory).patternFilter("customers-*.txt"), new Consumer<SourcePollingChannelAdapterSpec>() {
					public void accept(SourcePollingChannelAdapterSpec s) {
						s.poller(Pollers.fixedRate(pollingtime, TimeUnit.SECONDS));
					}
				})
                .transform(fileToJobLaunchRequestTransformer)
                .handle(jobLaunchingMessageHandler)
                .channel("inputMessageChannel")
                .get();
    }
    
    @ServiceActivator(inputChannel = "inputMessageChannel")
    public void messageReceiver(
        String payload) {
    	logger.info("Message arrived via an inbound channel adapter from sub-one! Payload: " + payload);
    }
}