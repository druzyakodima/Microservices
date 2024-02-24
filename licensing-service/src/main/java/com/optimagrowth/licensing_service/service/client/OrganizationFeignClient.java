package com.optimagrowth.licensing_service.service.client;


import com.optimagrowth.licensing_service.model.Organizations;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//Демонстрация общения микросервисов без знания конкретного адреса
@FeignClient("organization-service") // Определение службы Feign. Это не хост это id службы (organization-service)
public interface OrganizationFeignClient {
    @RequestMapping(
            method= RequestMethod.GET,
            value="/v1/organization/{organizationId}",
            consumes="application/json") // Определение endpoint и операция с ней
    Organizations getOrganization(@PathVariable("organizationId") String organizationId);
}