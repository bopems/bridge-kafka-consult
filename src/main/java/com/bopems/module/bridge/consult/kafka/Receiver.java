package com.bopems.module.bridge.consult.kafka;

import com.bopems.libraries.kafkasecurity.kafka.aop.KafkaHeaderAuthorization;
import com.bopems.libraries.kafkasecurity.kafka.utils.KafkaHeaderConstants;
import com.bopems.libraries.kafkasecurity.util.SecurityUtils;
import com.bopems.module.bridge.consult.kafka.errors.KafkaRuntimeException;
import com.bopems.module.bridge.consult.service.ConsultaService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Max Jeison Prass
 *
 */

public class Receiver {

    private final LogSep log = (LogSep) LogFactory.getLog(this.getClass());

    @Autowired
    private ConsultaService consultaService;

    @KafkaListener(topics = "${kafka.topic.consumer}")
    @KafkaHeaderAuthorization
    public void receive(@Payload String message,
                        @Header(KafkaHeaderConstants.HEADER_X_TOKEN) String origin,
                        @Header(name = KafkaHeaderConstants.HEADER_MESSAGE_ID, required = false) String messageId)  throws IOException {

        message = SecurityUtils.xssSecurity(message);
        log.info("Receiving message to topic {}." + message);

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};

        try {
            Map<String, Object> sepObject = objectMapper.readValue(message, typeRef);
            consultaService.addToASepObjectKafka(sepObject);
        } catch (Exception e) {
            log.error("Could not unserialize message " + SecurityUtils.xssSecurity(message), e);
            throw new KafkaRuntimeException(e.getMessage());
        }
    }
}
