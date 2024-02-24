package com.optimagrowth.licensing_service.service;


import com.optimagrowth.licensing_service.model.Licenses;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public interface LicenseService {
    Licenses getLicense(String licenseId, String organizationId) throws TimeoutException;
    Licenses createLicense(Licenses license);
    Licenses updateLicense(Licenses license);
    String deleteLicense(String licenseId, String organizationId);
    Licenses getLicenseWithClient(String organizationId, String licenseId, String clientType);
    CompletableFuture<List<Licenses>> getLicenseByOrganization(String organizationId) throws TimeoutException;
}
