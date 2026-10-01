# Contexto da aplicação

Para implementar ou revisar Spring/Kotlin, usar a skill local
[spring-kotlin](skills/spring-kotlin/SKILL.md) e carregar suas referências conforme
a tarefa. Ela reúne as convenções; este arquivo registra apenas o contexto local.

- Package raiz: `com.kotlin.template`; entrada: `TemplateApplication.kt`.
- Usar `./gradlew`. O build exige toolchain Java 25; o Java do terminal pode ser outro.
- HTTP usa WebFlux e persistência usa JPA bloqueante. Ao conectá-los, tratar a
  fronteira de execução e transação explicitamente.
- `compose.yaml` contém PostgreSQL e Redis. Kafka está na configuração de
  Testcontainers; sua dependência no build não configura o broker de desenvolvimento.
- Para validar mudanças, selecionar testes relevantes com `./gradlew test --tests '<classe>'`;
  testes de integração existentes usam Docker/Testcontainers.
