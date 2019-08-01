package com.bopems.module.bridge.consult.service;

import com.bopems.libraries.kafkasecurity.util.SecurityUtils;
import com.bopems.module.bridge.consult.kafka.LogSep;
import com.bopems.module.bridge.consult.kafka.Sender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Max Jeison Prass
 */
@Service
@RefreshScope
public class ConsultaServiceImpl implements ConsultaService {

    private final LogSep LOGGER = (LogSep) LogFactory.getLog(this.getClass());

    @Value("${app.api.host}")
    private String url;

    @Value("${app.api.method}")
    private String method;

    @Value("${app.api.timeout}")
    private String timeout;

    private final Sender sender;

    public ConsultaServiceImpl(Sender sender) {
        this.sender = sender;
    }

    public ResponseEntity addToASepObject(Map<String, Object> input) throws IOException {

        LOGGER.transacional("Entered addToASepObject method");

        Map<String, Object> sepObject = SecurityUtils.xssSecurity(input);

        HttpHeaders headers = this.setHeadersFromGet(sepObject);
        Map<String, Object> params = (Map<String, Object>) sepObject.get("Body");

        RestTemplate api = new RestTemplate();
        if (method.equals("get")) {
            LOGGER.info("Executing get request");

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

            for (Map.Entry<String, Object> entry : params.entrySet()) {

                builder.queryParam(entry.getKey(), entry.getValue());
            }

            HttpEntity entity = new HttpEntity(headers);

            URI uri = builder.buildAndExpand(params).toUri();
            int time = Integer.parseInt(timeout);
            this.setTimeout(api, time);
            HttpEntity<String> response = api.exchange(uri, HttpMethod.GET, entity, String.class);

            return new ResponseEntity(SecurityUtils.xssSecurity(response.getBody()), HttpStatus.OK);
        } else {
            LOGGER.info("Executing post request");

            HttpEntity entity = new HttpEntity<>(params, headers);
            ResponseEntity response = api.postForEntity(url, entity, Map.class);

            return new ResponseEntity(SecurityUtils.xssSecurity((Map<String, Object>) response.getBody()), HttpStatus.OK);
        }
    }

    @Override
    public void addToASepObjectKafka(Map<String, Object> sepObject) throws IOException {
        addToASepObjectKafka(sepObject, null);
    }

    @Override
    public void addToASepObjectKafka(Map<String, Object> sepObject, String messageId) throws IOException {
        LOGGER.info("Sending object to kafka");

        String body = (String) addToASepObject(sepObject).getBody();

        List<Map<String, Object>> data = new ObjectMapper().readValue(body, new TypeReference<List<Map<String, Object>>>() {
        });
        sender.send(data, messageId);
    }

    public HttpHeaders setHeadersFromGet(Map<String, Object> sepObject) {
        LOGGER.info("Setting object header");

        HttpHeaders header = new HttpHeaders();
        Map<String, Object> headers = (Map<String, Object>) sepObject.get("Header");

        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                if (entry.getKey().equals("Content-Type")) {
                    try {
                        header.setContentType(MediaType.parseMediaType((String) entry.getValue()));
                    } catch (Exception e) {
                        header.add(entry.getKey(), (String) entry.getValue());
                    }
                } else {
                    header.add(entry.getKey(), (String) entry.getValue());
                }
            }
        }

        return header;
    }

    public Map<String, Object> setParametersFromGet(Map<String, Object> sepObject) {
        LOGGER.info("Setting parameters for get");

        Map<String, Object> params = new HashMap<>();

        Map<String, Object> payload = (Map<String, Object>) sepObject.get("Payload");

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }

        return params;
    }

    public void setTimeout(RestTemplate restTemplate, int timeout) {

        LOGGER.info(String.format("Setting %d timeout", timeout));

        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                .getRequestFactory();
        rf.setReadTimeout(timeout);
        rf.setConnectTimeout(timeout);
    }
}
