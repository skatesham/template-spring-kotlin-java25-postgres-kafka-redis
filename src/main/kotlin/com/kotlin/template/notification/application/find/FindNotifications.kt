package com.kotlin.template.notification.application.find

import com.kotlin.template.notification.application.port.NotificationInbox
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

data class NotificationDetails(val eventId: UUID, val customerId: UUID, val revision: Long, val type: String, val occurredAt: Instant)
@Service
class FindNotifications(private val inbox: NotificationInbox) {
    @Transactional(readOnly = true) fun execute(ownerId: UUID) = inbox.find(ownerId)
}
