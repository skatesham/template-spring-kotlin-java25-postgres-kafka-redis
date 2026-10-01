package com.kotlin.template.customer.application.find

import com.kotlin.template.customer.application.CustomerDetails
import com.kotlin.template.customer.application.CustomerNotFound
import com.kotlin.template.customer.application.port.CustomerCache
import com.kotlin.template.customer.domain.model.CustomerId
import com.kotlin.template.customer.domain.repository.CustomerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

data class FindCustomerQuery(val id: UUID, val ownerId: UUID)

@Service
class FindCustomer(private val customers: CustomerRepository, private val cache: CustomerCache) {
    // PostgreSQL checks ownership and revision even on a cache hit. This deliberately trades
    // one small indexed query for consistency and immediate revocation/deletion.
    @Transactional
    fun execute(query: FindCustomerQuery): CustomerDetails {
        val revision = customers.lockRevision(CustomerId(query.id), query.ownerId) ?: throw CustomerNotFound()
        cache.get(query.id, revision)?.let { return it }
        val details =
            CustomerDetails.from(customers.find(CustomerId(query.id), query.ownerId) ?: throw CustomerNotFound())
        cache.put(details)
        return details
    }
}
