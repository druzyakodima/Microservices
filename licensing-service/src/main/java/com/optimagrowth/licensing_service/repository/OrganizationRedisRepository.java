package com.optimagrowth.licensing_service.repository;

import com.optimagrowth.licensing_service.model.Organizations;
import org.springframework.data.repository.CrudRepository;

public interface OrganizationRedisRepository extends CrudRepository<Organizations, String> {

}
