# 🚀 Guía de Arranque del Entorno — TAG OK

Esta guía permite a cualquier integrante del equipo levantar el entorno de desarrollo completo desde cero.

---

## ✅ Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:

| Herramienta | Versión mínima | ¿Para qué? |
|---|---|---|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Cualquier reciente | Base de datos PostgreSQL/PostGIS |
| [Java JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) | 21 | Backend Spring Boot |
| [Node.js](https://nodejs.org/) | 18+ | Frontend React (routes-ui) |
| Git | Cualquier | Control de versiones |

---

## 📁 Estructura del proyecto

```
tag-ok-mvp/
└── Producto/
    ├── docker compose.yml     ← Configuración de la BD y pgAdmin
    ├── routes-service/        ← Backend Java Spring Boot (puerto 8000)
    ├── routes-ui/             ← Frontend React/Leaflet (puerto 5173)
    ├── porticos/              ← Datos JSON de los pórticos de las autopistas
    └── sql/                   ← Scripts de inicialización de la base de datos
```

---

## PASO 1 — Clonar el repositorio

```bash
git clone <URL_DEL_REPO>
cd tag-ok-mvp
```

---

## PASO 2 — Levantar la base de datos con Docker

> ⚠️ **Docker Desktop debe estar abierto y corriendo** antes de ejecutar este paso.

```powershell
# Desde la carpeta Producto/
cd Producto
docker compose up -d
```

Verifica que los contenedores estén corriendo:

```powershell
docker ps
```

Deberías ver dos contenedores activos:
- `db-rutas` → PostgreSQL con PostGIS y pgRouting (puerto 5432)
- `pgadmin_container` → pgAdmin, interfaz web para ver la BD (puerto 1000)

### Credenciales de acceso

| Parámetro | Valor |
|---|---|
| Host | `localhost` |
| Puerto | `5432` |
| Base de datos | `db_rutas` |
| Usuario | `admin` |
| Contraseña | `admin` |

**pgAdmin** (interfaz web): http://localhost:1000
- Email: `admin@admin.com`
- Contraseña: `admin`

---

## PASO 3 — Inicializar la base de datos

> ⚠️ **Este paso solo se hace UNA VEZ** (o si el volumen de Docker se pierde/reinicia).
> Si ya tienes datos en la BD, puedes saltarte este paso.

Verifica primero si la BD ya tiene datos:

```powershell
docker exec -i db-rutas psql -U admin -d db_rutas -c "SELECT COUNT(*) FROM edge_vertices_pgr;" 2>$null
```
- Si ves un número mayor a 0: **la BD ya está inicializada, salta al Paso 4**.
- Si ves un error: **debes inicializar la BD con los pasos a continuación**.

### 3a. Crear el esquema de tablas

```powershell
# Desde la carpeta Producto/
Get-Content "sql\script.sql" | docker exec -i db-rutas psql -U admin -d db_rutas
```

### 3b. Cargar los datos de calles (50.000+ edges de Santiago)

> ⏳ Este paso puede tardar **2-5 minutos**. Es normal.

```powershell
Get-Content "sql\calles.sql" | docker exec -i db-rutas psql -U admin -d db_rutas
```

### 3c. Construir la topología de routing

> ⏳ Este paso puede tardar **1-2 minutos**.

```powershell
Get-Content "sql\createTopology.sql" | docker exec -i db-rutas psql -U admin -d db_rutas
```

Al finalizar deberías ver:
```
NOTICE: TOPOLOGY CREATED FOR 50749 edges
```

### 3d. Verificar que todo quedó bien

```powershell
docker exec -i db-rutas psql -U admin -d db_rutas -c "SELECT COUNT(*) FROM edge; SELECT COUNT(*) FROM edge_vertices_pgr;"
```

Resultado esperado:
```
 count
-------
 50749   ← calles cargadas

 count
-------
 56797   ← vértices (intersecciones) de la red vial
```

---

## PASO 4 — Iniciar el backend (routes-service)

```powershell
# Desde la carpeta Producto/routes-service/
cd routes-service
./mvnw spring-boot:run
```

O también puedes abrirlo directamente desde **IntelliJ IDEA** y hacer Run en `RoutesServiceApplication.java`.

El servidor estará disponible en: **http://localhost:8000**

### Endpoints disponibles

| Método | URL | Descripción |
|---|---|---|
| `GET` | `/autopistas` | Lista todas las autopistas con sus pórticos |
| `GET` | `/porticos` | Lista resumida de todos los pórticos |
| `GET` | `/porticos/{id}` | Detalle completo de un pórtico (tarifas + calendario) |
| `GET` | `/api/routes?lon1=&lat1=&lon2=&lat2=` | Calcula la ruta entre dos coordenadas |
| `POST` | `/autopistas` | Registra autopista con pórticos desde un JSON |
| `DELETE` | `/autopistas/{id}` | Elimina una autopista |

---

## PASO 5 — Iniciar el frontend (routes-ui)

```powershell
# Desde la carpeta Producto/routes-ui/
cd routes-ui
npm install     # Solo la primera vez
npm run dev
```

El frontend estará disponible en: **http://localhost:5173**

---

## PASO 6 — Cargar los datos de pórticos (si es la primera vez)

> Los archivos JSON de pórticos están en `Producto/porticos/`.

Usa el endpoint `POST /autopistas` para cargar los datos. Ejemplo con PowerShell:

```powershell
$json = Get-Content "porticos\vespucioNorte.json" -Raw
Invoke-RestMethod -Uri "http://localhost:8000/autopistas" -Method POST -Body $json -ContentType "application/json"
```

Repite para cada archivo de autopista:
```powershell
$json = Get-Content "porticos\autopistaCentral.json" -Raw
Invoke-RestMethod -Uri "http://localhost:8000/autopistas" -Method POST -Body $json -ContentType "application/json"

$json = Get-Content "porticos\vespucioSur.json" -Raw
Invoke-RestMethod -Uri "http://localhost:8000/autopistas" -Method POST -Body $json -ContentType "application/json"

$json = Get-Content "porticos\costaneraNorte.json" -Raw
Invoke-RestMethod -Uri "http://localhost:8000/autopistas" -Method POST -Body $json -ContentType "application/json"
```

---

## 🛑 Apagar el entorno

```powershell
# Detener contenedores (los datos se conservan)
docker compose stop

# Detener Y eliminar contenedores (los datos se conservan en el volumen)
docker compose down

# ⚠️ ELIMINAR TODO incluidos los datos (necesitarás repetir el Paso 3)
docker compose down -v
```

---

## ❓ Problemas comunes

| Error | Causa | Solución |
|---|---|---|
| `Connection to localhost:5432 refused` | Docker no está corriendo | Abrir Docker Desktop y esperar a que inicie |
| `relation "edge_vertices_pgr" does not exist` | BD no inicializada | Ejecutar el Paso 3 completo |
| `npm: command not found` | Node.js no instalado | Instalar Node.js desde nodejs.org |
| Puerto 5432 ya en uso | Otra instancia de PostgreSQL local corriendo | Detener el servicio local de PostgreSQL |
