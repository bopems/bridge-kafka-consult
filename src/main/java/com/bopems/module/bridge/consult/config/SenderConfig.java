package com.bopems.module.bridge.consult.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableKafka
@RefreshScope
public class SenderConfig {

    @Value("${kafka.host}")
    private String kafkaHost;

    @Value("${kafka.service-name}")
    private String discoveryServers;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    private DiscoveryClient discoveryClient;

    public SenderConfig(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    private Map<String, Object> producerConfigs() {

        String uri;

        Optional<ServiceInstance> serviceInstance = this.discoveryClient.getInstances(discoveryServers).stream().findFirst();

        if (kafkaHost.equals("") && serviceInstance.isPresent()) {
            uri = serviceInstance.get().getUri().toString();
        } else {
            uri = bootstrapServers;
        }

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, uri);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }

    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
