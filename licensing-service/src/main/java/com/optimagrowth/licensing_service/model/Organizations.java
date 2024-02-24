package com.optimagrowth.licensing_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@Getter
@Setter
@ToString
@RedisHash("organizations")
public class Organizations extends RepresentationModel<Organizations> {

    @Id
    String organizationId;

    String name;

    String contactName;

    String contactEmail;

    String contactPhone;

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
        return Objects.hash(super.hashCode(), organizationId, name, contactName, contactEmail, contactPhone);
    }
}