package com.optimagrowth.organization_service.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "organizations")
public class Organizations {

    @Id
    private String organizationId;
    @Column
    private String name;
    @Column
    private String contactName;
    @Column
    private String contactEmail;
    @Column
    private String contactPhone;

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;

        if (o instanceof Organizations) {
            Organizations that = (Organizations) o;
            return Objects.equals(organizationId, that.organizationId) && Objects.equals(name, that.name) && Objects.equals(contactName, that.contactName) && Objects.equals(contactEmail, that.contactEmail) && Objects.equals(contactPhone, that.contactPhone);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationId, name, contactName, contactEmail, contactPhone);
    }
}
