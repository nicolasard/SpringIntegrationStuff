package ar.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.IOException;

@SpringBootApplication
@ComponentScan("ar.example")
public class Main {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(IntegrationConfiguration.class);

        System.out.println("Press [Enter] to exit.");
        System.in.read();

        context.close();
    }
}