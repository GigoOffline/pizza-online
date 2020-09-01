package de.stealth.personservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"keycloak"}, name = "person_unique_keycloak_index")})
public class Person extends AbstractBaseEntity {

    private String firstName;

    private String lastName;

    @Email
    private String email;

    @NotBlank
    private String keycloak;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    private Address address;

}
