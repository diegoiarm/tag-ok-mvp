# TAG OK - Control y trazabilidad de gastos de peajes

![Estado del Proyecto](https://img.shields.io/badge/Estado-En_Desarrollo_(MVP)-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-brightgreen)
![React](https://img.shields.io/badge/React-19-61dafb)
![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack_Compose-purple)

**TAG OK** es una solución tecnológica diseñada para que los conductores de Santiago de Chile puedan planificar, registrar y controlar sus gastos por uso de vías concesionadas (TAG). Desarrollado como MVP para **Grupo Sentte**, transforma un gasto históricamente reactivo en una decisión consciente y planificada.

---

## Problema que resuelve

Los conductores urbanos enfrentan incertidumbre financiera y falta de trazabilidad en sus gastos de transporte, provocada por la fragmentación de información entre distintas concesionarias. TAG OK centraliza todo en un ecosistema con una **app móvil** para conductores y un **panel web administrativo**, conectados a través de una API REST.

---

## Componentes del sistema

### routes-service - API de rutas y tarifas
`Java 21 / Spring Boot 3 / PostgreSQL + PostGIS + pgRouting` - Puerto `8000`

- Calcula rutas óptimas en Santiago usando el algoritmo pgr_dijkstra sobre datos OSM (50k+ segmentos de calle).
- Determina qué pórticos TAG cruza una ruta y calcula el costo según calendario tarifario y tipo de vehículo.
- Expone pórticos, autopistas y tarifas mediante una API REST.
- Soporta dos estrategias de cobro: por pórtico cruzado y por tramo recorrido.

### history-service - Servicio de historial
`Java 21 / Spring Boot 3 / MongoDB` - Puerto `8001`

- Persiste el historial de cruces de pórticos por usuario.
- Guarda rutas calculadas con sus detalles de cobro.
- Base de datos documental para flexibilidad en el esquema de historial.

### osm-importer - Importador de datos OSM
`Java / Maven (herramienta standalone)`

- Parsea archivos GeoJSON de OpenStreetMap por cada comuna de Santiago.
- Filtra y normaliza segmentos viales con bounding box configurable.
- Carga los datos en la tabla `edge` mediante un pipeline de middlewares y scripts SQL secuenciales.

### routes-ui - Panel web administrativo
`React 19 / TypeScript / Vite / Leaflet` - Puerto `5173`

- Mapa interactivo con visualización de pórticos y rutas calculadas (Leaflet + GeoJSON).
- Gestión de autopistas, pórticos y tarifas.
- Autenticación con Supabase Auth.
- Data fetching con TanStack React Query.

### tag-ok-app — Aplicación móvil para conductores
`Kotlin / Jetpack Compose / Android`

- Planificación de viajes con cálculo de costo TAG antes de salir.
- Mapa interactivo con rutas y pórticos activos.
- Gestión de vehículos y presupuestos personales.
- Registro de historial de cruces y gastos.
- Autenticación con Supabase Auth.

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| App móvil | Kotlin, Jetpack Compose, Material 3 |
| Panel web | React 19, TypeScript, Vite, Leaflet, TanStack Query |
| API principal | Java 21, Spring Boot 3, JPA/Hibernate |
| API historial | Java 21, Spring Boot 3, Spring Data MongoDB |
| Base de datos | PostgreSQL + PostGIS + pgRouting |
| Historial | MongoDB |
| Autenticación | Supabase Auth |
| Infraestructura local | Docker Compose |

---

## Estructura del repositorio

```
tag-ok-mvp/
│
├── Producto/                          # Todo el código fuente
│   ├── docker-compose.yml             # PostgreSQL+PostGIS, pgAdmin, MongoDB
│   ├── porticos/                      # Datos JSON de pórticos por autopista
│   │   ├── autopistaCentral.json
│   │   ├── costaneraNorte.json
│   │   ├── vespucioNorte.json
│   │   ├── vespucioOrienteI.json
│   │   ├── vespucioSur.json
│   │   └── ...
│   │
│   ├── routes-service/                # API REST — rutas y tarifas
│   │   └── src/main/java/com/tagok/routes_service/
│   │       ├── controller/            # AutopistaController, PorticoController, RouteController, TarifaController
│   │       ├── domain/
│   │       │   ├── autopista/         # Autopista, TipoCobro
│   │       │   ├── calendario/        # CalendarioTarifario, RangoHorario, ReglaTemporal, TipoDia
│   │       │   ├── portico/           # Portico
│   │       │   ├── tarifa/            # ReglaTarifaria, Tarifa, TarifaCalculada, CalculadorTarifa
│   │       │   │   └── calculo/       # Strategy pattern: CalculadorPorPortico, CalculadorPorTramo
│   │       │   ├── tramo/             # Tramo
│   │       │   └── vehiculo/          # TipoVehiculo (MOTO, AUTO, CAMIONETA, BUS, CAMION, CAMION_REMOLQUE)
│   │       ├── dto/                   # Request y Response por recurso
│   │       ├── repository/            # AutopistaRepository, PorticoRepository, RouteRepository (pgRouting)
│   │       └── service/
│   │           ├── application/       # AutopistaService, PorticoService, RouteService, TarifaService
│   │           └── mapper/            # Mappers entidad ↔ DTO
│   │
│   ├── history-service/               # Servicio de historial (MongoDB)
│   │   └── src/main/java/com/tagok/history_service/
│   │       ├── controller/            # HistorialController, RutaGuardadaController
│   │       ├── domain/                # Historial, RutaGuardada, PorticoCruce, PorticoRuta, Segmento, Vehiculo
│   │       ├── repository/            # HistorialRepository, RutaGuardadaRepository
│   │       └── service/               # HistorialService, RutaGuardadaService
│   │
│   ├── osm-importer/                  # Herramienta de importación de datos OSM
│   │   └── src/main/
│   │       ├── java/com/roony/
│   │       │   ├── domain/            # BoundingBox, BoundingBoxFilter, Element, Geometry, Tags
│   │       │   └── infrastructure/
│   │       │       ├── database/      # DatabaseInitializer, RoutingInitializer
│   │       │       ├── filesystem/    # JsonFileScanner
│   │       │       ├── middleware/    # Pipeline de procesamiento (BoundsFilter, SqlExport)
│   │       │       └── parser/        # OsmJsonParser, ElementMapper
│   │       └── resources/
│   │           ├── database-scripts/  # SQL secuencial: extensiones, edge table, topología, costos
│   │           └── datos-calles/      # JSON OSM por comuna (Cerrillos, La Florida, Las Condes, etc.)
│   │
│   ├── routes-ui/                     # Panel web administrativo (React + TypeScript)
│   │   └── src/
│   │       ├── api/                   # axios.ts, porticos.ts, routes.ts
│   │       ├── app/
│   │       │   ├── context/           # AuthContext.tsx (Supabase)
│   │       │   ├── layout/            # MainLayout.tsx
│   │       │   ├── lib/               # supabase.ts
│   │       │   └── pages/             # Home.tsx, Login.tsx
│   │       ├── components/            # Mapa.tsx, PorticoMark.tsx, RouteLayer.ts, CalendarioTarifario.tsx, CobroMark.tsx
│   │       ├── features/
│   │       │   └── admin/pages/       # AdminPage.tsx, UsuariosPage.tsx
│   │       ├── hooks/                 # usePorticos, usePortico, useRoute, useCalles, useUsuarios
│   │       └── types/                 # types.ts
│   │
│   └── tag-ok-app/                    # App móvil Android (Kotlin / Jetpack Compose)
│       └── app/src/main/java/com/tagok/app/
│           ├── data/
│           │   ├── dto/               # PorticoResponse, RouteResponse, TarifaCalculada, Cruce, etc.
│           │   ├── local/             # GeofenceClient, GeofenceBroadcastReceiver, LocationEventBus
│           │   ├── mapper/            # RouteMapper
│           │   ├── remote/            # RouteApi, HttpClientProvider
│           │   ├── repository/        # RouteRepository
│           │   ├── GeocodingRepository.kt
│           │   ├── PresupuestoRepository.kt
│           │   └── VehiculoRepository.kt
│           ├── domain/
│           │   ├── interfaces/        # IRouteRepository
│           │   └── model/             # Point, Portico, Route
│           ├── ui/
│           │   ├── auth/              # LoginScreen, AuthViewModel
│           │   ├── boleta/            # BoletaScreen
│           │   ├── home/              # HomeScreen, HomeViewModel
│           │   ├── map/               # MapScreen, MapViewModel
│           │   ├── navigation/        # NavGraph
│           │   ├── perfil/            # PerfilScreen, PerfilViewModel
│           │   ├── planificar/        # PlanificarViajeScreen
│           │   ├── presupuesto/       # PresupuestoScreen, PresupuestoViewModel
│           │   ├── register/          # RegisterScreen, RegisterViewModel
│           │   ├── theme/             # Color, Theme, Type
│           │   └── vehiculos/         # VehiculosScreen, VehiculosViewModel
│           ├── MainActivity.kt
│           └── SupabaseClient.kt
│
├── Documentación/
│   ├── Aseguramiento de Calidad y Planificación/
│   │   └── Carta Gantt.xlsx
│   ├── Definición Técnica y Configuración/
│   └── Diagramas Técnicos de Estructura y Lógica/
│       ├── Diagrama de Casos de Uso.png
│       └── Diagrama de Ishikawa - Tag OK.png
│
└── Gestión/
    ├── 1.1.2 Documento de registro de definición e identificación del proyecto.pdf
    └── Integrantes.txt
```

---

## API endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/autopistas` | Listar todas las autopistas |
| POST | `/autopistas` | Crear autopista con pórticos |
| DELETE | `/autopistas/{id}` | Eliminar autopista |
| GET | `/porticos` | Listar pórticos (resumen) |
| GET | `/porticos/{id}` | Pórtico con calendario tarifario completo |
| POST | `/api/tarifas/calcular` | Calcular cobro para una lista de pórticos cruzados |
| GET | `/api/routes?lon1=&lat1=&lon2=&lat2=` | Calcular ruta (retorna GeoJSON + pórticos + costo) |

---

## Requisitos previos

- Docker Desktop
- Java 21 + Maven
- Node.js 20+
- Android Studio (para la app móvil)

---

## Instalación y ejecución

### 1. Base de datos

```bash
cd Producto
docker compose up -d
```

Levanta PostgreSQL+PostGIS (puerto 5432), pgAdmin (puerto 5050) y MongoDB.

### 2. Backend - routes-service

```bash
cd Producto/routes-service
./mvnw spring-boot:run
```

API disponible en `http://localhost:8000`.

### 3. Backend - history-service

```bash
cd Producto/history-service
./mvnw spring-boot:run
```

### 4. Frontend web

```bash
cd Producto/routes-ui
npm install
npm run dev
```

Panel disponible en `http://localhost:5173`.

### 5. App Android

Abrir `Producto/tag-ok-app` en Android Studio y ejecutar en emulador o dispositivo físico.

---

## Notas de configuración

- Conexión a BD configurada en `routes-service/src/main/resources/application.properties` (host `localhost:5432`, BD `db_rutas`, usuario `admin`).
- La topología pgRouting (`createTopology.sql`) tarda 1–2 min en construirse; re-ejecutarla requiere eliminar la topología previa.
- Los datos de calles por comuna están en `osm-importer/src/main/resources/datos-calles/comunas-separadas/`.
- CORS del backend está configurado para `localhost:5173`.

---

## Equipo de desarrollo

Proyecto desarrollado bajo metodología **Scrum** + **Aprendizaje Basado en Proyectos (ABP)**.

| Integrante | Rol |
|-----------|-----|
| Paulina Troncoso | UX/UI y Desarrollo Móvil |
| Ricardo Sánchez | Backend y Base de Datos |
| Diego Rodríguez | Frontend Web y QA |

---

*Proyecto académico para la asignatura Taller Aplicado de Programación — Portafolio. Cliente: Grupo Sentte.*
