package com.optimagrowth.organization_service.service;


import com.optimagrowth.organization_service.model.Organizations;

public interface OrganizationService {

    Organizations findById(String organizationId);
    Organizations create(Organizations organization);
    Organizations update(Organizations organization);
    String delete(String organizationId);
}
