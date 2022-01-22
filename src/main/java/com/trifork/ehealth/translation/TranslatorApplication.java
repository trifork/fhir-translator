package com.trifork.ehealth.translation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class TranslatorApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(TranslatorApplication.class);
    private final ApplicationContext applicationContext;
    private final TranslationRunner translationRunner;

    public TranslatorApplication(ApplicationContext applicationContext, TranslationRunner translationRunner) {
        this.applicationContext = applicationContext;
        this.translationRunner = translationRunner;
    }

    public static void main(String[] args) {
        SpringApplication.run(TranslatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        try {
            logger.info("Running {}", translationRunner.getClass().getSimpleName());
            translationRunner.execute();
        } finally {
            logger.info("Execution finished");
            SpringApplication.exit(applicationContext);
        }
    }

}
