package org.solorossi.fluxcapacitor.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api" )
public class FluxRestController {

    @GetMapping( "/help" )
    public String getHelp() {

        return "Help";
    }
}
