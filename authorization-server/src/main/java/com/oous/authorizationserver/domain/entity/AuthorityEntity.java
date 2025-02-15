package com.oous.authorizationserver.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "sso", name = "authorities")
public class AuthorityEntity {

    @Id
    @Column(name = "authority_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "authority_code", nullable = false)
    private String code;

    @Column(name = "authority_description", nullable = false)
    private String description;

    @Column(name = "system_code", nullable = false)
    private String systemCode;

    @Column(name = "active")
    private Boolean active;

}
