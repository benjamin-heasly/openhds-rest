package org.openhds;

import org.openhds.domain.util.SampleDataGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Created by Ben on 5/4/15.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableSpringDataWebSupport
public class OpenHdsRestApplication {

    @Bean
    public CommandLineRunner initOpenHDS(SampleDataGenerator sampleDataGenerator) {
        return (args) -> {
            sampleDataGenerator.clearData();
            sampleDataGenerator.generateSampleData();
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenHdsRestApplication.class, args);
    }
}

