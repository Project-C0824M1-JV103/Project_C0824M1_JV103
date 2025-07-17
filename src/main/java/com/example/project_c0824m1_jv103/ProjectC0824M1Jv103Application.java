package com.example.project_c0824m1_jv103;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ProjectC0824M1Jv103Application {

    public static void main(String[] args) {
        // Load environment variables from .env file into System properties
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        SpringApplication.run(ProjectC0824M1Jv103Application.class, args);
    }

}
