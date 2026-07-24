package org.solorossi.fluxcapacitor.configuration;

import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

/**
 * Sort the schema names alphabetically, but don't sort the attributes.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenApiCustomizer sortSchemasAlphabetically() {

        return openApi -> {
            if ( openApi.getComponents() != null && openApi.getComponents().getSchemas() != null ) {
                Map<String, Schema> schemas = openApi.getComponents().getSchemas();
                openApi.getComponents().setSchemas( new TreeMap<>( schemas ) );
            }
        };
    }
}
