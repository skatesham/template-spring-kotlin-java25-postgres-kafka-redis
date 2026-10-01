package com.kotlin.template.notification.application.port
import com.kotlin.template.notification.application.find.NotificationDetails
import java.util.UUID
fun interface NotificationInbox { fun find(ownerId: UUID): List<NotificationDetails> }
