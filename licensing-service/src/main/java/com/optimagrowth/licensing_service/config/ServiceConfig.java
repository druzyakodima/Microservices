package com.optimagrowth.licensing_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@Setter
@ConfigurationProperties(prefix = "example")
public class ServiceConfig {

    private String property;

    @Value("${redis.server}")
    private String redisServer;

    @Value("${redis.port}")
    private String redisPort;
}
