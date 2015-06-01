package org.openhds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.openhds.repository.util.SampleDataGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Created by Ben on 5/4/15.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableSpringDataWebSupport
@EnableEntityLinks
public class OpenHdsRestApplication {

    @Bean
    public CommandLineRunner initWithSampleData(SampleDataGenerator sampleDataGenerator) {
        return (args) -> {
            sampleDataGenerator.clearData();
            sampleDataGenerator.generateSampleData();
        };
    }

    @Bean
    public MappingJackson2HttpMessageConverter jacksonHibernateSupport() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate4Module());
        converter.setObjectMapper(mapper);
        return converter;
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenHdsRestApplication.class, args);
    }
}

