package com.kotlin.template.customer.application.publish

import com.kotlin.template.customer.application.port.CustomerCreationRequests
import com.kotlin.template.customer.application.port.CustomerOutbox
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
class PurgeCustomerOutbox(
    private val outbox: CustomerOutbox,
    private val clock: Clock,
    private val requests: CustomerCreationRequests
) {
    @Transactional
    fun execute() {
        outbox.purge(clock.instant().minusSeconds(30 * 86400L)); requests.purge()
    }
}
