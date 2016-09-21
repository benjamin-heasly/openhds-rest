package org.openhds;


import org.openhds.domain.model.FieldWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.security.Principal;
import java.util.Date;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
                .directModelSubstitute(FieldWorker.class, String.class)
                .select()
                .paths(regex("/.*"))
                .build()
                .ignoredParameterTypes(Principal.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Open HDS Rest")
                .description("Rest Service for OpenHDS data")
                .build();
    }
}