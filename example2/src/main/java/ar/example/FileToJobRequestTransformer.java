package ar.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;

import java.io.File;

public class FileToJobRequestTransformer {
    private final Job job;
    private final String fileParameterName;

    public FileToJobRequestTransformer(Job job, String fileParameterName) {
        this.job = job;
        this.fileParameterName = fileParameterName;
    }

    @Transformer
    public JobLaunchRequest transform(File file) {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("fileParameterName", file.getAbsolutePath());
        return new JobLaunchRequest(job, builder.toJobParameters());
    }
}