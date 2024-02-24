package com.optimagrowth.licensing_service;

import com.optimagrowth.licensing_service.events.model.OrganizationChangeModel;
import com.optimagrowth.licensing_service.utils.UserContextInterceptor;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
//@EnableBinding(Sink.class) Связь с брокером сообщений. (Sink.class) - Использовать каналы, определяемые интерфейсом Sink, для получения входящих сообщений.
@RefreshScope // Для обновления конфигураций без перезагрузки сервера /retry
@EnableDiscoveryClient //Активирует Eureka Discovery Client
@EnableFeignClients //Чтобы включить @FeignClient
public class LicensingServiceApplication {

    private final Logger logger = LoggerFactory.getLogger(LicensingServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LicensingServiceApplication.class, args);
    }

    //Sink - предоставляет канал по умолчанию (Называется input и используется для приёма сообщений).
    //@StreamListener - сообщает фреймворку Spring Cloud Stream, что тот должен вызывать метод loggerSink() для получения сообщения из канала input
    // Этот метод выполняется каждый раз, когда во входном канале появляется входящее сообщение
//    @StreamListener(Sink.INPUT)
//    private void loggerSink(OrganizationChangeModel orgChangeModel) {
//        logger.debug("Received {} event for the organization id {}", orgChangeModel.getAction(), orgChangeModel.getOrganizationId());
//    }

    //Для перевода на другие языки 1
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);

        return localeResolver;
    }

    //Для перевода на другие языки 2
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);

        messageSource.setBasenames("messages");

        return messageSource;
    }

    @Bean
    @LoadBalanced // Получаем список всех
    public RestTemplate getRestTemplate() { // создаём RestTemplate с поддержкой LoadBalancer.
        RestTemplate template = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();

        if (interceptors.isEmpty()) {
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;          // Автоматически выполняет циклическую балансировку всех запросов между экземплярами службы.
    }

    @Bean
    public KeycloakClientRequestFactory keycloakClientRequestFactory() {
        return new KeycloakClientRequestFactory();
    }
}
