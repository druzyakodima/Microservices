package com.optimagrowth.licensing_service.service.client;

import brave.ScopedSpan;
import brave.Tracer;
import com.optimagrowth.licensing_service.model.Organizations;
import com.optimagrowth.licensing_service.repository.OrganizationRedisRepository;
import com.optimagrowth.licensing_service.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import zipkin2.internal.Trace;

//Демонстрация общения микросервисов без знания конкретного адреса
@Component
@RequiredArgsConstructor
public class OrganizationRestTemplateClient {

    private final KeycloakRestTemplate restTemplate;

    private final OrganizationRedisRepository organizationRedisRepository;

    private final Tracer tracer;

    private final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    public Organizations getOrganization(String organizationId) {
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

        Organizations organization = checkRedisCache(organizationId); // если null, то мы запрашиваем организацию у службы организаций

        if (organization != null) {
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}", organizationId, organization);
            return organization;
        }

        logger.debug("Unable to locate organization from the redis cache: {}.", organizationId);

        ResponseEntity<Organizations> restExchange = getOrganizationFromService(organizationId);

        /*Save the record from cache*/
        organization = restExchange.getBody();

        if (organization != null) {
            cacheOrganizationObject(organization);
        }

        return restExchange.getBody();
    }

    public Organizations checkRedisCache(String organizationId) {
        ScopedSpan newSpan = tracer.startScopedSpan("readLicensingDataFromRedis");
        try {
            return organizationRedisRepository.findById(organizationId).orElse(null);
        } catch (Exception ex) {
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache.  Exception {}", organizationId, ex.getMessage());
            return null;
        } finally {
            newSpan.tag("peer.service", "redis");
            newSpan.annotate("Client received");
            newSpan.finish();
        }
    }

    public ResponseEntity<Organizations> getOrganizationFromService(String organizationId) {

        return restTemplate.exchange(
                "http://gateway-server:8072/organization-service/v1/organization/{organizationId}", // Это не хост это id службы (organization-service). При использовании RestTemplate с поддержкой Load Balancer создает целевой URL c идентификатором искомой службы
                HttpMethod.GET,
                null, Organizations.class, organizationId);
    }

    private void cacheOrganizationObject(Organizations organization) {
        try {
            organizationRedisRepository.save(organization);
        } catch (Exception ex) {
            logger.error("Unable to cache organization {} in Redis. Exception {}", organization.getOrganizationId(), ex.getMessage());
        }
    }
}