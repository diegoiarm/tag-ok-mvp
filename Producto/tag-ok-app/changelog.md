# CHANGELOG

## [0.2.0] - 2026-06-02

### Added
- Sistema de historial de viajes con carga progresiva (años → meses → calendario → detalle diario)
- Vista de calendario mensual interactivo con días resaltados según actividad
- Degradado de color en calendario basado en intensidad de uso (blanco a azul intenso)
- Leyenda visual de actividad en vista de calendario
- Detalle diario con lista completa de cruces, tarifas y horarios
- Iconos contextuales por tipo de vehículo en historial (auto, moto, bus, camión)
- Resumen total con métricas globales (cruces totales, gasto acumulado, años activos)
- Indicador de "Más activo" para años con mayor cantidad de cruces
- Endpoint REST para consultar detalle de día específico con todos los cruces
- Proyecciones MongoDB para optimizar consultas de historial (carga lazy)
- Endpoints separados por nivel de granularidad: `/years`, `/resumen`, `/year/{año}`, `/year/{año}/month/{mes}`, `/year/{año}/month/{mes}/day/{dia}`
- Soporte para múltiples tipos de vehículo en todo el flujo (Auto, Moto, Camioneta, Bus, Camión, Camión con Remolque)
- Dropdown de selección de vehículo en pantalla de planificación
- Mapeo de nombres descriptivos para tipos de vehículo (displayName)
- Animaciones de entrada con escala y fade en vistas de historial
- Transiciones animadas entre niveles de navegación del historial
- Formateo inteligente de moneda (K, M) para mejor legibilidad
- Estados de UI: loading, empty, error con reintento
- Resaltado del día actual en el calendario

### Changed
- Refactorizado el parámetro `vehiculo` de `String` a `TipoVehiculo` en todo el sistema (backend + Android)
- Actualizada la API de rutas: migrado de GET con query params a POST con body estructurado
- Mejorada la estructura del body en peticiones de ruta para incluir tipo de vehículo
- Optimizada la carga de historial con endpoints separados por nivel de detalle
- Actualizada la UI de planificación para mostrar nombre descriptivo del vehículo
- Eliminado uso de `BigDecimal` en cliente Android, reemplazado por `Double` nativo
- Mejorado el manejo de nulos en UI de historial (eliminados operadores `!!`)
- Actualizada la navegación para convertir String a TipoVehiculo de forma segura

### Fixed
- Corregido error de tipo en navegación al convertir String a TipoVehiculo
- Solucionado problema de importación de Pageable en servicio de historial
- Corregida la estructura del body en peticiones frontend (eliminado anidamiento `body: {}`)
- Arreglada la carga lazy de meses en vista de historial (error de rango en `items()`)
- Solucionado `NullPointerException` al cambiar entre vistas en historial
- Corregida la animación de meses (parámetro `delayMillis` en `spring()`)
- Ajustado el formateo de fechas y horas en detalle de cruces

## [0.1.1]

### Changed
- Cambiado el consumo del recurso `/rutas` para coincidir con el endpoint `@PostMapping`
- Ahora al momento de planificar se puede seleccionar el tipo de vehículo
- Actualizado el modelo de datos para soportar TipoVehiculo en todas las capas

### Fixed
- Corregida la llamada a la API de rutas para enviar correctamente el tipo de vehículo
- Solucionado el problema de body anidado en peticiones HTTP

## [0.1.0]

### Added
- Consumo básico de los endpoints: precálculo de ruta y lectura de pórticos
- Pantalla de planificación de viaje con mapa interactivo (Mapbox)
- Visualización de pórticos en el mapa
- Cálculo de tarifas basado en tipo de vehículo por defecto (AUTO)
- Navegación básica entre pantallas (planificación, mapa, pórticos)

### Base
- Implementación inicial del sistema de rutas
- Integración con Mapbox para visualización de mapas
- Estructura base del proyecto con arquitectura MVVM
- Consumo de APIs REST para rutas y pórticos
- Configuración de Ktor como cliente HTTP
- Sistema de navegación con Jetpack Navigation Compose