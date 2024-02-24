package com.optimagrowth.licensing_service.service;


import com.optimagrowth.licensing_service.config.ServiceConfig;
import com.optimagrowth.licensing_service.model.Licenses;
import com.optimagrowth.licensing_service.model.Organizations;
import com.optimagrowth.licensing_service.repository.LicenseRepository;
import com.optimagrowth.licensing_service.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.licensing_service.service.client.OrganizationFeignClient;
import com.optimagrowth.licensing_service.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {

    private final MessageSource messages;

    private final LicenseRepository licenseRepository;

    private final ServiceConfig serviceConfig;

    private final OrganizationFeignClient organizationFeignClient;

    private final OrganizationDiscoveryClient organizationDiscoveryClient;

    private final OrganizationRestTemplateClient organizationRestTemplateClient;
    private final Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);

    @Override
    // Реализация шаблона "Размыкатель цепи"(name = licenseService) смотри bootstrap.yml
    @CircuitBreaker(name = "licenseService")
    public Licenses getLicense(String licenseId, String organizationId) throws TimeoutException {
        sleep();

        Licenses license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        if (license == null) {
            throw new IllegalArgumentException(String.format(
                    messages.getMessage("license.search.error.message", null, null), licenseId, organizationId)
            );
        }

        return license.withComment(serviceConfig.getProperty());
    }

    // Реализация шаблона "Размыкатель цепи"(name = licenseService) смотри bootstrap.yml и "Использования резервной реализации"( fallbackMethod = buildFallbackLicenseList)
    //@Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "buildFallbackLicenseList") //Шаблон "Герметичные отсеки" Если реализовывать изоляцию с использованием семафоров. Она работает по умолчанию
    @Bulkhead(name = "bulkheadLicenseService", type = Type.THREADPOOL, fallbackMethod = "buildFallbackLicenseList") // Реализация изоляции с использованием пулов потоков, необходима синхронизация потому что UserContextHolder.getContext().getCorrelationId() возвращает null из-за множества потоков. Метод должен возвращать CompletableFuture<List<Licenses>>
    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")// Реализация шаблона "Ограничитель частоты"
    @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList") // Реализация шаблона "Повторных попыток"
    public CompletableFuture<List<Licenses>> getLicenseByOrganization(String organizationId) throws TimeoutException {

       /* logger.debug("getLicensesByOrganization Correlation id: {}",
                UserContextHolder.getContext().getCorrelationId());*/
        //sleep();

        List<Licenses> byOrganizationId = licenseRepository.findByOrganizationId(organizationId);

        logger.debug("After sleep");

        return CompletableFuture.completedFuture(byOrganizationId);
    }

    private void sleep() throws TimeoutException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.debug(e.getMessage());
        }
    }


    //Метод резервной реализации должен быть в том же классе и иметь ту же сигнатуру, что и исходный метод, плюс один доп. параметр для передачи целевого исключения.
    // Тоже можно использовать  @CircuitBreaker(name = "idService", fallbackMethod = " имя метода резервной реализации")
    @SuppressWarnings("unused")
    private CompletableFuture<List<Licenses>> buildFallbackLicenseList(String organizationId, Throwable t) {
        logger.debug(t.getMessage());
        List<Licenses> fallbackList = new ArrayList<>();
        Licenses license = new Licenses();

        license.setLicenseId("000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available");

        fallbackList.add(license);

        return CompletableFuture.completedFuture(fallbackList);
    }

    @Override
    @CircuitBreaker(name = "licenseServiceImpl")
    public Licenses createLicense(Licenses license) {
        Licenses savedLicense = licenseRepository.save(license);
        return savedLicense.withComment(serviceConfig.getProperty());
    }

    @Override
    @CircuitBreaker(name = "licenseServiceImpl")
    public Licenses updateLicense(Licenses license) {
        Licenses savedLicense = licenseRepository.save(license);
        return savedLicense.withComment(serviceConfig.getProperty());
    }

    @Override
    @CircuitBreaker(name = "licenseServiceImpl")
    public String deleteLicense(String licenseId, String organizationId) {
        String responseMessage;

        Licenses license = new Licenses();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);

        responseMessage = String.format(
                messages.getMessage("license.delete.message", null, null), licenseId, organizationId
        );

        return responseMessage;
    }

    @Override
    public Licenses getLicenseWithClient(String organizationId, String licenseId, String clientType) {

        var license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        if (license == null) {
            throw new IllegalArgumentException(
                    String.format(
                            messages.getMessage("license.search.error.message", null, null), licenseId, organizationId
                    )
            );
        }

        Organizations organization = retrieveOrganizationInfo(organizationId, clientType);

        if (organization != null) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(serviceConfig.getProperty());
    }

    private Organizations retrieveOrganizationInfo(String organizationId, String clientType) { // Служба лицензий вызывает службу организаций не зная, где находиться служба организаций через механизм обнаружения служб

        Organizations organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using rest client");
                organization = organizationRestTemplateClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }


}
