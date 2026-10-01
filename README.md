![Kotlin Template — API modular com Kotlin e Spring Boot](docs/assets/header.svg)

# Kotlin Template

Base para APIs REST em **Kotlin e Spring Boot**, organizada por capacidades de negócio, com domínio independente e ports/adapters. Usa Spring MVC com JPA bloqueante, PostgreSQL e migrations Flyway. O contexto `identity` implementa cadastro, login e consulta do usuário autenticado.

[Início rápido](#início-rápido) · [Comandos](#comandos) · [Ferramentas](#ferramentas) · [API](#api-e-autenticação) · [Arquitetura](#arquitetura) · [Testes](#testes)

## Ferramentas

| Ferramenta | Uso no projeto |
| --- | --- |
| Kotlin 2.3.21 / JDK 25 | Linguagem e toolchain do build |
| Spring Boot 4.1.1 / Spring MVC | Aplicação e endpoints HTTP síncronos |
| Spring Data JPA / Hibernate | Persistência e transações nos casos de uso |
| PostgreSQL / Flyway | Banco relacional e versionamento do schema |
| Spring Security / OAuth2 Resource Server | Autorização por roles e validação de Bearer JWT |
| PBKDF2 | Hash de senha com salt aleatório e identificador de algoritmo |
| Spring Validation | Validação dos contratos HTTP |
| springdoc-openapi 3.1.0 / Swagger UI | Documentação da aplicação, rotas, campos e autenticação |
| Redis / Spring Data Redis | Infraestrutura disponível para cache e estado temporário |
| Apache Kafka 4.1.2 / Spring for Apache Kafka | Broker local e infraestrutura para mensageria |
| Actuator | Healthcheck e informações da aplicação |
| JUnit 5 / MockMvc / Testcontainers | Testes HTTP, segurança e integrações reais |
| Gradle Wrapper / GNU Make / Docker Compose | Build, comandos de desenvolvimento e serviços locais |
| GraalVM Build Tools / REST Docs / Asciidoctor | Plugins e suporte de build disponíveis |

Redis e Kafka estão configurados para desenvolvimento; os fluxos atuais de identidade usam PostgreSQL. Ainda não há listeners, publicação de eventos ou cache nesses fluxos.

## Início rápido

Instale **JDK 25**, Docker com Compose, GNU Make e OpenSSL. Configure `JAVA_HOME` para um JDK 25; o Java padrão do terminal pode ser outra versão.

```sh
export JAVA_HOME=/caminho/para/jdk-25
make env
make run
```

`make env` gera `.env` com senha de banco e chave JWT aleatórias, com permissão restrita. Se o arquivo já existir, ele é preservado. O arquivo é ignorado pelo Git; [`.env.example`](.env.example) mostra as opções para configuração manual.

`make run` carrega `.env`, inicia PostgreSQL, Redis e Kafka, aguarda seus healthchecks e executa `./gradlew bootRun`. A aplicação inicia em `http://localhost:8080`; Flyway aplica as migrations e Hibernate valida o schema.

| Serviço | Endereço padrão |
| --- | --- |
| Aplicação | `http://localhost:8080` |
| Swagger UI | [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| OpenAPI JSON | [localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| Healthcheck | [localhost:8080/actuator/health](http://localhost:8080/actuator/health) |
| PostgreSQL | `localhost:5432`, database `mydatabase`, usuário `myuser` |
| Redis | `localhost:6379` |
| Kafka no host | `localhost:9092` |
| Kafka na rede Docker | `kafka:19092` |

As portas da infraestrutura são publicadas apenas em `127.0.0.1`. Se alguma estiver ocupada, ajuste `.env` antes de iniciar, por exemplo `POSTGRES_PORT=5433`. O Compose é um ambiente local descartável; não define volumes persistentes. Interrompa a aplicação com `Ctrl+C` e remova os containers com `make down`.

## Comandos

O Makefile usa comentários inline `##` para gerar a ajuda. Execute `make` ou `make help` para listar os comandos.

| Comando | Ação |
| --- | --- |
| `make env` | Gera os segredos locais sem substituir `.env` existente |
| `make up` | Inicia a infraestrutura e aguarda os healthchecks |
| `make down` | Para e remove os containers |
| `make ps` | Mostra serviços e portas |
| `make logs` | Acompanha logs dos serviços |
| `make config` | Valida o Compose sem imprimir segredos |
| `make run` | Inicia a infraestrutura e a aplicação |
| `make build` | Compila, testa e empacota a aplicação |
| `make test` | Executa todos os testes |
| `make test-http` | Testes HTTP e segurança, sem Docker |
| `make test-integration` | Testes de identidade com PostgreSQL real |
| `make test-class TEST='<classe>'` | Executa uma classe ou padrão de testes |
| `make clean` | Remove os artefatos do build |
| `make kafka-topics` | Lista os tópicos do broker local |
| `make kafka-create-topic TOPIC=events.demo` | Cria um tópico com uma partição e uma réplica |

Todos os comandos de build usam `./gradlew`. `make build` e `make test` exigem Docker para os testes de integração.

## Configuração

O Makefile lê `.env` e exporta as variáveis para Gradle e Compose. O Spring Boot lê variáveis de ambiente; executar `./gradlew` diretamente não carrega `.env` automaticamente.

| Variável | Padrão / finalidade |
| --- | --- |
| `DATABASE_PASSWORD` | Obrigatória; também usada pelo PostgreSQL no Compose |
| `JWT_SECRET` | Obrigatória; Base64 de pelo menos 32 bytes aleatórios |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/mydatabase` |
| `DATABASE_USERNAME` | `myuser` |
| `DATABASE_POOL_SIZE` | `10` |
| `POSTGRES_PORT` | `5432`; Make deriva `DATABASE_URL` quando não definido |
| `REDIS_HOST` / `REDIS_PORT` | `localhost` / `6379` |
| `REDIS_PASSWORD` | Vazia; para conexão a um Redis externo autenticado |
| `KAFKA_PORT` | `9092`; porta externa e endereço anunciado pelo broker local |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092`; Make deriva a porta de `KAFKA_PORT` |
| `SERVER_PORT` | `8080` |
| `JWT_ISSUER` | `template` |
| `JWT_ACCESS_TOKEN_TTL` | `PT15M`; duração ISO-8601 |
| `CORS_ALLOWED_ORIGINS` | Vazia; origens permitidas separadas por vírgula |
| `OPENAPI_ENABLED` | `true`; habilita Swagger e OpenAPI |
| `DOCKER_COMPOSE_ENABLED` | `false`; o Makefile gerencia a infraestrutura |

Para trocar portas, ajuste `.env`, por exemplo `POSTGRES_PORT=5433` ou `KAFKA_PORT=9094`, antes de `make up`. Para serviços externos, configure seus endereços e execute `make run` apenas se também quiser iniciar a infraestrutura local; caso contrário, exporte as variáveis necessárias e execute `./gradlew bootRun`.

Também é possível ativar o gerenciamento de Compose pelo Spring Boot com `DOCKER_COMPOSE_ENABLED=true`. As conexões de PostgreSQL e Redis são descobertas pelo Boot; Kafka usa `KAFKA_BOOTSTRAP_SERVERS` explicitamente. [Referência de serviços de desenvolvimento do Spring Boot](https://docs.spring.io/spring-boot/reference/features/dev-services.html).

## API e autenticação

| Método | Rota | Acesso | Resposta |
| --- | --- | --- | --- |
| POST | `/api/auth/signup` | Público | `201` com `id`, `name`, `email` e `roles` |
| POST | `/api/auth/login` | Público | `200` com `accessToken`, `tokenType` e `expiresIn` |
| GET | `/api/users/me` | Bearer JWT | `200` com o usuário autenticado |
| GET | `/actuator/health` | Público | Estado de saúde sem detalhes |
| GET | `/actuator/info` | `ADMIN` | Informações da aplicação |

### Cadastro

```sh
curl -X POST http://localhost:8080/api/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{"name":"Maria Exemplo","email":"maria@example.com","password":"senha-de-exemplo"}'
```

| Campo | Validação / comportamento |
| --- | --- |
| `name` | Obrigatório, não vazio, até 100 caracteres; espaços nas extremidades são removidos |
| `email` | Obrigatório, válido, até 254 caracteres; normalizado para minúsculas e único |
| `password` | Obrigatória, de 8 a 128 caracteres; preservada e armazenada como hash PBKDF2 |

Campos desconhecidos são rejeitados. Cadastro sempre atribui `USER`; o cliente não pode enviar roles. A migration cria `USER` e `ADMIN`. Atribuir `ADMIN` depende de um futuro fluxo administrativo ou de uma operação controlada no banco.

### Login e acesso protegido

```sh
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"maria@example.com","password":"senha-de-exemplo"}'

curl http://localhost:8080/api/users/me \
  -H 'Authorization: Bearer <accessToken>'
```

Login recebe email e senha com os mesmos limites do cadastro. O token contém ID técnico e roles, sem nome ou email. `expiresIn` informa a validade em segundos, `900` na configuração padrão.

No Swagger, execute signup e login; cole apenas `accessToken` em **Authorize**. O Swagger adiciona `Bearer` ao header. As rotas públicas dispensam autenticação e as protegidas herdam `bearerAuth`. A especificação descreve campos, validação, respostas e erros.

### Segurança e erros

A autenticação é stateless pelo header `Authorization`. Sessão, cookies de autenticação, formulário e HTTP Basic estão desabilitados. JWTs validam assinatura HS256, emissor e expiração, com tolerância de relógio padrão do Spring Security. Preserve a chave entre reinícios e instâncias; trocá-la invalida os tokens existentes.

Senha e hash nunca aparecem nas respostas. Erros seguem Problem Details (`application/problem+json`): `400` para entrada inválida, `401` para credenciais ou token inválidos, `403` para permissão insuficiente e `409` para email duplicado. Falhas de validação incluem `errors` com campo e mensagem, sem valores rejeitados.

O escopo atual não inclui refresh tokens, revogação, limitação de tentativas ou administração de roles. Alterações de roles afetam novos tokens; os já emitidos seguem válidos até expirar. Em ambientes publicados, forneça os segredos pelo ambiente e use HTTPS.

## Kafka local

O Compose executa um único nó Kafka em **KRaft**, com os papéis de broker e controller, sem ZooKeeper. O listener externo anuncia `localhost:${KAFKA_PORT}`; o interno anuncia `kafka:19092`. Essa separação permite conectar a aplicação executada no host e ferramentas executadas na rede Docker. [Documentação Docker do Apache Kafka](https://kafka.apache.org/41/getting-started/docker/).

```sh
make up
make kafka-create-topic TOPIC=events.demo
make kafka-topics
```

O broker usa PLAINTEXT e fatores de replicação iguais a 1 para o ambiente local. Os comandos acima administram tópicos; os endpoints de identidade ainda não publicam mensagens.

## Arquitetura

```text
src/main/kotlin/com/kotlin/template/
├── TemplateApplication.kt
├── identity/
│   ├── domain/          # User, Role e porta UserRepository; sem Spring/JPA
│   ├── application/     # Signup, Login, CurrentUser e portas de saída
│   ├── infrastructure/  # JPA, hash de senha, JWT e Spring Security
│   └── interfaces/rest/ # Controllers, DTOs, validação e tratamento de erros
└── shared/infrastructure/config/
    └── OpenApiConfig.kt

src/main/resources/
├── application.yml
└── db/migration/V1__create_users_and_roles.sql
```

Fluxo HTTP: **request → controller → caso de uso → porta → adapter**. Transações ficam nos casos de uso e Open Session in View está desabilitado. O domínio não depende de Spring, JPA ou HTTP. As convenções de desenvolvimento estão na [skill spring-kotlin](.agents/skills/spring-kotlin/SKILL.md) e no [AGENTS.md](AGENTS.md).

O header em [`docs/assets/header.svg`](docs/assets/header.svg) é um SVG local com animações CSS, sem scripts ou fontes externas. Ele mantém uma composição estática quando o visualizador não anima SVG e respeita `prefers-reduced-motion`.

## Testes

```sh
make test-http
make test-integration
make test-class TEST=com.kotlin.template.TemplateApplicationTests
```

- **AuthHttpTests:** cadastro, login, validação, email duplicado, rejeição de roles no payload, CORS, JWTs ausentes/adulterados/expirados, autorização e senhas longas com caracteres multibyte.
- **IdentityIntegrationTests:** PostgreSQL via Testcontainers, Flyway, JPA, login, cadastro concorrente, Swagger e esquema de segurança OpenAPI.
- **TemplateApplicationTests:** inicialização do contexto com PostgreSQL, Redis e Kafka via Testcontainers.

Testcontainers cria seus próprios serviços e não depende dos containers do Compose. Os testes usam dados e uma chave JWT sintéticos, exclusivos do ambiente de teste. O relatório HTML fica em `build/reports/tests/test/index.html`.
