# Modelos de dominio

## routes-service (PostgreSQL + PostGIS)

### Autopista — Representa una carretera concesionada (ej. Vespucio Sur, Costanera Norte)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `nombre` | `String` | Nombre de la autopista (ej. "Vespucio Sur") |
| `codigo` | `String` | Código corto (ej. "VSP") |
| `tipoCobro` | `TipoCobro` | `PORTICO` — cobro por pórtico cruzado / `TRAMO` — cobro por tramo entre pórticos |
| `porticos` | `List<Portico>` | Colección de pórticos que pertenecen a la autopista |
| `tramos` | `List<Tramo>` | Colección de tramos entre pórticos |

### Portico — Pórtico TAG (torre con sensores) en un punto geográfico de la autopista
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `codigo` | `String` | Código del pórtico (ej. "P001") |
| `nombre` | `String` | Nombre descriptivo |
| `sentido` | `String` | Sentido de circulación (ej. "N-S") |
| `latitud` | `double` | Latitud (WGS84) |
| `longitud` | `double` | Longitud (WGS84) |
| `activo` | `Boolean` | Si está operativo |
| `fechaCreacion` | `LocalDateTime` | Fecha de creación |
| `fechaActualizacion` | `LocalDateTime` | Última actualización |
| `autopista` | `Autopista` | Autopista a la que pertenece |
| `reglas` | `List<ReglaTarifaria>` | Reglas tarifarias directas del pórtico |
| `calendario` | `CalendarioTarifario` | Calendario con horarios y tarifas |

### Tramo — Segmento de vía entre dos pórticos (entrada y salida)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `long` | Identificador único |
| `entrada` | `Portico` | Pórtico de entrada al tramo |
| `salida` | `Portico` | Pórtico de salida del tramo |
| `distanciaKm` | `double` | Distancia en kilómetros |
| `area` | `String` | Área o zona |
| `sentido` | `String` | Sentido de circulación |
| `autopista` | `Autopista` | Autopista a la que pertenece |
| `reglas` | `List<ReglaTarifaria>` | Reglas tarifarias del tramo |
| `calendario` | `CalendarioTarifario` | Calendario con horarios y tarifas |

### ReglaTarifaria — Define qué tarifa aplicar según el tipo de vehículo
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `aplicaA` | `List<TipoVehiculo>` | Tipos de vehículo a los que aplica |
| `valores` | `List<ValorTarifa>` | Valores tarifarios según `TipoTarifa` |

### ValorTarifa — Monto concreto de una tarifa para un tipo específico (TBFP, TBP, TS)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `tipoTarifa` | `TipoTarifa` | `TBFP` / `TBP` / `TS` |
| `valor` | `BigDecimal` | Monto en pesos chilenos ($) |

### CalendarioTarifario — Conjunto de reglas temporales que determinan qué tarifa aplicar según día y hora
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `reglas` | `List<ReglaTemporal>` | Reglas temporales |

### ReglaTemporal — Asocia un tipo de tarifa a un tipo de día y sus rangos horarios
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `tipoTarifa` | `TipoTarifa` | `TBFP` / `TBP` / `TS` |
| `tipoDia` | `TipoDia` | `LABORAL` / `SABADO_FESTIVO` / `DOMINGO` |
| `tramos` | `List<RangoHorario>` | Rangos horarios en los que aplica |

### RangoHorario — Intervalo de horas del día (ej. 07:00–11:00)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `horaInicio` | `LocalTime` | HH:mm — inicio del rango |
| `horaFin` | `LocalTime` | HH:mm — fin del rango |

### Cruce (sealed interface) — Representa el paso de un vehículo por un punto de cobro
Implementaciones:
- **`CrucePortico`** — cruce por un pórtico individual (`porticoId`, `codigo`, `nombre`, `autopista`, `latitud`, `longitud`, `tipoTarifa`, `valor`, `horaFechaCruce`)
- **`CruceTramo`** — cruce por un tramo entre dos pórticos (`codigoEntrada`, `codigoSalida`, `entradaId`, `salidaId`, `nombreEntrada`, `nombreSalida`, `latitudEntrada`, `longitudEntrada`, `latitudSalida`, `longitudSalida`, más campos comunes)

### TarifaCalculada — Resultado del cálculo de tarifa: total a pagar más desglose por cruce
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `total` | `BigDecimal` | Suma total de todos los cruces |
| `cruces` | `List<Cruce>` | Lista de cruces calculados |
| `vehiculo` | `TipoVehiculo` | Tipo de vehículo usado en el cálculo |

### EventoUso — Registro de uso del sistema (consulta de ruta o estimación de tarifa)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `tipo` | `TipoEventoUso` | `CONSULTA_RUTA` / `ESTIMACION_TARIFA` |
| `usuarioId` | `String` | ID del usuario (nullable) |
| `fecha` | `LocalDateTime` | Fecha del evento |

### RegistroAuditoria — Traza de cambios administrativos (crear, actualizar, activar/desactivar, etc.)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador único |
| `accion` | `TipoAccion` | `CREAR` / `ACTUALIZAR` / `ACTIVAR` / `DESACTIVAR` / `ELIMINAR` / `CONFIGURAR_TARIFA` / `CARGA_MASIVA` |
| `entidad` | `String` | Nombre de la entidad afectada |
| `entidadId` | `String` | ID de la entidad afectada |
| `descripcion` | `String` | Descripción del cambio |
| `usuarioId` | `String` | ID del usuario que realizó la acción |
| `usuarioEmail` | `String` | Email del usuario |
| `fecha` | `LocalDateTime` | Fecha de la acción |

---

## history-service (MongoDB)

Los documentos están en el paquete `document` (no `domain`). El historial se organiza con un documento anual por usuario que contiene snapshots anidados por mes y día.

### HistorialAnualDocument — Historial completo de un usuario para un año
**Colección:** `historial_anual` — un documento por usuario y año.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `String` | `{usuarioId}-{año}` (ID compuesto) |
| `usuarioId` | `String` | ID del usuario (Supabase `sub`) |
| `año` | `int` | Año del historial |
| `cantidadCruces` | `int` | Total de cruces en el año |
| `totalAño` | `BigDecimal` | Suma total de todos los cruces del año |
| `meses` | `List<HistorialMensualSnapshot>` | Snapshots por mes |

### HistorialMensualSnapshot — Resumen de un mes dentro del historial anual
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `mes` | `int` | Número de mes (1-12) |
| `totalMes` | `BigDecimal` | Suma del mes |
| `cantidadCruces` | `int` | Cantidad de cruces en el mes |
| `dias` | `List<HistorialDiarioSnapshot>` | Snapshots por día |

### HistorialDiarioSnapshot — Resumen de un día con sus cruces individuales
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `fecha` | `LocalDate` | Fecha del día |
| `totalDia` | `BigDecimal` | Suma del día |
| `cantidadCruces` | `int` | Cantidad de cruces del día |
| `cruces` | `List<CruceSnapshot>` | Cruces individuales del día |

### CruceSnapshot — Cruce TAG registrado (pórtico, monto, vehículo, hora)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `codigo` | `String` | Código del pórtico |
| `nombre` | `String` | Nombre del pórtico |
| `autopista` | `String` | Nombre de la autopista |
| `tipoTarifa` | `String` | Tipo de tarifa aplicada |
| `valor` | `BigDecimal` | Monto del cruce |
| `tipoVehiculo` | `String` | Tipo de vehículo |
| `patente` | `String` | Patente del vehículo |
| `horaFechaCruce` | `LocalDateTime` | Fecha y hora del cruce |

### RutaGuardada — Ruta calculada que el usuario guardó para consultar después
**Colección:** `rutas_guardadas`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `String` | Identificador único (MongoDB `_id`) |
| `idToken` | `String` | Identificador del usuario |
| `segments` | `List<Segmento>` | Segmentos GeoJSON de la ruta |
| `porticos` | `List<PorticoRuta>` | Pórticos que cruza la ruta |

### Segmento — Tramo vial individual dentro de la ruta guardada (nombre + geometría GeoJSON)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `name` | `String` | Nombre de la calle o tramo |
| `geometry` | `String` | GeoJSON de la geometría del segmento |

### PorticoRuta — Pórtico TAG incluido en la ruta guardada, con su tarifa estimada
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `nombre` | `String` | Nombre del pórtico |
| `codigo` | `String` | Código del pórtico |
| `autopista` | `String` | Nombre de la autopista |
| `codigoAutopista` | `String` | Código de la autopista |
| `longitud` | `double` | Longitud (WGS84) |
| `latitud` | `double` | Latitud (WGS84) |
| `tipoTarifa` | `String` | Tipo de tarifa |
| `valor` | `double` | Valor del cruce |
| `fechaHora` | `String` | Fecha y hora estimada del cruce |

---

## Enumeraciones compartidas

| Enum | Valores |
|------|---------|
| **TipoCobro** | `PORTICO` — cobro por pórtico individual / `TRAMO` — cobro por tramo recorrido |
| **TipoVehiculo** | `MOTO`, `AUTO`, `CAMIONETA`, `BUS`, `CAMION`, `CAMION_REMOLQUE` |
| **TipoTarifa** | `TBFP` — tarifa básica fuera de punta / `TBP` — tarifa básica punta / `TS` — tarifa saturada |
| **TipoDia** | `LABORAL` — lunes a viernes / `SABADO_FESTIVO` — sábados y festivos / `DOMINGO` — domingos |
| **TipoAccion** | `CREAR`, `ACTUALIZAR`, `ACTIVAR`, `DESACTIVAR`, `ELIMINAR`, `CONFIGURAR_TARIFA`, `CARGA_MASIVA` |
| **TipoEventoUso** | `CONSULTA_RUTA`, `ESTIMACION_TARIFA` |
