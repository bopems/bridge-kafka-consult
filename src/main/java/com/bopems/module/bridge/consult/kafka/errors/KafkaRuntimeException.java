package com.bopems.module.bridge.consult.kafka.errors;

public class KafkaRuntimeException extends RuntimeException{

    public KafkaRuntimeException(String message) {
        super(message);
    }
}
