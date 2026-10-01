# Kotlin Template

Spring Boot 4 / Kotlin, Spring MVC, JPA/PostgreSQL, Flyway e Spring Security.
Código organizado no contexto `identity`, com domínio independente e adapters de HTTP, persistência e segurança.

## Executar

O build exige **JDK 25**. Use o Gradle Wrapper.

```sh
export JAVA_HOME=/caminho/para/jdk-25
export DATABASE_PASSWORD=uma-senha-local
export JWT_SECRET="$(openssl rand -base64 32)"
export DOCKER_COMPOSE_ENABLED=true
./gradlew bootRun
```

O suporte Docker Compose conecta a aplicação às portas publicadas por PostgreSQL e Redis. O Compose também lê `DATABASE_PASSWORD`. Sem esse suporte, configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `REDIS_HOST` e `REDIS_PORT` para seus serviços.

Kafka não está no Compose. `KAFKA_BOOTSTRAP_SERVERS` apenas define o endereço: inicie um broker separadamente quando houver uso de Kafka.

`JWT_SECRET` é obrigatório, Base64 com pelo menos 32 bytes aleatórios. Preserve a mesma chave entre reinícios e instâncias; trocá-la invalida tokens existentes. Não versione segredos. `JWT_ISSUER` define o emissor (padrão `template`), `JWT_ACCESS_TOKEN_TTL` usa duração ISO-8601 (padrão `PT15M`). Os tokens validam assinatura HS256, emissor e expiração, com a tolerância de relógio padrão do Spring Security.

`CORS_ALLOWED_ORIGINS` aceita origens separadas por vírgula. Sem configuração, requisições de outras origens não são autorizadas. `SERVER_PORT` tem padrão 8080. Flyway cria o schema e Hibernate somente o valida; Open Session in View está desabilitado.

## Rotas

| Método | Rota | Acesso | Resultado |
| --- | --- | --- | --- |
| POST | `/api/auth/signup` | Público | 201 com id, name, email e roles |
| POST | `/api/auth/login` | Público | 200 com accessToken, tokenType e expiresIn |
| GET | `/api/users/me` | Bearer JWT | 200 com dados do usuário autenticado |
| GET | `/actuator/health` | Público | Estado de saúde sem detalhes |
| GET | `/actuator/info` | ADMIN | Informações da aplicação |

Signup recebe `name` (1–100 caracteres), `email` (válido, até 254 caracteres) e `password` (8–128 caracteres). Login recebe email e senha com os mesmos limites. Nome é aparado; email é convertido para minúsculas; senha é preservada. Campos desconhecidos são rejeitados. Cadastro sempre atribui `USER`, nunca aceita roles fornecidas pelo cliente. A migration cria `USER` e `ADMIN`; promoção a ADMIN requer um fluxo administrativo futuro ou operação controlada no banco.

Senhas usam PBKDF2 com salt aleatório e identificador de algoritmo. Senha e hash não aparecem nas respostas. Email duplicado retorna 409, credenciais inválidas 401, entradas inválidas 400, falta de autorização 403. Erros seguem Problem Details (`application/problem+json`), com campo `errors` para falhas de validação, sem valores rejeitados.

A autenticação é stateless, exclusivamente por `Authorization: Bearer <accessToken>`; cookies, sessão, formulário e HTTP Basic estão desabilitados. Tokens contêm ID técnico e roles. Não há refresh token, logout com revogação ou limitação de tentativas nesta versão; mudanças de roles só afetam novos tokens, e os existentes continuam válidos até expirar. Use HTTPS no ambiente publicado.

## OpenAPI

Acesse [Swagger UI](http://localhost:8080/swagger-ui.html) ou [OpenAPI JSON](http://localhost:8080/v3/api-docs). Faça signup, execute login e cole `accessToken` em **Authorize**. O Swagger acrescenta o prefixo Bearer. As operações de signup/login são documentadas sem exigência de token; as demais herdam `bearerAuth`. Contratos incluem campos, limites, status e descrições. `OPENAPI_ENABLED=false` desabilita Swagger e a especificação.

## Validar

```sh
./gradlew test --tests 'com.kotlin.template.identity.AuthHttpTests'
./gradlew test --tests 'com.kotlin.template.identity.IdentityIntegrationTests'
```

O primeiro cobre HTTP, validação e segurança sem Docker. O segundo usa PostgreSQL real via Testcontainers para migrations, JPA, login, concorrência de cadastro e OpenAPI. Os dados e a chave dos testes são sintéticos e exclusivos dos testes. O teste original de contexto usa também Kafka e Redis em containers.
