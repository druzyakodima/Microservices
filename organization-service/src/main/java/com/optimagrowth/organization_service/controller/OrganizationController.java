package com.optimagrowth.organization_service.controller;

import com.optimagrowth.organization_service.model.Organizations;
import com.optimagrowth.organization_service.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/organization")
public class OrganizationController {
    private final OrganizationService organizationService;

    @RolesAllowed("ROLE_ADMIN")
    @GetMapping(value = "/{organizationId}")
    public ResponseEntity<Organizations> getOrganization(@PathVariable("organizationId") String organizationId) {
        return ResponseEntity.ok(organizationService.findById(organizationId));
    }

    @RolesAllowed({ "ROLE_ADMIN", "ROLE_USER" })
    @PutMapping(value = "/{organizationId}")
    public ResponseEntity<Organizations> updateOrganization(@PathVariable("organizationId") String organizationId, @RequestBody Organizations organization) {
        return ResponseEntity.ok(organizationService.update(organization));
    }

    @RolesAllowed({ "ROLE_ADMIN", "ROLE_USER" })
    @PostMapping(value = "/create")
    public ResponseEntity<Organizations> saveOrganization(@RequestBody Organizations organization) {
        return ResponseEntity.ok(organizationService.create(organization));
    }

    @RolesAllowed("ROLE_ADMIN")
    @DeleteMapping(value = "/{organizationId}")
    public ResponseEntity<String> deleteOrganization(@PathVariable("organizationId") String organizationId) {
        return ResponseEntity.ok(organizationService.delete(organizationId));
    }
}
