package com.kotlin.template.customer.application.create

import com.kotlin.template.customer.application.CustomerDetails
import com.kotlin.template.customer.application.port.CustomerCreationRequests
import com.kotlin.template.customer.application.port.CustomerCreationConflict
import com.kotlin.template.customer.domain.model.CustomerEmail
import com.kotlin.template.customer.application.contract.CustomerChange
import com.kotlin.template.customer.application.port.CustomerIds
import com.kotlin.template.customer.application.port.CustomerOutbox
import com.kotlin.template.customer.domain.model.Customer
import com.kotlin.template.customer.domain.model.CustomerId
import com.kotlin.template.customer.domain.repository.CustomerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.UUID

data class CreateCustomerCommand(val ownerId: UUID, val name: String, val email: String, val requestKey: UUID)
@Service
class CreateCustomer(private val customers: CustomerRepository, private val outbox: CustomerOutbox,
    private val ids: CustomerIds, private val clock: Clock, private val requests: CustomerCreationRequests) {
    @Transactional
    fun execute(command: CreateCustomerCommand): CustomerDetails {
        val previousId = requests.reserve(command.ownerId, command.requestKey, command.name.trim(), CustomerEmail.of(command.email).value)
        if (previousId != null) {
            val previous = customers.find(CustomerId(previousId), command.ownerId) ?: throw CustomerCreationConflict()
            return CustomerDetails.from(previous)
        }
        val customer = Customer.register(CustomerId(ids.next()), command.ownerId, command.name,
            command.email, ids.next(), clock.instant().truncatedTo(ChronoUnit.MICROS))
        customers.create(customer)
        customer.events.forEach { outbox.append(CustomerChange.from(it)) }
        requests.complete(command.ownerId, command.requestKey, customer.id.value)
        return CustomerDetails.from(customer)
    }
}
