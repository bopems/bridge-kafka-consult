package com.bopems.module.bridge.consult.config;

import com.bopems.module.bridge.consult.kafka.LogSep;
import com.bopems.module.bridge.consult.kafka.Receiver;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableKafka
public class ReceiverConfig {

    private final LogSep log = (LogSep) LogFactory.getLog(this.getClass());

    @Value("${spring.kafka.consumer.group-id}")
    private String groupIdKafka;

    @Value("${kafka.host}")
    private String kafkaHost;

    @Value("${kafka.service-name}")
    private String discoveryServers;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    private DiscoveryClient discoveryClient;

    public ReceiverConfig(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    private Map<String, Object> consumerConfigs() {

        String uri;

        Optional<ServiceInstance> serviceInstance = discoveryClient.getInstances(discoveryServers).stream().findFirst();
        if (kafkaHost.equals("") && serviceInstance.isPresent()) {
            uri = serviceInstance.get().getUri().toString();
        }
        else{
            uri = bootstrapServers;
        }

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, uri);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdKafka);

        return props;
    }

    private ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
                new StringDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setErrorHandler((e, consumerRecord) -> log.error(new StringBuilder().append("Ocorreu erro Consumer Listener. ").append(e.getMessage())));
        return factory;
    }

    @Bean
    public Receiver receiver(){
        return new Receiver();
    }
}
