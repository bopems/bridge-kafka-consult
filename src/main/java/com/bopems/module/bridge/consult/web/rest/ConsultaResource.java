package com.bopems.module.bridge.consult.web.rest;

import com.bopems.module.bridge.consult.kafka.LogSep;
import com.bopems.module.bridge.consult.service.ConsultaService;
import io.micrometer.core.annotation.Timed;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping(value = "${app.interface}", produces = "application/json")
public class ConsultaResource {

    private final LogSep LOGGER = (LogSep) LogFactory.getLog(this.getClass());

    private final ConsultaService consultaService;

    public ConsultaResource(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity consulta(@RequestBody Map<String,Object> sepObject) throws IOException {
        LOGGER.transacional("Entering consulta method");
        return consultaService.addToASepObject(sepObject);
    }

    @PostMapping("/ci")
    @Timed
    public ResponseEntity consultaKafka(@RequestBody Map<String,Object> sepObject) throws IOException {
        LOGGER.transacional("Entering consulta method for kafka");
        consultaService.addToASepObjectKafka(sepObject);
        return new ResponseEntity(HttpStatus.OK);
    }
}
