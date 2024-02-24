package com.optimagrowth.gateway.filter;

import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;

@Configuration
public class ResponseFilter {

    final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    private final FilterUtils filterUtils;

    private final Tracer tracer;

    @Autowired
    public ResponseFilter(Tracer tracer, FilterUtils filterUtils) {
        this.filterUtils = filterUtils;
        this.tracer = tracer;
    }


    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {

            String traceId = tracer.currentSpan().context().traceIdString();

            logger.debug("Adding the correlation id to the outbound headers. {}", traceId);

            exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);

            logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
        }));
    }
}