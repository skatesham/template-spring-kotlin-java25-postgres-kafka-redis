package com.kotlin.template.customer.interfaces.scheduler

import com.kotlin.template.audit.application.record.RecordCustomerAudit
import com.kotlin.template.customer.application.delete.DeleteExpiredCustomers
import com.kotlin.template.customer.application.publish.PublishCustomerOutbox
import com.kotlin.template.customer.application.publish.PurgeCustomerOutbox
import com.kotlin.template.notification.application.record.NotifyCustomerChange
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Configuration(proxyBeanMethods = false)
@EnableScheduling
class CustomerSchedulingConfiguration

@Component
@ConditionalOnProperty(name = ["app.customer.jobs.enabled"], havingValue = "true", matchIfMissing = true)
class CustomerJobs(
    private val publish: PublishCustomerOutbox, private val purge: PurgeCustomerOutbox,
    private val expired: DeleteExpiredCustomers, private val audit: RecordCustomerAudit,
    private val notifications: NotifyCustomerChange, private val meters: MeterRegistry
) {
    @Scheduled(fixedDelayString = "\${app.customer.outbox.poll-ms:500}")
    fun publish() {
        repeat(20) { if (!publish.execute()) return }
    }

    @Scheduled(
        fixedDelayString = "\${app.customer.retention.poll-ms:3600000}",
        initialDelayString = "\${app.customer.retention.poll-ms:3600000}"
    )
    fun retention() {
        expired.execute(); purge.execute(); audit.purge(); notifications.purge()
        meters.counter("customer.retention.runs").increment()
    }
}
