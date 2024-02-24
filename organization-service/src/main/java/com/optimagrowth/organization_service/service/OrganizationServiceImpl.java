package com.optimagrowth.organization_service.service;

import brave.ScopedSpan;
import brave.Tracer;
import com.optimagrowth.organization_service.events.source.SimpleSourceBean;
import com.optimagrowth.organization_service.model.Organizations;
import com.optimagrowth.organization_service.repository.OrganizationRepository;
import com.optimagrowth.organization_service.utils.ActionEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final SimpleSourceBean simpleSourceBean;

    private final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final Tracer tracer;


    @Override
    public Organizations findById(String organizationId) {
        Optional<Organizations> opt = null;

        ScopedSpan newSpan = tracer.startScopedSpan("getOrgDBCall");
        try {
            opt = organizationRepository.findById(organizationId);
            simpleSourceBean.publishOrganizationChange(ActionEnum.GET, organizationId);
            if (!opt.isPresent()) {
                String message = String.format("Unable to find an organization with the Organization id %s", organizationId);
                logger.error(message);
                throw new IllegalArgumentException(message);
            }
            logger.debug("Retrieving Organization Info: " + opt.get().toString());
        }finally {
            newSpan.tag("peer.service", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }
        return opt.get();
    }

    public Organizations create(Organizations organization) {

        organization.setOrganizationId(UUID.randomUUID().toString());
        organization = organizationRepository.save(organization);
        logger.debug("Before publishOrganizationChange Save");
        simpleSourceBean.publishOrganizationChange(ActionEnum.SAVE, organization.getOrganizationId()); // Публикуем сообщение

        return organization;
    }

    @Override
    public Organizations update(Organizations organization) {
        return organizationRepository.save(organization);
    }

    @Override
    public String delete(String organizationId) {
        var foundOrganization = organizationRepository.findById(organizationId);

        if (foundOrganization.isEmpty()) {
            return "Not found " + organizationId;
        }

        organizationRepository.delete(foundOrganization.get());
        return "Successfully delete " + organizationId;
    }
}
