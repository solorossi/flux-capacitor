package org.solorossi.fluxcapacitor.rest;

import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api" )
public class FluxRestController {

    private FluxCapacitorService fluxCapacitorService;

    @Autowired
    public FluxRestController( FluxCapacitorService fluxCapacitorService ) {

        this.fluxCapacitorService = fluxCapacitorService;
    }

    @GetMapping( "/help" )
    public String getHelp() {

        return "Help";
    }
}
