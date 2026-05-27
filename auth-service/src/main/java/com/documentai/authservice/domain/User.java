package com.documentai.authservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", schema = "auth_schema")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

}
