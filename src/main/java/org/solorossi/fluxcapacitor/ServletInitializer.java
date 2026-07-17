package org.solorossi.fluxcapacitor;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    @NonNull
    protected SpringApplicationBuilder configure( @NonNull SpringApplicationBuilder application ) {

        return application.sources( FluxCapacitorApplication.class );
    }

}
