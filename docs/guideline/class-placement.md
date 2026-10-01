# Class Placement

Este arquivo define onde cada tipo de classe deve ficar.

## Domain

```text
<context>/domain/
```

| Tipo | Local |
|---|---|
| Aggregate Root | `domain/model/` |
| Entity | `domain/model/` |
| Value Object | `domain/model/` |
| Domain Event | `domain/event/` |
| Domain Exception | `domain/exception/` |
| Domain Service | `domain/service/` |
| Specification | `domain/specification/` |
| Repository Port | `domain/repository/` |
| Factory | `domain/factory/` |

## Application

```text
<context>/application/
```

Preferir organização por caso de uso:

```text
application/
├── create/
├── update/
├── delete/
└── find/
```

| Tipo | Local |
|---|---|
| Use Case | `application/<feature>/` |
| Command | `application/<feature>/` |
| Query | `application/<feature>/` |
| Result | `application/<feature>/` |
| Outbound Port | `application/port/` |
| Application Exception | próximo ao use case ou `application/exception/` |

## Interfaces

```text
<context>/interfaces/
```

| Tipo | Local |
|---|---|
| REST Controller | `interfaces/rest/` |
| Request DTO | `interfaces/rest/` |
| Response DTO | `interfaces/rest/` |
| REST Exception Handler | `interfaces/rest/` |
| Kafka Listener | `interfaces/messaging/` |
| Scheduled input job | `interfaces/scheduler/` |

## Infrastructure

```text
<context>/infrastructure/
```

| Tipo | Local |
|---|---|
| JPA Entity | `infrastructure/persistence/` |
| Spring Data Repository | `infrastructure/persistence/` |
| Repository Adapter | `infrastructure/persistence/` |
| Persistence Mapper | `infrastructure/persistence/` |
| Redis Adapter | `infrastructure/cache/` |
| Kafka Publisher | `infrastructure/messaging/` |
| External HTTP Client | `infrastructure/client/` |
| Spring Configuration | próximo da infraestrutura configurada ou `infrastructure/config/` |

## Mapper

O mapper deve ficar próximo da fronteira que transforma.

```text
REST ↔ Application
→ interfaces/rest/

Domain ↔ JPA
→ infrastructure/persistence/

External API ↔ Internal model
→ infrastructure/client/

Kafka message ↔ Application command
→ interfaces/messaging/
```

Evitar um package global:

```text
mapper/
```

com transformações de toda a aplicação.
