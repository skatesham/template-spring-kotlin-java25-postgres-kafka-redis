package com.kotlin.template.notification.interfaces.rest

import com.kotlin.template.notification.application.find.FindNotifications
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*

data class NotificationResponse(
    val eventId: UUID,
    val customerId: UUID,
    val revision: Long,
    val type: String,
    val occurredAt: Instant
)

@RestController
class NotificationController(private val find: FindNotifications) {
    @GetMapping("/api/notifications")
    fun find(@AuthenticationPrincipal jwt: Jwt) = find.execute(UUID.fromString(jwt.subject)).map {
        NotificationResponse(it.eventId, it.customerId, it.revision, it.type, it.occurredAt)
    }
}
