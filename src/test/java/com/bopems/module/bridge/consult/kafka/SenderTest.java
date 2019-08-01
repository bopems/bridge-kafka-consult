package com.bopems.module.bridge.consult.kafka;

import com.bopems.libraries.kafkasecurity.kafka.encryption.KafkaMessageEncryption;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SenderTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private KafkaMessageEncryption messageEncryption;

    private Sender sender;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        sender = new Sender(kafkaTemplate,messageEncryption);
        sender.topic = "testTopic;Abc";
    }

    @Test
    public void send() {

        List<Map<String,Object>> senderObj = new ArrayList<>();
        Map<String,Object> item = new HashMap<>();
        item.put("a","mock");
        senderObj.add(item);

        //when(messageEncryption.getEncryptedKey()).thenReturn("token");

        try {
            sender.send(senderObj,null);
            //verify(messageEncryption,times(1)).getEncryptedKey();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to test send object");
        }
    }

    @Test
    public void sendDLQ() {

        List<Map<String,Object>> senderObj = new ArrayList<>();
        Map<String,Object> item = new HashMap<>();
        item.put("a","mock");
        senderObj.add(item);

        try {
            sender.sendDLQ(senderObj);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to test send dlq object");
        }
    }
}