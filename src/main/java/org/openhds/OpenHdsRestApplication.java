package org.openhds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.openhds.repository.generator.MasterDataGenerator;
import org.openhds.resource.converter.EntityCollectionMessageWriter;
import org.openhds.resource.converter.JsonArrayDelimiter;
import org.openhds.resource.converter.XmlElementDelimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.Validator;
import java.util.List;

/**
 * Created by Ben on 5/4/15.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableSpringDataWebSupport
@EnableEntityLinks
public class OpenHdsRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenHdsRestApplication.class, args);
    }

    @Bean
    public CommandLineRunner initWithSampleData(MasterDataGenerator masterDataGenerator) {
        return (args) -> {
            // TODO: optional command line argument to generate data
        };
    }

    @Bean(name = "projectCodeMap")
    public YamlMapFactoryBean yamlMapFactoryBean() {
        YamlMapFactoryBean yamlMapFactoryBean = new YamlMapFactoryBean();
        yamlMapFactoryBean.setResources(new ClassPathResource("project-codes.yml"));
        return yamlMapFactoryBean;
    }

    @Bean
    public Validator beanValidator() {
        return new LocalValidatorFactoryBean();
    }

    @EnableWebMvc
    @Configuration
    public static class WebConfig extends WebMvcConfigurerAdapter {
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

        @Bean
        EntityCollectionMessageWriter jsonPagedMessageWriter() {
            return new EntityCollectionMessageWriter(jsonConverter(), new JsonArrayDelimiter());
        }

        @Bean
        EntityCollectionMessageWriter xmlPagedMessageWriter() {
            return new EntityCollectionMessageWriter(xmlConverter(), new XmlElementDelimiter());
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(jsonPagedMessageWriter());
            converters.add(xmlPagedMessageWriter());
            converters.add(jsonConverter());
            converters.add(xmlConverter());
        }
    }

    private static void configureObjectMapper(ObjectMapper mapper) {
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
