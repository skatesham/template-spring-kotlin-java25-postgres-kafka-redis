package com.kotlin.template.customer.infrastructure.persistence

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SpringDataCustomerRepository : JpaRepository<CustomerJpaEntity, UUID> {
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): CustomerJpaEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CustomerJpaEntity c where c.id = :id and c.ownerId = :ownerId")
    fun lockByIdAndOwnerId(id: UUID, ownerId: UUID): CustomerJpaEntity?
}
