package org.solorossi.fluxcapacitor.exception;

import org.springframework.validation.MapBindingResult;

import java.util.HashMap;

public class BusinessErrors extends MapBindingResult {

    public BusinessErrors() {

        super( new HashMap<String, Object>(), "businessErrors" );
    }
}
