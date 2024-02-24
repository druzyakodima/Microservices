package com.optimagrowth.licensing_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "licenses")
public class Licenses extends RepresentationModel<Licenses> { // для hateoas. hateoas - это для отправки в ответе связных ссылок на ресурс

    @Id
    @Column
    private String licenseId;
    @Column
    private String description;
    @Column
    private String organizationId;
    @Column
    private String productName;
    @Column
    private String licenseType;
    @Column
    private String comment;
    @Transient
    private String organizationName;
    @Transient
    private String contactName;
    @Transient
    private String contactPhone;
    @Transient
    private String contactEmail;

    public Licenses withComment(String comment) {
        this.setComment(comment);

        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) return false;

        if (o instanceof Licenses) {
            Licenses license = (Licenses) o;
            return licenseId.equals(license.licenseId) && description.equals(license.description) && organizationId.equals(license.organizationId) && productName.equals(license.productName) && licenseType.equals(license.licenseType);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), licenseId, description, organizationId, productName, licenseType);
    }
}
