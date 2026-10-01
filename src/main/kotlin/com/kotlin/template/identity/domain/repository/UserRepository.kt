package com.kotlin.template.identity.domain.repository

import com.kotlin.template.identity.domain.model.User
import java.util.UUID

interface UserRepository {
    fun findByEmail(email: String): User?
    fun findById(id: UUID): User?
    fun create(user: User): User
}
