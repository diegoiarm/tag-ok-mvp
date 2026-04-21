# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TAG OK MVP — a toll expense tracking platform for drivers in Santiago, Chile. Three components:
- **routes-service** — Java 21 / Spring Boot 3 REST API (port 8000)
- **routes-ui** — React 19 + TypeScript admin web portal (port 5173)
- **tag-ok-app** — Android (Kotlin / Jetpack Compose) mobile app

All under `Producto/`. Documentation in `Documentacion/`. Toll gate data as JSON in `Producto/porticos/`.

## Commands

### Database (must be running first)
```bash
cd Producto
docker compose up -d      # Start PostgreSQL+PostGIS + pgAdmin
docker compose down       # Stop
```

### Backend
```bash
cd Producto/routes-service
./mvnw spring-boot:run    # Start API at http://localhost:8000
./mvnw test               # Run unit tests
./mvnw clean package      # Build JAR
```

### Frontend
```bash
cd Producto/routes-ui
npm run dev               # Start dev server at http://localhost:5173
npm run build             # Production build
npm run lint              # ESLint check
```

### Android
```bash
cd Producto/tag-ok-app
./gradlew build
./gradlew test
./gradlew connectedAndroidTest   # Requires connected device/emulator
```

## Architecture

### Backend (routes-service)
Classic layered MVC: `Controller → Service → Repository → PostgreSQL+PostGIS`.

Key domain entities:
- `Autopista` — highway, owns a collection of `Portico`s
- `Portico` — toll gate with lat/lon geometry (PostGIS Point, SRID 4326)
- `CalendarioTarifario` / `ReglaTarifaria` — time-based pricing rules per vehicle type (MOTO, AUTO, CAMIONETA, BUS, CAMION, CAMION_REMOLQUE)

Route calculation calls `pgRouting` directly via native SQL in `RouteRepository`. The DB holds 50k+ street edges from OSM. Route responses return GeoJSON segments.

CORS is locked to `localhost:5173`.

### Frontend (routes-ui)
Data fetching via **TanStack React Query** hooks (`useRoute`, `usePorticos`). API calls go through an Axios instance in `src/api/axios.ts` with `baseURL: localhost:8000`.

`Mapa.tsx` is the main container — renders Leaflet map, a GeoJSON route layer (`RouteLayer.ts`), and clickable `PorticoMark` components that show tariff info on selection.

### Database schema highlights
- `edge` table: OSM street segments with `source`/`target` for pgRouting, `cost`/`reverse_cost`, GIST-indexed geometry
- `portico` table: toll gate points spatially joined to edges via `asignarPorticoAEdge.sql`
- Topology built once with `createTopology.sql` (do not re-run without dropping first)

### Android (tag-ok-app)
Currently a Jetpack Compose skeleton — Material 3 theme only. `MainActivity.kt` is the entry point.

## Key API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET | `/autopistas` | List all highways |
| POST | `/autopistas` | Create highway with porticos |
| DELETE | `/autopistas/{id}` | Delete highway |
| GET | `/porticos` | List toll gates (summary) |
| GET | `/porticos/{id}` | Toll gate with full tariff schedule |
| GET | `/api/routes?lon1=&lat1=&lon2=&lat2=` | Calculate route (returns GeoJSON) |

## Database Connection
Configured in `Producto/routes-service/src/main/resources/application.properties`:
- Host: `localhost:5432`, DB: `db_rutas`, user: `admin`
- pgAdmin available at `localhost:5050`

## Spatial / Routing Notes
- All geometries use SRID 4326 (WGS84)
- `calles.sql` is 54 MB — only load once during initial setup
- pgRouting topology (`createTopology.sql`) takes 1–2 min; re-running requires dropping the topology first
- `RouteRepository` uses native SQL with pgRouting's `pgr_dijkstra` function
