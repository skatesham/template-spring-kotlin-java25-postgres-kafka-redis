package com.kotlin.template.customer.application.publish

import com.kotlin.template.customer.application.CustomerNotFound
import com.kotlin.template.customer.application.port.CustomerOutbox
import com.kotlin.template.customer.application.port.CustomerEventPublisher
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RecoverCustomerDelivery(private val outbox: CustomerOutbox, private val publisher: CustomerEventPublisher,
    private val meters: MeterRegistry) {
    @Transactional fun retry(eventId: UUID) {
        if (!outbox.retryFailed(eventId)) throw CustomerNotFound()
        meters.counter("customer.outbox.recoveries", "action", "retry").increment()
    }
    @Transactional(readOnly = true) fun replay(eventId: UUID) {
        val change = outbox.findPublished(eventId) ?: throw CustomerNotFound()
        change.validate()
        publisher.publish(change)
        meters.counter("customer.outbox.recoveries", "action", "replay").increment()
    }
}
