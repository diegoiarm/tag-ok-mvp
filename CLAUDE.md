# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TAG OK MVP — a toll expense tracking platform for drivers in Santiago, Chile. It has grown from a monolith into a **microservices architecture** behind an API gateway, with Supabase for authentication.

Components (all under `Producto/`):

| Component | Stack | Local port | Role |
|-----------|-------|-----------|------|
| **gateway-service** | Spring Cloud Gateway (WebFlux), Java 21 | 8080 | Single entry point; validates Supabase JWT, routes to backends |
| **routes-service** | Spring Boot 3, Java 21 | 8000 | Highways, toll gates, tariffs, route calculation (PostGIS + pgRouting) |
| **history-service** | Spring Boot 3, Java 21 | 8003 | Crossing history, saved routes, billing (MongoDB + Kafka consumer) |
| **osm-importer** | Java 21 batch job | — | One-time OSM → PostGIS street-graph importer (**do not run normally**) |
| **routes-ui** | React 19 + TypeScript (Vite) | 5173 | Admin web portal |
| **tag-ok-app** | Android (Kotlin / Jetpack Compose) | — | Mobile app (skeleton) |

Infrastructure: PostgreSQL+PostGIS+pgRouting, MongoDB, Kafka (+ Zookeeper), pgAdmin, mongo-express — all via `Producto/docker-compose.yml`.

Documentation in `Documentacion/`. Toll gate / street JSON data in `Producto/porticos/`, `Producto/osm-importer/src/main/resources/datos-calles/`, and SQL in `Producto/sql/`.

## Commands

### Infrastructure (start first)
```powershell
cd Producto
docker compose up -d db-rutas db-historial zookeeper kafka kafka-setup pgadmin mongo-express
docker compose down
```
**Do not run a bare `docker compose up`** — it tries to build `osm-importer`, whose single-stage Dockerfile copies a pre-built `target/osm-importer.jar` that usually doesn't exist, aborting the whole build. Always name the services you want, or put `osm-importer` behind a Compose profile.

### Backend services (run with a Spring profile!)
Each Spring service has **no datasource in its base `application.properties`** — connection details live only in the `local` / `docker` profiles. Running without a profile fails with *"Failed to determine a suitable driver class"*. On Windows PowerShell, quote the argument:
```powershell
cd Producto/routes-service
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=local"   # → http://localhost:8000
.\mvnw test
.\mvnw clean package
```
Same pattern for `history-service` (→ 8003) and `gateway-service` (→ 8080). **The gateway also needs the `local` profile when services run outside Docker** — its base `application.yml` routes to Docker hostnames (`routes-service:8080`, `history-service:8080`), which don't resolve on the host; `application-local.yml` re-points them to `localhost:8000` / `localhost:8003`. Running the gateway without the profile makes every proxied call return HTTP 500.

### Frontend
```powershell
cd Producto/routes-ui
npm run dev      # http://localhost:5173
npm run build
npm run lint
```

### Android
```powershell
cd Producto/tag-ok-app
./gradlew build
./gradlew test
```

## Architecture

### Request flow
```
routes-ui ──(Supabase JWT)──► gateway-service :8080 ──► routes-service / history-service
```
The frontend's Axios instance (`src/api/axios.ts`) points at `http://localhost:8080/api` (the gateway) and attaches the Supabase `access_token` as a Bearer header on every request via an interceptor. It throws if there is no active session.

### gateway-service
Spring Cloud Gateway (reactive/WebFlux). Defined declaratively in `src/main/resources/application.yml`:
- Validates JWT as an OAuth2 resource server against the **Supabase issuer** (`https://<project>.supabase.co/auth/v1`); public key fetched automatically from the issuer.
- Routes (`StripPrefix=2` drops `/api/<service>`):
  - `/api/routes/**` → `routes-service:8080`
  - `/api/history/**` → `history-service:8080`
- `SecurityConfig.java` holds the security wiring.

### routes-service
Classic layered MVC: `Controller → Service → Repository → PostgreSQL+PostGIS`. Controllers are versioned under `/v1/...`.

Key domain entities:
- `Autopista` — highway, owns a collection of `Portico`s
- `Portico` — toll gate with lat/lon geometry (PostGIS Point, SRID 4326); has admin CRUD + bulk load + estado toggle
- `CalendarioTarifario` / `ReglaTarifaria` — time-based pricing rules per vehicle type (MOTO, AUTO, CAMIONETA, BUS, CAMION, CAMION_REMOLQUE)

Route calculation calls `pgRouting` via native SQL in `RouteRepository`. The DB holds 50k+ OSM street edges. Route responses return GeoJSON segments. When a route crosses a toll gate, a `portico-cruzado` event is produced to Kafka.

### history-service
MongoDB-backed (`historial_db`). Consumes the Kafka `portico-cruzado` topic (consumer group `history-service`) to build per-user crossing history, aggregated annually/monthly/daily (`HistorialAnualDocument` with nested snapshots). Also stores saved routes (`RutaGuardada`) and generates billing (`BoletaController`).

### Frontend (routes-ui)
React 19 + Vite + React Router + **TanStack React Query**. Supabase client in `src/app/lib/supabase.ts`. Admin features live under `src/features/admin/` (pages: Autopistas, Porticos, Reportes, Usuarios). `Mapa` renders a Leaflet map with a GeoJSON route layer and clickable toll-gate marks showing tariff info. Routes: `/`, `/mapa`, `/login`, `/usuarios`, `/autopistas`, `/porticos`, `/reportes`, `/files`.

### Supabase
Used for auth (JWT issuer for the gateway) and user management. Edge functions in `Producto/supabase/functions/`: `list-users`, `update-user-status` (admin user-management backing the Usuarios page). Config in `Producto/supabase/config.toml`.

### Kafka
Single topic `portico-cruzado` (3 partitions, replication 1), created by `Producto/kafka/init-topics.sh` (the `kafka-setup` one-shot container). routes-service is the producer; history-service the consumer.

### osm-importer
One-time job that imports OSM street data into PostGIS and builds the pgRouting topology (SQL in `src/main/resources/database-scripts/`). Marked `# No ejecutar!` in compose. Its Dockerfile is single-stage and expects a pre-built `target/osm-importer.jar`; build it with `.\mvnw clean package` first if you ever need to run it.

### Android (tag-ok-app)
Jetpack Compose skeleton — Material 3 theme only. `MainActivity.kt` is the entry point.

## Key API Endpoints
All client traffic goes through the gateway (`http://localhost:8080/api/...`); paths below are the **service-internal** paths after `StripPrefix`.

**routes-service** (`/api/routes/...` → strips to):
| Method | Path | Description |
|--------|------|-------------|
| GET/POST/PUT/DELETE | `/v1/autopistas` (+ `/import`, `/{id}`) | Highway CRUD + bulk import |
| GET | `/v1/porticos` | List toll gates (summary) |
| GET | `/v1/porticos/admin` | Admin list with full detail |
| GET | `/v1/porticos/{id}` | Toll gate with full tariff schedule |
| POST/PUT/DELETE | `/v1/porticos` (+ `/{id}`) | Toll gate CRUD |
| PATCH | `/v1/porticos/{id}/estado` | Toggle active/inactive |
| POST | `/v1/porticos/bulk` | Bulk create (returns per-item result) |
| GET/PUT | `/v1/porticos/{id}/tarifas` | Get / replace a toll gate's tariff config (reglas + calendario) — CU19 |
| GET | `/v1/tramos` | Admin list of tramos (for tariff management) |
| GET/PUT | `/v1/tramos/{id}/tarifas` | Get / replace a tramo's tariff config — CU19 |
| POST | `/v1/rutas` | Calculate route (returns GeoJSON) |
| POST | `/v1/tarifas` | Calculate the toll for a crossing (NOT tariff CRUD) |

**history-service** (`/api/history/...` → strips to):
| Method | Path | Description |
|--------|------|-------------|
| GET | `/v1/historial/years`, `/resumen`, `/year/{año}[/month/{mes}[/day/{dia}]]` | Crossing history (drill-down) |
| GET | `/v1/historial/patentes`, `/autopistas` | Filter option lists |
| POST | `/v1/historial/resumen-filtrado` | Filtered summary |
| POST | `/v1/boleta/obtener` | Generate billing document |
| GET/POST | `/rutas-guardadas` (+ `/{idToken}`) | Saved routes |

## Service Connections (local dev)
Docker-mapped host ports (see `docker-compose.yml`) and the values the `local` Spring profiles expect:
- **PostgreSQL (db-rutas)**: `localhost:5431` → DB `db_rutas`, user/pass `admin/admin` (container exposes 5432)
- **MongoDB (db-historial)**: `localhost:5678` → DB `historial_db`, user/pass `admin/admin`
- **Kafka**: `localhost:9092`
- **pgAdmin**: `localhost:1000` (login `admin@admin.com` / `admin`)
- **mongo-express**: `localhost:8002`

In `docker` profiles, services instead use container hostnames (`db-rutas:5432`, `kafka:9092`, etc.) and all run on port 8080 internally.

## Spatial / Routing Notes
- All geometries use SRID 4326 (WGS84)
- `sql/calles.sql` is ~54 MB — only load once during initial setup
- pgRouting topology (`sql/createTopology.sql`) takes 1–2 min; re-running requires dropping the topology first
- `portico` points are spatially joined to edges via `sql/asignarPorticoAEdge.sql`
- `RouteRepository` uses native SQL with pgRouting's `pgr_dijkstra` function
