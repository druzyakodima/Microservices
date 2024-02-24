package com.optimagrowth.gateway.filter;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    private final FilterUtils filterUtils;

    @Autowired
    public TrackingFilter(FilterUtils filterUtils) {
        this.filterUtils = filterUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) { // Метод, который выполняется каждый раз когда запрос проходит через фильтр

        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        boolean isCorrelationIdPresent = filterUtils.getCorrelationId(requestHeaders) != null;

        if (isCorrelationIdPresent) {
            logger.debug("tmx-correlation-id found in tracking filter: {}. ",
                    filterUtils.getCorrelationId(requestHeaders));
        } else {
            String correlationID = UUID.randomUUID().toString();
            exchange = filterUtils.setCorrelationId(exchange, correlationID);
            logger.debug("tmx-correlation-id generated in tracking filter: {}.", correlationID);
        }

        System.out.println("The authentication name from the token is : " + getUsername(requestHeaders));

        return chain.filter(exchange);
    }

    private String getUsername(HttpHeaders requestHeaders) {
        String username = "";
        if (filterUtils.getAuthToken(requestHeaders) != null) {
            String authToken = filterUtils.getAuthToken(requestHeaders).replace("Bearer ", ""); // Анализирует токен из HTTP-заголовка Authorization
            JSONObject jsonObj = decodeJWT(authToken);
            try {
                username = jsonObj.getString("preferred_username"); // Извлекаем preferred_username из JWT
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }
        return username;
    }

    private JSONObject decodeJWT(String JWTToken) {
        System.out.println("JWTToken: " + JWTToken);
        String[] split_string = JWTToken.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody)); // Преобразуем тело JWT в объект JSON, чтобы получить preferred_username
        return new JSONObject(body);
    }
}
