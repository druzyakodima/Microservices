package com.optimagrowth.organization_service.repository;

import com.optimagrowth.organization_service.model.Organizations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends CrudRepository<Organizations, String> {
    Optional<Organizations> findById(String id);
}
