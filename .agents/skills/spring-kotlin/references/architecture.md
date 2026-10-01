# Arquitetura e localização de classes

Organizar o código por bounded context, definido pela capacidade de negócio.
Cada contexto controla seu modelo, seus repositórios e sua API pública. Começar
com módulos lógicos por packages; separar em módulos Gradle ou serviços somente
quando uma necessidade concreta justificar a mudança.

```text
<base-package>/
├── shared/
└── <context>/
    ├── domain/
    ├── application/
    ├── infrastructure/
    └── interfaces/
```

Manter `shared` pequeno. Evitar packages globais `controller`, `service`,
`repository`, `entity`, `dto`, `mapper`, `utils` e `helpers` que misturem contextos.

## Dependências

| Origem | Pode depender de |
|---|---|
| `domain` | Seu próprio domínio e biblioteca padrão |
| `application` | Domínio e portas que definem suas necessidades |
| `interfaces` | API de aplicação |
| `infrastructure` | Domínio, aplicação e bibliotecas técnicas |

O domínio não importa Spring, JPA/Hibernate, HTTP, Kafka ou Redis. A aplicação
não conhece controllers, entidades JPA, `KafkaTemplate` ou `RedisTemplate`.
Controllers não acessam repositories ou `EntityManager` diretamente.
Anotações técnicas ficam fora dos objetos de domínio; `@Service` e
`@Transactional` são aceitáveis em casos de uso.

Entre contextos, usar API de aplicação/facade, porta ou evento. Não importar
repositórios ou detalhes internos de persistência de outro contexto.

## Localização

| Elemento | Package relativo ao contexto |
|---|---|
| Aggregate, entity, value object | `domain/model/` |
| Evento, exceção, serviço de domínio | `domain/event/`, `domain/exception/`, `domain/service/` |
| Specification, factory, repository port | `domain/specification/`, `domain/factory/`, `domain/repository/` |
| Use case, command, query, result | `application/<use-case>/` |
| Porta de saída de aplicação | `application/port/` |
| Exceção de aplicação | Próxima do use case ou `application/exception/` |
| Controller, DTO HTTP, exception handler | `interfaces/rest/` |
| Listener Kafka, job de entrada | `interfaces/messaging/`, `interfaces/scheduler/` |
| JPA entity, Spring Data repository, adapter, mapper | `infrastructure/persistence/` |
| Redis, publisher Kafka, cliente externo | `infrastructure/cache/`, `infrastructure/messaging/`, `infrastructure/client/` |
| Configuração Spring | Próxima da infraestrutura configurada ou `infrastructure/config/` |

Manter mappers junto à fronteira transformada: HTTP na interface, JPA na
persistência, mensagem no listener e modelo externo no cliente.

## Nomes

Preferir nomes de negócio: `ConfirmOrder`, `CreateOrderCommand`, `FindOrderQuery`,
`OrderDetails`, `OrderRepository`, `JpaOrderRepository`, `OrderJpaEntity`.
Evitar `GenericService`, `BaseService`, `Manager` ou `Processor` quando existir
um nome preciso na linguagem do domínio. Não criar abstrações antecipadamente.
