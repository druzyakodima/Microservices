package com.optimagrowth.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@RefreshScope
@EnableEurekaClient
@SpringBootApplication
public class ApiGatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayServerApplication.class, args);
    }

}
