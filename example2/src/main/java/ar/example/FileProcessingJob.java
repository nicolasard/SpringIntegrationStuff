package ar.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = org.springframework.batch.core.configuration.annotation.JobBuilderFactory.class)
public class FileProcessingJob{
	
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public Job getJob() {
        return jobBuilderFactory.get("TeradataJobConfig")
                .incrementer(new RunIdIncrementer())
                .start(configurationStep())
                .build();
    }
    
    @Bean
    protected Step configurationStep() {
        return stepBuilderFactory
                .get("ConfigurationStep")
                .tasklet(new ConfigurationTasklet())
                .build();
    }
}
