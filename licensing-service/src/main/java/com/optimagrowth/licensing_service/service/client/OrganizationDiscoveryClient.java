package com.optimagrowth.licensing_service.service.client;

import com.optimagrowth.licensing_service.model.Organizations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

//Демонстрация общения микросервисов без знания конкретного адреса
@Component
public class OrganizationDiscoveryClient {

    @Autowired
    private DiscoveryClient discoveryClient;

    public Organizations getOrganization(String organizationId) {
        RestTemplate restTemplate = new RestTemplate();
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service"); // Получает список всех экземпляров службы организации - это недостаток по сравнению с другими клиентами

        if (instances.isEmpty()) return null;
        String serviceUri = String.format("%s/v1/organization/%s", instances.get(0).getUri().toString(), organizationId);

        ResponseEntity<Organizations> restExchange =
                restTemplate.exchange(
                        serviceUri,
                        HttpMethod.GET,
                        null, Organizations.class, organizationId);

        return restExchange.getBody();
    }
}