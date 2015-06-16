package org.openhds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.openhds.repository.util.SampleDataGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

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
    public MappingJackson2HttpMessageConverter jsonConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        configureObjectMapper(mapper);
        converter.setObjectMapper(mapper);
        return converter;
    }

    @Bean
    public MappingJackson2XmlHttpMessageConverter xmlConverter() {
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
        ObjectMapper mapper = new XmlMapper();
        configureObjectMapper(mapper);
        converter.setObjectMapper(mapper);
        return converter;
    }

    private static void configureObjectMapper(ObjectMapper mapper) {
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenHdsRestApplication.class, args);
    }
}

