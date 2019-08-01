package com.bopems.module.bridge.consult.service;

import com.bopems.module.bridge.consult.kafka.Sender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.Serializable;
import java.util.*;

public class ConsultaServiceImplTest {

    @Mock
    private Sender sender;

    private ConsultaServiceImpl consultaService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        consultaService = new ConsultaServiceImpl(sender);
    }

    @Test
    public void testConsultaService(){
    }

    @Test
    public void testConsultaSetService(){ }

    @Test
    public void addToASepObject() throws Exception{
    }

    @Test
    public void testSetHeadersFromGet()
    {
        Map<String, Object> obj = new HashMap<String, Object>();
        Map<String, Object> headers = new HashMap<String, Object>();

        headers.put("Content-Type", "application/json");
        obj.put("Header", headers);
        HttpHeaders header = this.consultaService.setHeadersFromGet(obj);
        Assert.assertEquals(MediaType.APPLICATION_JSON, header.getContentType());

        obj.clear();
        header.clear();

        obj.put("Body", headers);
        header = this.consultaService.setHeadersFromGet(obj);
        Assert.assertEquals(0, header.size());
    }

    @Test
    public void testSetParametersFromGet()
    {
        Map<String, Object> obj = new HashMap<String, Object>();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("prop1", "1");
        params.put("prop2", "2");
        obj.put("Payload", params);

        Map<String, Object> params2 = this.consultaService.setParametersFromGet(obj);

        Assert.assertEquals(params, params2);
    }

    private class MockObject implements Serializable{

        long id;

        MockObject(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}
