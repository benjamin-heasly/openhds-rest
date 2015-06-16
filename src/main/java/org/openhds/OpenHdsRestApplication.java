package org.openhds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
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

        // don't serialize hibernate proxies
        mapper.registerModule(new Hibernate4Module());

        // respect JAXB annotations
        mapper.registerModule(new JaxbAnnotationModule());

        // work with java.time
        mapper.registerModule(new JSR310Module());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        converter.setObjectMapper(mapper);
        return converter;
    }

    @Bean
    public MappingJackson2XmlHttpMessageConverter xmlConverter() {
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
        ObjectMapper mapper = new XmlMapper();

        // don't serialize hibernate proxies
        mapper.registerModule(new Hibernate4Module());

        // respect JAXB annotations
        mapper.registerModule(new JaxbAnnotationModule());

        // work with java.time
        mapper.registerModule(new JSR310Module());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        converter.setObjectMapper(mapper);
        return converter;
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenHdsRestApplication.class, args);
    }
}

