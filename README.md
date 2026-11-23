# ms-email

Microsserviço de envio de e-mails assíncrono. A API recebe o pedido, enfileira no **RabbitMQ** e um
consumidor envia pelo **Amazon SES**, com retry e dead-letter queue. Ambiente AWS simulado com **LocalStack**.

**Stack:** Java 17 · Spring Boot 3.3 · RabbitMQ · Amazon SES (AWS SDK v2) · PostgreSQL · Flyway · Spring Security · Docker

## Arquitetura

```
POST /emails ─► Postgres (PENDING) ─► email.exchange ─► email.send.queue ─► Consumer ─► SES ─► SENT
                                                              │ 3 tentativas falharam
                                                              ▼
                                                   email.dlx ─► email.send.dlq    (ERROR)
```

- **202 Accepted:** o cliente não espera o envio; acompanha pelo `GET /emails/{id}`.
- **Publicação após o commit:** a mensagem só vai para a fila depois que o e-mail existe no banco.
- **Retry + DLQ:** 3 tentativas com backoff exponencial; depois, status `ERROR` com o motivo e mensagem na DLQ.
- **Idempotência:** mensagens duplicadas (entrega *at least once*) não reenviam e-mails já enviados.
- **Provedor plugável:** `EmailSender` com implementações SES (padrão) e SMTP (perfil `smtp`).

## Como rodar

Pré-requisitos: Docker e Java 17+.

```bash
docker compose up -d
./mvnw spring-boot:run                                              # envia via SES (LocalStack)
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local,smtp"      # envia via SMTP (Mailpit)
./mvnw test
```

Usuários locais: `admin` / `admin123` (envia e consulta) e `user` / `user123` (só consulta).

| | URL |
|---|---|
| Swagger (botão *Authorize*) | http://localhost:8080/swagger-ui.html |
| RabbitMQ | http://localhost:15672 (guest / guest) |
| Mailpit (caixa de entrada) | http://localhost:8025 |
| E-mails no SES (LocalStack) | http://localhost:4566/_aws/ses |

No perfil `local` os logs mostram só o ciclo de cada e-mail (criado → publicado → tentativas → `SENT`/`ERROR`).
Para ver o SQL do Hibernate, rode com `LOG_SQL=DEBUG`.

## Endpoints

| Método | Rota | Papel | Descrição |
|---|---|---|---|
| POST | `/emails` | ADM | Enfileira um e-mail |
| GET | `/emails` | USER, ADM | Lista com paginação |
| GET | `/emails/{id}` | USER, ADM | Status do envio |

## Produção (AWS)

Rodar com `SPRING_PROFILES_ACTIVE=aws`, configurado por variáveis de ambiente: RDS PostgreSQL (`DB_*`),
Amazon MQ for RabbitMQ (`RABBITMQ_*`, TLS) e SES via role IAM com `ses:SendEmail`.
Fora do perfil `local` nenhum usuário é criado automaticamente.

## Pontos de evolução

- **Gestão de usuários:** criar o admin inicial via variável de ambiente e um endpoint de cadastro restrito a ADM.
- **Segredos** no AWS Secrets Manager em vez de variáveis de ambiente.
- **Reprocessamento da DLQ** por endpoint administrativo.
- **Observabilidade:** logs estruturados em JSON, métricas no Actuator/Micrometer e tracing com correlation id.