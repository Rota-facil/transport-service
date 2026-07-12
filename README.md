# transport-service

Servico central do dominio de transporte. Gerencia onibus, rotas, viagens, passageiros, check-ins, recorrencia de rotas, feedbacks e integracao com um servico externo de inteligencia.

## Para que serve

- Registrar e listar onibus.
- Registrar rotas com instituicoes, pontos de embarque, dias da semana e onibus recorrentes.
- Criar viagens recorrentes por agendamento.
- Permitir entrada, saida e check-in de estudantes em viagens.
- Iniciar, cancelar e processar viagens.
- Listar viagens por perfil do usuario autenticado.
- Gerar interpretacao de rota e mapa de calor por integracao HTTP.
- Publicar eventos para auditoria, notificacao e arquivos.

## Porta e base path

- Aplicacao: `transport-service`
- Porta: `8085`
- Context path: `/transports`
- Via gateway: `http://localhost:8080/transports`

## Endpoints principais

Onibus:

- `POST /transports/bus/register`: cadastra onibus. Exige `ADMIN`.
- `GET /transports/bus`: lista onibus.
- `GET /transports/bus/{busId}`: busca onibus.

Rotas:

- `POST /transports/routes/register`: cadastra rota. Exige `ADMIN`.
- `GET /transports/routes`: lista rotas conforme usuario/prefeitura.
- `GET /transports/routes/{routeId}`: busca rota.
- `POST /transports/routes/{routeId}/interpreter`: solicita interpretacao da rota ao intelligence service.
- `POST /transports/routes/{routeId}/board-point/heat-map`: gera mapa de calor de pontos de embarque.

Viagens:

Relatórios administrativos:

- `GET /transports/reports/student-absences?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`: gera PDF de faltas.
- `GET /transports/reports/cancelled-trips?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`: gera PDF de viagens canceladas.


- `POST /transports/trips/process?tripId={uuid}&latitude={lat}&longitude={lng}`: processa posicao/status de viagem.
- `POST /transports/trips/{tripId}/join`: adiciona usuario a viagem.
- `POST /transports/trips/{tripId}/exit`: remove usuario da viagem.
- `POST /transports/trips/{tripId}/checkin`: registra check-in.
- `POST /transports/trips/{tripId}/init`: inicia a ida da viagem. Exige pelo menos um aluno cadastrado.
- `POST /transports/trips/{tripId}/return/init`: inicia a volta após a finalização da ida.
- `POST /transports/trips/{tripId}/cancel`: cancela viagem.
- `GET /transports/trips`: lista viagens.
- `GET /transports/trips/my-trips`: lista viagens de hoje do usuario autenticado.
- `GET /transports/trips/{tripId}`: busca viagem.
- `GET /transports/trips/{tripId}/students`: lista estudantes da viagem.

Feedback:

- `GET /transports/feedbacks/users/{userId}`: lista feedbacks recebidos pelo usuário para ADMIN/SUPERUSER da mesma prefeitura.

- `POST /transports/users/{userId}/evaluate`: avalia outro usuario.

Infra:

- `GET /transports/health-check`
- `/transports/v3/api-docs`
- `/transports/swagger-ui.html`

## Agendamento

`TripSchedule` executa diariamente as `03:00` no fuso `America/Sao_Paulo` e cria viagens para rotas recorrentes do dia.

## Intelligence service

O servico chama `INTELLIGENCE_SERVICE_BASE_URL`, default `http://localhost:8000`, com base path `/intelligence`:

- `GET /intelligence`
- `POST /intelligence/route/interpretation`
- `POST /intelligence/route/heat-map`

## Eventos consumidos

Exchange `auth.events`:

- `user.created`
- `user.updated`
- `driver.admin.updated`
- `user.deleted`
- `user.deactivate`

Exchange `places.events`:

- `institution.created`
- `institution.updated`
- `institution.deleted`
- `boarding.created`
- `boarding.updated`
- `boarding.deleted`

Esses eventos mantem copias locais de usuarios, instituicoes e pontos de embarque.

## Eventos publicados

Exchange `transport.events`:

- `trip.created`
- `trip.running`
- `trip.cancelled`
- `trip.deleted`
- `route.created`
- `route.updated`
- `route.deleted`
- `bus.created`
- `bus.updated`
- `bus.deleted`
- `user.feedback`

Eventos de inicio e cancelamento de viagem sao consumidos por `notification-service` e `audit-service`. Eventos de remocao de viagem, CRUD de onibus e feedback sao consumidos por `audit-service`.

## Banco de dados

- Default: `jdbc:postgresql://localhost:5435/transport_database`
- Usuario default: `rota-facil`
- Senha default: `admin`
- Usa `hibernate-spatial` para dados geograficos.
- Migrations: `src/main/resources/db/migration`

## Como rodar

Pre-requisitos:

- Java 21.
- PostgreSQL com banco `transport_database`.
- Eureka.
- RabbitMQ.
- `auth-service` e `places-service` em funcionamento para popular dados via eventos.

Comando:

```bash
cd transport-service
./mvnw spring-boot:run
```

## Especializacao

Este servico concentra as regras operacionais de transporte. Ele nao e a fonte principal de usuarios ou lugares; recebe esses dados por eventos e os usa para operar viagens e rotas.
