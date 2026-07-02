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

### Gateway-Service — Punto de entrada único al backend

```java
// Java 21 / Spring Boot 3 / Spring Gateway Webflux / Netty — Puerto 8080
```

- Enrruta peticiones a los servicios
- Integra OAuth, con Supabase como servidor, para autenticar peticiones
- Actúa como mediador entre peticiones para APIs externas

### routes-service — API de rutas y tarifas

```java
// Java 21 / Spring Boot 3 / PostgreSQL + PostGIS + pgRouting / Tomcat
```

- Calcula rutas óptimas en Santiago usando el algoritmo `pgr_dijkstra` sobre datos OSM (50k+ segmentos de calle)
- Determina qué pórticos TAG cruza una ruta y calcula el costo según calendario tarifario y tipo de vehículo
- Expone pórticos, autopistas y tarifas mediante una API REST
- Soporta dos estrategias de cobro: por pórtico cruzado y por tramo recorrido

### history-service — Servicio de historial

```java
// Java 21 / Spring Boot 3 / MongoDB / Tomcat
```

- Persiste el historial de cruces de pórticos por usuario
- Guarda rutas calculadas con sus detalles de cobro
- Base de datos documental para flexibilidad en el esquema de historial

### routes-ui — Panel web administrativo

```typescript
// React 19 / TypeScript / Vite / Leaflet — Puerto 5173
```

- Mapa interactivo con visualización de pórticos y rutas calculadas (Leaflet + GeoJSON)
- Gestión de autopistas, pórticos y tarifas
- Autenticación con Supabase Auth
- Data fetching con TanStack React Query

### tag-ok-app — Aplicación móvil para conductores

```kotlin
// Kotlin / Jetpack Compose / Android
```

- Planificación de viajes con cálculo de costo TAG antes de salir
- Mapa interactivo con rutas y pórticos activos
- Gestión de vehículos y presupuestos personales
- Registro de historial de cruces y gastos
- Autenticación con Supabase Auth

### Broker de eventos Apache Kafka

- Comunica servicios internamente de manera asíncrona, desacoplándolos y permitiendo escalabilidad horizontal
- Un evento tiene un productor, pero puede tener muchos consumidores

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| App móvil | Kotlin, Jetpack Compose, Material 3 |
| Panel web | React 19, TypeScript, Vite, Leaflet, TanStack Query |
| API gateway | `Java 21`, Spring Boot 3, Spring WebFlux |
| API principal | `Java 21`, Spring Boot 3, JPA/Hibernate |
| API historial | `Java 21`, Spring Boot 3, Spring Data MongoDB |
| Base de datos | PostgreSQL + PostGIS + pgRouting |
| Historial | MongoDB |
| Autenticación | Supabase Auth |
| Broker de eventos | Apache Kafka + Zookeeper |
| Infraestructura local | Docker Compose |

---

## Estructura del repositorio

```
tag-ok-mvp/
│
├── .github/                           # Workflows / CI
├── .vscode/                           # Configuración del editor
│
├── .env                               # Variables de entorno (local)
├── .env.example                       # Plantilla de variables de entorno
├── .gitignore
├── README.md
├── SETUP_ENTORNO.md                   # Guía de setup del entorno
│
├── Producto/                          # Código fuente del producto
│   ├── docker-compose.yml             # PostgreSQL+PostGIS, pgAdmin, MongoDB, Kafka
│   │
│   ├── porticos/                      # Datos JSON de pórticos por autopista
│   │   ├── autopistaCentral.json
│   │   ├── costaneraNorte.json
│   │   ├── porticoVacio.json
│   │   ├── vespucioNorte.json
│   │   ├── vespucioOrienteI.json
│   │   └── vespucioSur.json
│   │
│   ├── gateway-service/               # API Gateway (Spring Cloud Gateway, WebFlux)
│   │   └── src/main/java/com/tagok/gateway_service/
│   │       └── config/                # SecurityConfig.java, CorsConfig
│   │
│   ├── routes-service/                # API REST — rutas y tarifas
│   │   └── src/main/java/com/tagok/routes_service/
│   │       ├── controller/            # AutopistaController, PorticoController, RouteController, TarifaController
│   │       ├── domain/
│   │       │   ├── autopista/         # Autopista, TipoCobro
│   │       │   ├── calendario/        # CalendarioTarifario, RangoHorario, ReglaTemporal, TipoDia
│   │       │   ├── portico/           # Portico
│   │       │   ├── tarifa/            # ReglaTarifaria, Tarifa, TarifaCalculada, CalculadorTarifa
│   │       │   │   └── calculo/       # Strategy: CalculadorPorPortico, CalculadorPorTramo
│   │       │   ├── tramo/             # Tramo
│   │       │   └── vehiculo/          # TipoVehiculo
│   │       ├── dto/                   # Request / Response
│   │       ├── repository/            # AutopistaRepository, PorticoRepository, RouteRepository (pgRouting)
│   │       └── service/
│   │           ├── application/       # AutopistaService, PorticoService, RouteService, TarifaService
│   │           └── mapper/            # Mappers entidad ↔ DTO
│   │
│   ├── history-service/               # Servicio de historial (MongoDB)
│   │   └── src/main/java/com/tagok/history_service/
│   │       ├── controller/            # HistorialController, RutaGuardadaController, BoletaController
│   │       ├── domain/                # Historial, RutaGuardada, PorticoCruce, Boleta
│   │       ├── ia/                    # ExtractorFacturaIA (Gemini adapter)
│   │       ├── repository/            # HistorialRepository, RutaGuardadaRepository
│   │       └── service/               # HistorialService, RutaGuardadaService, ComparadorFacturas
│   │
│   ├── osm-importer/                  # Importación de datos OSM → PostGIS
│   │   └── src/main/
│   │       ├── java/com/roony/
│   │       │   ├── domain/            # BoundingBox, Element, Geometry, Tags
│   │       │   └── infrastructure/
│   │       │       ├── database/      # DatabaseInitializer, RoutingInitializer
│   │       │       ├── filesystem/    # JsonFileScanner
│   │       │       ├── middleware/    # Pipeline (BoundsFilter, SqlExport)
│   │       │       └── parser/        # OsmJsonParser, ElementMapper
│   │       └── resources/
│   │           ├── database-scripts/  # SQL: extensiones, edge table, topología, costos
│   │           └── datos-calles/      # JSON OSM por comuna
│   │
│   ├── routes-ui/                     # Panel web (React 19 + TypeScript + Vite)
│   │   └── src/
│   │       ├── api/                   # axios.ts, porticos.ts, routes.ts
│   │       ├── app/
│   │       │   ├── context/           # AuthContext.tsx
│   │       │   ├── layout/            # MainLayout.tsx
│   │       │   ├── lib/               # supabase.ts
│   │       │   └── pages/             # Home.tsx, Login.tsx
│   │       ├── components/            # Mapa.tsx, PorticoMark, RouteLayer, CalendarioTarifario
│   │       ├── features/admin/pages/  # AdminPage.tsx, UsuariosPage.tsx
│   │       ├── hooks/                 # usePorticos, useRoute, useUsuarios
│   │       └── types/                 # types.ts
│   │
│   ├── tag-ok-app/                    # App Android (Kotlin / Jetpack Compose)
│   │   └── app/src/main/java/com/tagok/app/
│   │       ├── MainActivity.kt
│   │       ├── SupabaseClient.kt
│   │       ├── data/
│   │       │   ├── auth/              # AuthTokenProvider
│   │       │   ├── dto/
│   │       │   │   ├── boleta/        # BoletaDtos, ComparacionDtos
│   │       │   │   ├── history/       # HistorialDtos
│   │       │   │   ├── notificacion/  # NotificacionDtos
│   │       │   │   ├── portico/       # PorticoResponse, CalendarioTarifarioResponse, TramoResponse, etc.
│   │       │   │   ├── presupuesto/   # PresupuestoDtos
│   │       │   │   ├── route/         # RouteRequest/Response, CobroPortico/Tramo/RutaResponse
│   │       │   │   ├── tarifa/        # TarifaCalculadaResponse, CruceResponse
│   │       │   │   ├── vehiculo/      # VehiculoDts
│   │       │   │   ├── Cruce.kt
│   │       │   │   ├── PorticoCruzadoRequest.kt
│   │       │   │   ├── RouteSegment.kt
│   │       │   │   ├── TarifaCalculada.kt
│   │       │   │   └── TarifaRequest.kt
│   │       │   ├── map/               # GeofenceBroadcastReceiver
│   │       │   ├── mapper/            # BoletaMapper, HistorialMapper, PorticoMapper, RouteMapper, etc.
│   │       │   ├── remote/
│   │       │   │   ├── exceptions/    # ApiErrorType, ApiException
│   │       │   │   ├── interfaces/    # ClientsInterfaces
│   │       │   │   ├── ApiClient.kt
│   │       │   │   ├── ApiConfig.kt
│   │       │   │   ├── BoletaApi.kt, HistoryApi.kt, NotificacionApi.kt, PorticoApi.kt
│   │       │   │   ├── PresupuestoApi.kt, RouteApi.kt, TarifaApi.kt, VehiculoApi.kt
│   │       │   │   └── HttpClientProvider.kt
│   │       │   ├── repository/        # BoletaRepository, HistoryRepository, PorticoRepository, RouteRepository, etc.
│   │       │   ├── GeocodeSuggestion.kt
│   │       │   ├── GeocodingRepository.kt
│   │       │   ├── Presupuesto.kt
│   │       │   └── Vehiculo.kt
│   │       ├── di/
│   │       │   ├── ServiceLocator.kt
│   │       │   └── modules/           # ApiModule, AppModule, LocationModule, NetworkModule, RepositoryModule, ServiceModule, ViewModelModule
│   │       ├── domain/
│   │       │   ├── exceptions/        # ApplicationError, ApplicationErrorMapper
│   │       │   ├── interfaces/        # RepositoryInterfaces
│   │       │   ├── model/
│   │       │   │   ├── boleta/        # BoletaModels, ComparacionModels
│   │       │   │   ├── history/       # HistorialModels
│   │       │   │   ├── location/      # PorticoGeofence
│   │       │   │   ├── notificacion/  # NotificacionModels
│   │       │   │   ├── portico/       # PorticoType, CalendarioTarifario, TramoPortico, ReglaTarifaria, etc.
│   │       │   │   ├── presupuesto/   # PresupuestoModels
│   │       │   │   ├── routes/        # Point, Portico, Route, Toll, Tramo
│   │       │   │   ├── tarifa/        # TarifaCalculada, TarifaModels
│   │       │   │   └── vehiculo/      # VehiculoModels
│   │       │   ├── services/
│   │       │   │   ├── interfaces/    # ServiceInterfaces, ILocationProvider
│   │       │   │   ├── AlertaService.kt
│   │       │   │   ├── ApplicationService.kt
│   │       │   │   ├── BoletaService.kt
│   │       │   │   ├── HistoryService.kt
│   │       │   │   ├── LocationProvider.kt
│   │       │   │   ├── PlanificarService.kt
│   │       │   │   └── PorticoService.kt
│   │       │   └── vehiculo/          # TipoVehiculo
│   │       └── ui/
│   │           ├── auth/              # LoginScreen, AuthViewModel
│   │           ├── boleta/            # BoletaScreen, BoletaViewModel, comparacion/ (ComparacionScreen, FacturaPicker)
│   │           ├── common/            # DateUtils, EmptyState, ErrorContent, LoadingContent, etc.
│   │           ├── components/
│   │           │   ├── map/           # DirectionField, MapControls, RouteResult
│   │           │   └── routes/        # TollItems
│   │           ├── historial/         # HistorialScreen, HistorialViewModel, components/ (calendar, day, month, year, shared), model/, utils/
│   │           ├── home/              # HomeScreen, HomeViewModel
│   │           ├── map/               # MapScreen, MapViewModel, portico/ (PorticoContainer, PorticoDetail, PorticoLayer, etc.), route/ (RouteLayer)
│   │           ├── navigation/        # NavGraph
│   │           ├── notificaciones/    # NotificacionesScreen, NotificacionesViewModel
│   │           ├── perfil/            # PerfilScreen, PerfilViewModel
│   │           ├── planificar/        # PlanificarViajeScreen, PlanificarViajeViewModel, RouteBottomCard
│   │           ├── presupuesto/       # PresupuestoScreen, PresupuestoViewModel
│   │           ├── register/          # RegisterScreen, RegisterViewModel
│   │           ├── theme/             # BrandColors, Color, Theme, Type
│   │           └── vehiculos/         # VehiculosScreen, VehiculosViewModel
│   │
│   ├── supabase/                      # Configuración y edge functions
│   │   ├── config.toml
│   │   ├── supabase-schema.sql
│   │   └── functions/
│   │       ├── list-users/            # Edge function: listar usuarios
│   │       ├── update-user-status/    # Edge function: cambiar estado
│   │       └── update-user-role/      # Edge function: cambiar rol
│   │
│   ├── kafka/                         # Configuración de Kafka
│   │   ├── init-topics.sh
│   │   └── server.properties
│   │
│   ├── scripts/                       # Scripts de simulación
│   │   ├── seed-alertas.ps1
│   │   ├── simular-boleta-vespucio-sur.ps1
│   │   ├── simular-cruces.ps1
│   │   └── verificar-alertas.ps1
│   │
│   └── sql/                           # Scripts SQL auxiliares
│       ├── asignarPorticoAEdge.sql
│       ├── calles.sql
│       ├── createTopology.sql
│       ├── porticosTest.sql
│       ├── script.sql
│       └── vespucioNorte.json
│
├── Documentación/
│   ├── Aseguramiento de Calidad y Planificación/
│   │   └── Carta Gantt.xlsx
│   ├── Diagramas Técnicos de Estructura y Lógica/
│   │   ├── Diagrama de Arquitectura.png
│   │   ├── Diagrama de Casos de Uso.png
│   │   └── Diagrama de Ishikawa - Tag OK.png
│   ├── Manual de Usuario - Administrador.pdf
│   ├── Manual de Usuario - Conductor.pdf
│   ├── Manual de usuario - TAG OK Admin Web.md
│   ├── Manual de usuario - TAG OK.md
│   ├── Plan de Despliegue.pdf
│   ├── Plan de Pruebas.pdf
│   ├── Plan Implementación ambiente desarrollo backend.pdf
│   ├── Plan Implementación ambiente desarrollo móvil.pdf
│   ├── Plan Implementación ambiente desarrollo web.pdf
│   ├── Reporte de Plan de Pruebas Ejecutadas.html
│   └── Resumen Estado de Avance Casos de Uso.xlsx
│
└── Gestión/
    ├── 1.1.2 Documento de registro de definición e identificación del proyecto.pdf
    └── Integrantes.txt
```

---

## API endpoints

El punto de entrada es el **API gateway** (`gateway-service`, puerto `8080`). Enrruta según el prefijo de la ruta:

| Ruta base | Servicio destino |
|-----------|-----------------|
| `/api/routes/**` | routes-service |
| `/api/history/**` | history-service |

> **Seguridad:** Actualmente el gateway usa `.permitAll()` para desarrollo. Valida el JWT vía `oauth2ResourceServer` pero no rechaza peticiones sin token. En producción se puede restringir cambiando a `.anyExchange().authenticated()` en `SecurityConfig.java`.

La documentación detallada de cada endpoint (modelos, métodos, respuestas) está en Swagger/OpenAPI:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Requisitos previos

- **Docker Desktop**
- **Java 21** + Maven
- **Android Studio** (para la app móvil)

---

## Esquema de Supabase

- El esquema de tablas que usa Supabase se encuentra en `./Producto/supabase/supabase-schema.sql`
- Contiene las tablas necesarias junto a las funciones de presupuesto (edge functions)

---

## Notas de configuración

Antes de iniciar el sistema, se debe hacer una configuración en el archivo de entorno **`.env`**, donde se colocarán las URLs de autenticación, APIs externas y API keys.

---

## Instalación y ejecución

El sistema se encuentra bajo Docker, cuenta con un archivo `docker-compose.yml` que gestiona la construcción de imágenes para la ejecución dentro de una máquina.

1. Tener **Docker Desktop** abierto y ejecutándose en el equipo, junto con la compatibilidad de virtualización.

2. Instalar **SDK**, **Maven** para compilar proyectos Java.

3. Ir a la carpeta `./tag-ok-mvp/Producto`, aquí se encuentra el archivo `docker-compose.yml`. Se abren dos aristas:

   - Inicializar solo el backend:
     ```bash
     docker compose up gateway-service -d
     ```

   - Inicializar el dashboard + backend:
     ```bash
     docker compose up frontend -d
     ```

4. El archivo Docker está configurado para levantar todas las dependencias.

5. El sistema necesita los datos de las calles de Santiago y pórticos. Se encuentran en el repositorio.

6. Para la carga de pórticos, se puede hacer desde el Dashboard de administración, cargando todos los archivos JSON en `./Producto/porticos`. El sistema solo aceptará los que tengan el formato requerido y se hará la carga masiva.

   > ⚠ Los archivos JSON en `./Producto/porticos/` fueron creados y digitados manualmente, por lo que están sujetos a errores humanos (coordenadas, códigos, tarifas, etc.). Verifica los datos antes de usarlos en producción.

   También se puede hacer mediante la API directamente (curl / Postman):

   ```bash
   POST /api/routes/v1/porticos/bulk
   Content-Type: application/json
   ```

   ```json
   [
     {
       "nombre": "string",
       "ubicacion": { "lat": -33.1234, "lng": -70.5678 },
       "activo": true,
       "autopistaId": 1,
       "tipoCobro": "POR_PORTICO",
       "tramoId": null
     }
   ]
   ```

   > `tipoCobro`: `POR_PORTICO` | `POR_TRAMO` — `tramoId` solo es requerido si `tipoCobro` es `POR_TRAMO`.

7. Para cargar las calles se necesita de `osm-importer`. Este programa recoge un archivo de exportación en formato JSON de Open Street Maps, lo formatea a los datos que necesitamos y los importa a la base de datos del servicio de rutas. Para compilarlo ir a `./Producto/osm-importer`:

   ```bash
   mvn compile
   mvn dependency:copy-dependencies
   java -cp "target/classes;target/dependency/*" com.roony.Main
   ```

   > Se podría tener el JAR, comparto el código fuente si se necesitara a futuro alguna modificación =)

8. Con estos dos pasos el sistema ya está listo para empezar a funcionar.

### App Android

1. Abrir `Producto/tag-ok-app` en **Android Studio** y ejecutar en emulador o dispositivo físico.

2. Antes de compilar, configurar la URL del gateway en `Producto/tag-ok-app/app/src/main/java/com/tagok/app/data/remote/ApiConfig.kt`:

   ```kotlin
   // Para ejecutar en emulador (localhost de la máquina anfitriona):
   const val BASE_URL = "http://10.0.2.2:8080/api"

   // Para ejecutar en dispositivo físico con el backend en la misma red:
   // const val BASE_URL = "http://<IP_LOCAL>:8080/api"
   ```

   > `10.0.2.2` es la dirección especial del emulador Android que resuelve al `localhost` del host. Si usas dispositivo físico, reemplázala por la IP local de la máquina donde corre el gateway (ej. `192.168.1.100`).

3. El **token de Mapbox** se configura en `Producto/tag-ok-app/local.properties` (archivo ignorado por git):

   ```properties
   MAPBOX_ACCESS_TOKEN=pk.xxxxx
   ```

   El `build.gradle.kts` lo lee desde `local.properties` y lo inyecta en `BuildConfig.MAPBOX_ACCESS_TOKEN`, usado en `MainActivity.kt:22`. Como respaldo, también puede definirse en `~/.gradle/gradle.properties` de forma global. No está hardcodeado en el código Kotlin, pero queda en el proyecto local de cada desarrollador. Solicita el token gratis en [mapbox.com](https://account.mapbox.com/access-tokens/).

---

## Equipo de desarrollo

Proyecto desarrollado bajo metodología **Scrum** + **Aprendizaje Basado en Proyectos (ABP)**.

| Integrante | Rol |
|-----------|-----|
| Paulina Troncoso | UX/UI y Desarrollo Móvil |
| Ricardo Sánchez | Backend y Base de Datos |
| Diego Rodríguez | Frontend Web y QA |

---

> **Recomendación sobre osm-importer:** La importación de datos OSM es un proceso intensivo que solo debe ejecutarse una vez durante la configuración inicial del entorno. Los archivos JSON de calles por comuna ya están incluidos en el repositorio y el script SQL `calles.sql` (~54 MB) contiene los datos precargados. Evita re-ejecutar el importador a menos que necesites actualizar la red vial. El contenedor Docker de `osm-importer` está excluido del arranque normal (`docker compose up`) por esta razón.
>
> **Pipeline de ejecución:**
>
> 1. **`JsonFileScanner`** — Escanea el directorio `datos-calles/` en busca de archivos JSON
> 2. **`DatabaseInitializer`** — Ejecuta los scripts SQL en orden:
>    - `00_clean_tables.sql` — Elimina tablas `edge` y `edge_vertices_pgr` si existen
>    - `01_extensions.sql` — Habilita PostGIS y pgRouting
>    - `02_edge_table.sql` — Crea la tabla `edge` (id, element_id, name, type, surface, lanes, maxspeed, oneway, geometry, cost, reverse_cost, source, target)
>    - `03_indexes.sql` — Crea índices GIST (geometry), source, target, element_id, type
> 3. **`OsmJsonParser`** — Parsea el JSON OSM, filtra por bounding box de Santiago y transforma cada elemento en una fila en la tabla `edge`
> 4. **`RoutingInitializer`** — Ejecuta los scripts finales:
>    - `04_create_topology.sql` — `pgr_createTopology('edge', ...)` construye el grafo de pgRouting (aprox. 1-2 min)
>    - `05_cost.sql` — Calcula `cost` y `reverse_cost` para cada arista
>
> **Cómo se calcula el costo (05_cost.sql):**
>
> ```sql
> cost = (longitud_geografia / (velocidad_kmh * 1000 / 3600)) * factor_tipo_via
> ```
>
> | Tipo de vía | Velocidad (km/h) | Factor |
> |-------------|:-:|:-:|
> | motorway, motorway_link | 100 / 60 | 0.5 |
> | trunk, trunk_link | 80 / 50 | 0.7 |
> | primary, primary_link | 60 / 40 | 2.5 |
> | secondary, secondary_link | 50 / 30 | 2.5 |
> | tertiary, tertiary_link | 40 / 20 | 2.5 |
> | residential | 30 | 2.5 |
> | service | 20 | 2.5 |
> | unclassified | 30 | 2.5 |
>
> **Cómo editar los costos:** Modifica directamente `05_cost.sql` en `Producto/osm-importer/src/main/resources/database-scripts/`. Puedes cambiar las velocidades (`speed_kmh`) o los factores multiplicadores (`WHEN type = '...' THEN X`) para que el algoritmo de ruteo favorezca o evite ciertos tipos de vía. Si el importador ya se ejecutó, puedes actualizar los costos manualmente con:
>
> ```sql
> UPDATE edge SET cost = ... , reverse_cost = ... ;
> ```
>
> `reverse_cost` se asigna como `-1` en vías de un solo sentido (`oneway = 1`) o igual al `cost` si son bidireccionales.

---

La descripción detallada de cada modelo de dominio (rutas e historial) con sus campos y tipos está en [`MODELOS.md`](./MODELOS.md).

---

*Proyecto académico para la asignatura Taller Aplicado de Programación — Portafolio. Cliente: Grupo Sentte.*
