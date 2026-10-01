package com.kotlin.template.customer.interfaces.rest

import com.kotlin.template.customer.application.CustomerDetails
import com.kotlin.template.customer.application.create.CreateCustomer
import com.kotlin.template.customer.application.create.CreateCustomerCommand
import com.kotlin.template.customer.application.delete.DeleteCustomer
import com.kotlin.template.customer.application.delete.DeleteCustomerCommand
import com.kotlin.template.customer.application.find.FindCustomer
import com.kotlin.template.customer.application.find.FindCustomerQuery
import com.kotlin.template.customer.application.find.ListCustomers
import com.kotlin.template.customer.application.find.ListCustomersQuery
import com.kotlin.template.customer.application.update.UpdateCustomer
import com.kotlin.template.customer.application.update.UpdateCustomerCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.Instant
import java.util.*

data class CreateCustomerRequest(
    @field:NotBlank @field:Size(max = 100) val name: String,
    @field:NotBlank @field:Email @field:Size(max = 254) val email: String,
)

data class UpdateCustomerRequest(
    @field:NotBlank @field:Size(max = 100) val name: String,
    @field:NotBlank @field:Email @field:Size(max = 254) val email: String,
    @field:Positive val revision: Long,
)

data class CustomerResponse(
    val id: UUID, val name: String, val email: String, val revision: Long,
    val createdAt: Instant, val updatedAt: Instant
) {
    companion object {
        fun from(details: CustomerDetails) = CustomerResponse(
            details.id, details.name,
            details.email, details.revision, details.createdAt, details.updatedAt
        )
    }
}

@RestController
@RequestMapping("/api/customers")
class CustomerController(
    private val create: CreateCustomer, private val find: FindCustomer,
    private val update: UpdateCustomer, private val delete: DeleteCustomer, private val list: ListCustomers
) {
    @PostMapping
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestHeader("Idempotency-Key") requestKey: UUID,
        @Valid @RequestBody request: CreateCustomerRequest
    ): ResponseEntity<CustomerResponse> {
        val result =
            create.execute(CreateCustomerCommand(UUID.fromString(jwt.subject), request.name, request.email, requestKey))
        return ResponseEntity.created(URI.create("/api/customers/${result.id}")).body(CustomerResponse.from(result))
    }

    @GetMapping
    fun list(
        @AuthenticationPrincipal jwt: Jwt, @RequestParam(required = false) after: UUID?,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) limit: Int
    ) =
        list.execute(ListCustomersQuery(UUID.fromString(jwt.subject), after, limit)).map(CustomerResponse::from)

    @GetMapping("/{id}")
    fun find(@AuthenticationPrincipal jwt: Jwt, @PathVariable id: UUID) =
        CustomerResponse.from(find.execute(FindCustomerQuery(id, UUID.fromString(jwt.subject))))

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateCustomerRequest
    ) =
        CustomerResponse.from(
            update.execute(
                UpdateCustomerCommand(
                    id,
                    UUID.fromString(jwt.subject),
                    request.name,
                    request.email,
                    request.revision
                )
            )
        )

    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID,
        @RequestParam @Positive revision: Long
    ): ResponseEntity<Void> {
        delete.execute(DeleteCustomerCommand(id, UUID.fromString(jwt.subject), revision))
        return ResponseEntity.noContent().build()
    }
}
