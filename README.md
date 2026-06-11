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

### Gateway-Service - Punto de entrada único al backend
`Java 21 / Spring Boot 3 / Spring Gateway Webflux / Netty` - Puerto `8080`

- Enrruta peticiones a los servicios
- Integra OAuth, con Supabase como servidor, para autenticar peticiones
- Actua como mediador entre peticiones para APIs externas

### routes-service - API de rutas y tarifas
`Java 21 / Spring Boot 3 / PostgreSQL + PostGIS + pgRouting / Tomcat`

- Calcula rutas óptimas en Santiago usando el algoritmo pgr_dijkstra sobre datos OSM (50k+ segmentos de calle).
- Determina qué pórticos TAG cruza una ruta y calcula el costo según calendario tarifario y tipo de vehículo.
- Expone pórticos, autopistas y tarifas mediante una API REST.
- Soporta dos estrategias de cobro: por pórtico cruzado y por tramo recorrido.

### history-service - Servicio de historial
`Java 21 / Spring Boot 3 / MongoDB / Tomcat`

- Persiste el historial de cruces de pórticos por usuario.
- Guarda rutas calculadas con sus detalles de cobro.
- Base de datos documental para flexibilidad en el esquema de historial.

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
| API gateway | Java 21, Spring Boot 3, SpringWebFlux |
| API principal | Java 21, Spring Boot 3, JPA/Hibernate |
| API historial | Java 21, Spring Boot 3, Spring Data MongoDB |
| Base de datos | PostgreSQL + PostGIS + pgRouting |
| Historial | MongoDB |
| Autenticación | Supabase Auth |
| Brocker de eventos | Apache Kafka + Zookeeper |
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

---

El sistema cuenta como punto de entrada el API gateway (gateway-service), las peticiones se envian a este servicio y es quien
está encargado de enrrutarlas.

El servicio expone el puerto 8080, contando con rutas

| /api/routes/** | Servicio de rutas |
| /api/history/** | Servicio de historial |

Además de contar con la documentación generada por Swagger/OpenAPI
url local, del recurso: http://localhost:8080/swagger-ui/index.html

Esta implementación mapea tanto los modelos, rutas de recursos, metodos http y respuestas para cada servicio, funcionando como
documentación del api.

---

## Requisitos previos

- Docker Desktop
- Java 21 + Maven
- Android Studio (para la app móvil)

---

## Instalación y ejecución

El sistema se encuentra bajo docker, cuenta con un archivo docker-compose.yml que gestiona la construcción de imagenes,
para la ejecución dentro de una máquina.

- Lo principal es Tener Docker Desktop abierto y ejecutandose en el equipo, junt con la compatibilidad de virtualización.

- Instalar SDK, Maven para compilar proyectos java.

- Ir a la carpeta ./tag-ok-mvp/Producto, aqui se encuentra el archivo docker-compose.yml, aqui se abren dos aristas.

- -- Inicializar solo el backend: 'docker compose up gateway-service -d'
- -- Inicializar el dashboard + backend: 'docker compose up frontend -d'

- El archivo docker, esta configurado para levantar todas las dependencias.

- El sistema, necesita los datos de las calles de santiago y pórticos. Se encuentran en el repositorio.

- Para la carga de pórticos, se puede hacer desde el Dashboard de administración, cargando todos los archivos json, en ./Producto/porticos, el sistema solo aceptara los que tengan el formato requerido y se hará la carga masiva.

- Para cargar las calles se necesita de osm-importer, lo que realiza este programa, recoge un archivo de exportación en formato JSON, de Open Street Maps, lo formatea a los datos que necesitamos, y los importa a la base de datos del servicio de rutas. Para compliarlo se debe ir a ./Producto/osm-importer, alli:

- -- 'mvn compile' <-- Compila el proyecto
- -- 'mvn dependency:copy-dependencies' <-- Carga las dependencias
- -- 'java -cp "target/classes;target/dependency/*" com.roony.Main' <-- Ejecuta el archivo compilado

- Con estos dos pasos el sistema ya esta listo para empezar a funcionar.


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
