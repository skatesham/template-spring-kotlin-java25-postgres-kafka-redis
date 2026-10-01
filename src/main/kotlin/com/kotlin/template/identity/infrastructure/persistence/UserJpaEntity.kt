package com.kotlin.template.identity.infrastructure.persistence

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id val id: UUID,
    @Column(nullable = false, length = 100) val name: String,
    @Column(nullable = false, length = 254) val email: String,
    @Column(name = "password_hash", nullable = false, length = 255) val passwordHash: String,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_name")])
    val roles: Set<RoleJpaEntity>,
)

@Entity
@Table(name = "roles")
class RoleJpaEntity(@Id @Column(length = 32) val name: String)
