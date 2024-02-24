package com.optimagrowth.licensing_service.controller;

import com.optimagrowth.licensing_service.model.Licenses;
import com.optimagrowth.licensing_service.service.LicenseService;
import com.optimagrowth.licensing_service.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/v1/organization/{organizationId}/license")
public class LicenceController {
    private final LicenseService licenseService;
    private final Logger logger = LoggerFactory.getLogger(LicenceController.class);

    @Autowired
    public LicenceController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @GetMapping(value = "/{licenseId}/{clientType}")
    public ResponseEntity<Licenses> getLicenseWithClient(@PathVariable("organizationId") String organizationId,
                                                         @PathVariable("licenseId") String licenseId,
                                                         @PathVariable("clientType") String clientType) {

        return ResponseEntity.ok(licenseService.getLicenseWithClient(organizationId, licenseId, clientType));
    }

    @GetMapping("/")
    public List<Licenses> getLicensesByOrganizationId(@PathVariable("organizationId") String organizationId) throws TimeoutException, ExecutionException, InterruptedException {
        logger.debug("LicenseServiceController Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

        CompletableFuture<List<Licenses>> licenseByOrganization = licenseService.getLicenseByOrganization(organizationId);
        List<Licenses> licenses = new ArrayList<>();

        while (!licenseByOrganization.isDone()) {
            if (licenseByOrganization.isDone()) licenses = licenseByOrganization.get();
        }

         if (licenseByOrganization.isDone() && licenses.isEmpty()) licenses = licenseByOrganization.get();

        logger.debug("LicenseServiceController Correlation id: {}", UserContextHolder.getContext().getCorrelationId());
        return licenses;
    }

    @GetMapping(value = "/{licenseId}")
    public ResponseEntity<Licenses> getLicense(@PathVariable("organizationId") String organizationId,
                                               @PathVariable("licenseId") String licenseId) throws TimeoutException {

        Licenses license = licenseService.getLicense(licenseId, organizationId);

        license.add(linkTo(methodOn(LicenceController.class) // добавляем в ответ связанные ссылки на ресурс
                        .getLicense(organizationId, license.getLicenseId()))
                        .withRel("get license"),
                linkTo(methodOn(LicenceController.class)
                        .createLicense(license))
                        .withRel("create license"),
                linkTo(methodOn(LicenceController.class)
                        .updateLicense(license))
                        .withRel("update license"),
                linkTo(methodOn(LicenceController.class)
                        .deleteLicense(organizationId, license.getLicenseId()))
                        .withRel("delete license"));

        return ResponseEntity.ok(license);
    }

    @PostMapping("/create")
    public ResponseEntity<Licenses> createLicense(@RequestBody Licenses license) {
        return ResponseEntity.ok(licenseService.createLicense(license));
    }

    @PutMapping
    public ResponseEntity<Licenses> updateLicense(@RequestBody Licenses license) {
        return ResponseEntity.ok(licenseService.updateLicense(license));
    }


    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<String> deleteLicense(@PathVariable("organizationId") String organizationId,
                                                @PathVariable("licenseId") String licenseId) {

        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId));
    }
}
