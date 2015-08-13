package org.openhds.documentation;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by ben on 8/13/15.
 */
@Configuration
public class DocumentationConfiguration {

    @Bean
    PegDownProcessor pegDownProcessor() {
        return new PegDownProcessor(Extensions.ALL);
    }

}
