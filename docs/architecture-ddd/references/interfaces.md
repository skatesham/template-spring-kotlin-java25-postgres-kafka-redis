# Interfaces

## REST

Controllers são adapters de entrada.

```text
HTTP
 ↓
Controller
 ↓
Command / Query
 ↓
Application
 ↓
Domain
```

Controller deve:

- validar formato;
- converter Request DTO;
- executar caso de uso;
- converter resposta.

Controller não deve:

- acessar Repository diretamente;
- conter regra de negócio;
- executar SQL;
- publicar Kafka diretamente.

## DTOs

Cada fronteira deve possuir seu modelo quando necessário.

```text
CreateOrderRequest
    ↓
CreateOrderCommand
    ↓
Order
    ↓
OrderResult
    ↓
OrderResponse
```

Evitar reutilizar a mesma classe para REST, banco, Kafka e domínio.
