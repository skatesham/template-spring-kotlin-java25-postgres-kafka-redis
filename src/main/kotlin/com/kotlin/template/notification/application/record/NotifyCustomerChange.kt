package com.kotlin.template.notification.application.record

import com.kotlin.template.notification.application.port.CustomerNotifications
import com.kotlin.template.customer.application.contract.CustomerChange
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotifyCustomerChange(private val records: CustomerNotifications, private val meters: MeterRegistry) {
    @Transactional
    fun execute(change: CustomerChange) {
        change.validate()
        val created = records.record(change)
        meters.counter("customer.consumer.records", "consumer", "notification",
            "result", if (created) "created" else "duplicate").increment()
    }
    @Transactional fun purge() { records.purge() }
}
