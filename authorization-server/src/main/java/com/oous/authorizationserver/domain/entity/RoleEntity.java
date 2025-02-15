package com.oous.authorizationserver.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "sso", name = "roles")
public class RoleEntity {

    @Id
    @Column(name = "role_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "role_code", nullable = false)
    private String code;

    @Column(name = "role_description", nullable = false)
    private String description;

    @Column(name = "system_code", nullable = false)
    private String systemCode;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(schema = "sso", name = "role_authorities",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<AuthorityEntity> authorities = new ArrayList<>();
}
