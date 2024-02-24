package com.optimagrowth.licensing_service.repository;


import com.optimagrowth.licensing_service.model.Licenses;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends CrudRepository<Licenses,String> {

    List<Licenses> findByOrganizationId(String organizationId);
    Licenses findByOrganizationIdAndLicenseId(String organizationId, String licenseId);
}
