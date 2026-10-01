# Testes

A estratégia segue as fronteiras arquiteturais.

## Domain

- unit tests;
- sem Spring;
- sem banco;
- preferencialmente sem mocks.

## Application

- unit tests;
- ports substituídos por fakes/mocks quando necessário.

## Persistence

- integração com PostgreSQL real via Testcontainers.

## Redis

- integração via Testcontainers.

## Kafka

- integração quando necessário.

## REST

- Spring MVC tests;
- validação de contratos HTTP.

Regra principal:

```text
quanto mais próximo do domínio,
menos infraestrutura deve ser necessária para testar.
```
