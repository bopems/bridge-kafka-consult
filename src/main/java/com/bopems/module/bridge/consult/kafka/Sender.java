package com.bopems.module.bridge.consult.kafka;

import com.bopems.libraries.kafkasecurity.kafka.EncryptedSender;
import com.bopems.libraries.kafkasecurity.kafka.encryption.KafkaMessageEncryption;
import com.bopems.libraries.kafkasecurity.kafka.utils.KafkaHeaderConstants;
import com.bopems.libraries.kafkasecurity.util.JSONHelper;
import com.bopems.libraries.kafkasecurity.util.KafkaUtils;
import com.bopems.libraries.kafkasecurity.util.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Max Jeison Prass
 */
@Component
@RefreshScope
public class Sender implements EncryptedSender {

    private final LogSep log = (LogSep) LogFactory.getLog(this.getClass());

    @Value("${kafka.topic.producer}")
    protected String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaMessageEncryption messageEncryption;

    public Sender(KafkaTemplate<String, String> kafkaTemplate, KafkaMessageEncryption messageEncryption) {
        this.kafkaTemplate = kafkaTemplate;
        this.messageEncryption = messageEncryption;
    }

    public void send(List<Map<String, Object>> sepBody, String messageId) throws IOException {

        List<String> topics = Arrays.asList(Strings.split(topic, ';'));

        log.info(new StringBuilder("Sending message to topic(s) ").append(SecurityUtils.xssSecurity(topic)));

        String sharedMessageId = KafkaUtils.generateUUIDIfNotPresent(messageId);

        topics.forEach(currentTopic -> {
            String convertedMessage = null;
            try {
                ObjectMapper objm = new ObjectMapper();
                convertedMessage = objm.writeValueAsString(sepBody);
                Message<String> message = MessageBuilder
                        .withPayload(convertedMessage)
                        .setHeader(KafkaHeaders.TOPIC, currentTopic)
                        .setHeader(KafkaHeaderConstants.HEADER_X_TOKEN, messageEncryption.getEncryptedKey())
                        .setHeader(KafkaHeaderConstants.HEADER_MESSAGE_ID, sharedMessageId)
                        .build();
                kafkaTemplate.send(message);
            } catch (JsonProcessingException e) {
                log.error("Failed to convert object " + convertedMessage, e);
            }
        });


    }

    @Override
    public void sendDLQ(Object sepBody) {
        String dlqTopic = topic + "DLQ";
        log.info("Sending message to DLQ topic");

        try {
            String convertedMessage = SecurityUtils.xssSecurity(JSONHelper.objectToString(sepBody));
            Message<String> message = MessageBuilder
                    .withPayload(convertedMessage)
                    .setHeader(KafkaHeaders.TOPIC, dlqTopic)
                    .build();
            kafkaTemplate.send(message);
        } catch (IOException e) {
            log.error("Failed to convert object {}", e);
        }
    }
}
