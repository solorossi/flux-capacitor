package org.solorossi.fluxcapacitor.exception;

import org.springframework.validation.MapBindingResult;

import java.util.HashMap;

/**
 * An error collector for the business (service) layer that is similar to the Spring MVC
 * binding results error collector for web page validation.
 */
public class BusinessErrors extends MapBindingResult {

    public BusinessErrors() {

        super( new HashMap<String, Object>(), "businessErrors" );
    }
}
