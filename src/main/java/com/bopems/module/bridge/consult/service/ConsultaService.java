package com.bopems.module.bridge.consult.service;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Max Jeison Prass
 *
 */
public interface ConsultaService {

    ResponseEntity addToASepObject(Map<String, Object> sepObject) throws IOException;

    void addToASepObjectKafka(Map<String, Object> sepObject) throws IOException;

    void addToASepObjectKafka(Map<String, Object> sepObject, String messageId) throws IOException;

}
