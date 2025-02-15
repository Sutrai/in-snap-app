package com.oous.authorizationserver.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Data
@Entity
@Table(name = "users")
@Setter
public class UserEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "admin", nullable = false)
    private Boolean admin;

    @Column(name = "superuser", nullable = false)
    private Boolean superuser;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    public List<RoleEntity> roles = new ArrayList<>();
}
