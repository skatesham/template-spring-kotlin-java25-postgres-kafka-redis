# REST e contratos HTTP

Colocar controllers, requests, responses, mappers HTTP e exception handlers em
`<base-package>.<context>.interfaces.rest`. Controllers são adapters de entrada.

```text
HTTP → Request DTO → Command/Query → Use case → Result → Response DTO
```

## Responsabilidades

O controller trata HTTP, valida formato, transforma a entrada, chama a API de
aplicação e converte o resultado. Não acessa repositories, `EntityManager`,
Redis ou publishers Kafka diretamente, nem decide regras de negócio.

Usar nomes como `CreateOrderRequest`, `OrderResponse` e `OrderController`.
DTO HTTP é um contrato próprio: não reutilizar entidade JPA, entidade de
domínio ou mensagem Kafka como request/response. Manter o mapper junto ao
controller ou contrato que transforma.

## Validação, erros e autorização

`@Valid`, `@NotBlank`, `@Size` e equivalentes validam formato e contrato de
entrada; regras de negócio continuam no domínio.

Usar `@RestControllerAdvice` para mapear erros internos para HTTP. O domínio
não conhece status codes. Não expor stack traces, segredos ou payloads
sensíveis nas respostas de erro.

Verificar a permissão do sujeito autenticado e sua relação com o recurso.
Um ID difícil de adivinhar não substitui autorização. Expor somente os campos
necessários para o consumidor e usar IDs técnicos independentes de dados
pessoais.

## Modelo de execução

Inspecionar se a aplicação utiliza Spring MVC ou WebFlux antes de escolher
assinaturas, adapters e testes. Preservar o modelo existente; exemplos MVC
não justificam adicionar outro starter HTTP a uma aplicação WebFlux.

JPA executa operações bloqueantes. Em WebFlux, tratar explicitamente a
fronteira de execução dessas operações e da transação para não bloquear o
event loop. Não presumir que `suspend` ou um retorno `Mono` torna JPA reativo.
Evitar chamadas `block()` dentro do fluxo HTTP reativo.
