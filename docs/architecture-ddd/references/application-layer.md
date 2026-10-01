# Application Layer

A camada `application` contém os casos de uso.

Exemplos:

```text
CreateOrder
ConfirmOrder
CancelOrder
RegisterCustomer
ApprovePayment
```

Sua responsabilidade é orquestrar.

```text
input
  ↓
load aggregate
  ↓
domain behavior
  ↓
persist
  ↓
external effects/events
  ↓
result
```

Exemplo:

```kotlin
@Service
class ConfirmOrder(
    private val repository: OrderRepository
) {
    @Transactional
    fun execute(command: ConfirmOrderCommand) {
        val order = repository.findById(command.orderId)
            ?: throw OrderNotFound(command.orderId)

        order.confirm()
        repository.save(order)
    }
}
```

## Regra

A Application Layer coordena.

O Domain decide.

Regras de negócio que pertencem ao Aggregate não devem ser movidas para Use Cases apenas para simplificar entidades.
