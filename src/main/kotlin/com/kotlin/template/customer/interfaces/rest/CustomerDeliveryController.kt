package com.kotlin.template.customer.interfaces.rest

import com.kotlin.template.customer.application.publish.RecoverCustomerDelivery
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/admin/customer-delivery")
@PreAuthorize("hasRole('ADMIN')")
class CustomerDeliveryController(private val recover: RecoverCustomerDelivery) {
    @PostMapping("/{eventId}/retry")
    fun retry(@PathVariable eventId: UUID): ResponseEntity<Void> {
        recover.retry(eventId)
        return ResponseEntity.accepted().build()
    }

    @PostMapping("/{eventId}/replay")
    fun replay(@PathVariable eventId: UUID): ResponseEntity<Void> {
        recover.replay(eventId)
        return ResponseEntity.accepted().build()
    }
}
