package com.labs.testeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class TestEurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestEurekaServerApplication.class, args);
    }
}
