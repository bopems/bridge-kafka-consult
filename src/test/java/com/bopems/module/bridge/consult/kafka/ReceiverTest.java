package com.bopems.module.bridge.consult.kafka;

import com.bopems.libraries.kafkasecurity.util.JSONHelper;
import com.bopems.libraries.kafkasecurity.util.KafkaUtils;
import com.bopems.module.bridge.consult.service.ConsultaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class ReceiverTest {

    @Mock
    private ConsultaService atualizaService;

    @InjectMocks
    private Receiver receiver = new Receiver();

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void receiveSucess() throws IOException {
        doNothing().when(atualizaService).addToASepObjectKafka(any());

        Map<String, Object> sepObject = new HashMap<>();
        sepObject.put("objeto", "message");
        receiver.receive(JSONHelper.objectToString(sepObject), "anyToken", KafkaUtils.generateUUIDIfNotPresent(""));

        verify(atualizaService, Mockito.times(1)).addToASepObjectKafka(any());
    }

    @Test
    public void receiveError() throws IOException {

        try {
            List<Map<String, Object>> sepObject = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("objeto", "message");
            sepObject.add(item);

            ObjectMapper objm = new ObjectMapper();
            String message = objm.writeValueAsString(sepObject);

            receiver.receive(message, "anyToken", KafkaUtils.generateUUIDIfNotPresent(""));
            fail("An error is excpeted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}