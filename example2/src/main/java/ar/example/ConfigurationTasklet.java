package ar.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationTasklet implements Tasklet{
	
    Logger logger = LoggerFactory.getLogger(Tasklet.class);

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info(String.format("Running tasklet on job instance ID %s", chunkContext.getStepContext().getJobInstanceId()));
		return RepeatStatus.FINISHED;
	}
}
