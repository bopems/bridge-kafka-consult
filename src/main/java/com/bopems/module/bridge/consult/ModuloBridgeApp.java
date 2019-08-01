package com.bopems.module.bridge.consult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@Configuration
@ComponentScan(basePackages = {"com.bopems.modulo.bridge.consulta",
        "com.bopems.libraries"})
public class ModuloBridgeApp {

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ModuloBridgeApp.class, args);
    }

}
