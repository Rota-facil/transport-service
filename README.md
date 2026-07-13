# transport-service

Serviço operacional de transporte do Rota Fácil. Gerencia ônibus, rotas, viagens, passageiros, presença, geolocalização, feedback, métricas, relatórios e integração com inteligência.

## Porta e base path

- Porta: `8085`
- Context path: `/transports`
- Via gateway: `http://localhost:8080/transports`

## Endpoints

Ônibus:

- `POST /transports/bus/register`
- `GET /transports/bus` e `GET /transports/bus/{busId}`
- `PUT /transports/bus/{busId}` e `DELETE /transports/bus/{busId}`

Rotas:

- `POST /transports/routes/register`
- `GET /transports/routes`, `GET /transports/routes/simple`, `GET /transports/routes/{routeId}`
- `PUT /transports/routes/{routeId}`, `DELETE /transports/routes/{routeId}`
- `POST /transports/routes/{routeId}/interpreter`
- `GET /transports/routes/{routeId}/interpretations`
- `DELETE /transports/routes/{routeId}/interpretations/{interpretationId}`
- `POST /transports/routes/{routeId}/board-point/heat-map`

Viagens:

- `POST /transports/trips/register`: cria manualmente uma viagem de hoje para uma rota e um ônibus já associado; `ADMIN`.
- `POST /transports/trips/process?tripId={uuid}&latitude={lat}&longitude={lng}`
- `POST /transports/trips/{tripId}/join`, `/exit` e `/checkin`
- `POST /transports/trips/{tripId}/init`: inicia a ida.
- `POST /transports/trips/{tripId}/return/init`: inicia a volta.
- `POST /transports/trips/{tripId}/cancel`
- `GET /transports/trips`, `GET /transports/trips/my-trips`
- `GET /transports/trips/active`: lista todas as viagens ativas de hoje da prefeitura autenticada, sem paginacao.
- `GET /transports/trips/{tripId}`, `GET /transports/trips/{tripId}/students`

Feedbacks:

- `GET /transports/feedbacks/users/{userId}`: lista feedbacks, notas, remetente e data recebidos pelo usuário da mesma prefeitura; `ADMIN/SUPERUSER`.
- `POST /transports/feedbacks/{userId}/evaluate`
- `POST /transports/feedbacks/trips/{tripId}/users/{userId}`

Administração e análise:

- `GET /transports/metrics`
- `GET /transports/institutions/route-counts`
- `GET /transports/users/drivers`, `GET /transports/users/drivers/me`
- `PATCH /transports/users/drivers/{driverId}/bus/change`
- `GET /transports/reports/student-absences?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
- `GET /transports/reports/cancelled-trips?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`

Os relatórios retornam `application/pdf` como download e são filtrados pela prefeitura autenticada.

Infra: `GET /transports/health-check`, `/transports/v3/api-docs`, `/transports/swagger-ui.html`.

## Ciclo da viagem

- A ida só pode ser iniciada quando a viagem está `NOT_STARTED` e possui ao menos um aluno.
- A volta só pode ser iniciada quando a ida está `STARTED_FINISHED`.
- O envio de geolocalização não inicia automaticamente a volta.
- Antes do início, sair da viagem remove o `TripUser` e reduz o contador. Depois do início, mantém o vínculo e marca `ABSENT`, preservando dados para relatórios.
- O processamento geográfico usa lock pessimista, ignora viagens não iniciadas/terminais e verifica status já registrados antes de inserir. Assim, posições repetidas enquanto o ônibus permanece parado não duplicam chegadas.
- Instituições e pontos esperados são avaliados por fase (ida/volta); ausências e destinos ignorados são recalculados quando necessário.
- Instituições e pontos retornados nas rotas usam ordenação explícita por latitude, longitude e UUID para uma resposta determinística.

## Agendamento

`TripSchedule` roda diariamente às `03:00` em `America/Sao_Paulo` e cria viagens para rotas recorrentes do dia.

Administradores também podem criar uma viagem manual para testes ou necessidades operacionais. O backend exige que rota e ônibus estejam ativos, pertençam à prefeitura autenticada, que o ônibus tenha motorista e esteja associado à rota. A viagem é criada para o dia atual em `NOT_STARTED`.

## Intelligence service

Usa `INTELLIGENCE_SERVICE_BASE_URL`, padrão `http://localhost:8000`, para interpretação de rota e mapa de calor. É a exceção atual ao padrão de integração assíncrona entre serviços.

## Eventos

Consome de `auth.events`: `user.created`, `user.updated`, `driver.admin.updated`, `user.deleted`, `user.deactivate`.

Consome de `places.events`: CRUD de `institution.*` e `boarding.*`.

Publica em `transport.events`: `trip.created`, `trip.running`, `trip.cancelled`, `trip.deleted`, `trip.completed`, `user.trips.increased`, `user.trips.decreased`, `user.feedback`, CRUD de `route.*` e CRUD de `bus.*`.

## Persistência

- Banco PostGIS: `jdbc:postgresql://localhost:5435/transport_database`
- Usuário padrão: `rota-facil`
- Migrations: `src/main/resources/db/migration`
- Hibernate: `ddl-auto=validate`
- PDFs: Apache PDFBox, gerados em memória; os endpoints não exigem nova tabela.

## Como rodar

```bash
cd transport-service
./mvnw spring-boot:run
```

Requer Java 21, PostgreSQL/PostGIS, Eureka e RabbitMQ. `auth-service` e `places-service` precisam publicar eventos para popular as cópias locais.
