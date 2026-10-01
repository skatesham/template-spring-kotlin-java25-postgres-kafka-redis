# Empacotamento

## Estrutura recomendada

```text
order/
├── domain/
│   ├── model/
│   ├── repository/
│   ├── event/
│   ├── service/
│   ├── specification/
│   └── exception/
│
├── application/
│   ├── create/
│   ├── confirm/
│   ├── cancel/
│   └── find/
│
├── infrastructure/
│   ├── persistence/
│   ├── messaging/
│   ├── cache/
│   └── client/
│
└── interfaces/
    └── rest/
```

## Package by Feature

A unidade principal de organização deve ser a funcionalidade/bounded context.

```text
order
payment
customer
catalog
```

Não:

```text
controllers
services
repositories
```

## Vertical Slice

Dentro de `application`, agrupar por caso de uso:

```text
application/
├── create/
│   ├── CreateOrderCommand.kt
│   ├── CreateOrder.kt
│   └── CreateOrderResult.kt
├── confirm/
└── find/
```

## Modular Monolith

Cada bounded context deve ser tratado como um módulo lógico independente.

A separação inicial é por packages. Se necessário, poderá evoluir para módulos Gradle ou serviços independentes.
