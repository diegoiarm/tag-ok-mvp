# CHANGELOG

## [0.2.0] - 2026-06-02

### Added
- Sistema de historial de viajes con carga lazy (años → meses → días)
- Vista de calendario mensual con días resaltados según actividad
- Detalle diario con lista de cruces realizados
- Endpoint para consultar detalle de día específico con todos los cruces
- Proyecciones ligeras para optimizar consultas de historial
- Soporte para múltiples tipos de vehículo en todo el flujo
- Dropdown de selección de vehículo en pantalla de planificación
- Mapeo de nombres descriptivos para tipos de vehículo (Auto, Moto, Camioneta, etc.)

### Changed
- Refactorizado el parámetro `vehiculo` de `String` a `TipoVehiculo` en todo el sistema
- Actualizada la API de rutas para usar POST en lugar de GET
- Mejorada la estructura del body en peticiones de ruta para incluir tipo de vehículo
- Optimizada la carga de historial con endpoints separados por nivel de detalle
- Actualizada la UI de planificación para mostrar nombre descriptivo del vehículo

### Fixed
- Corregido error de tipo en navegación al convertir String a TipoVehiculo
- Solucionado problema de importación de Pageable en servicio de historial
- Corregida la estructura del body en peticiones frontend (eliminado anidamiento incorrecto)
- Arreglada la carga lazy de meses en vista de historial

## [0.1.1]

### Changed
- Cambiado el consumo del recurso /rutas para que coincida con lo que espera el backend
- Ahora al momento de planificar se puede seleccionar el tipo de vehículo
- Actualizado el modelo de datos para soportar TipoVehiculo en todas las capas

### Fixed
- Corregida la llamada a la API de rutas para enviar correctamente el tipo de vehículo

## [0.1.0]

### Added
- Consumo básico de los endpoints: precálculo de ruta y lectura de pórticos
- Pantalla de planificación de viaje con mapa interactivo
- Visualización de pórticos en el mapa
- Cálculo de tarifas basado en tipo de vehículo por defecto (AUTO)
- Navegación básica entre pantallas

### Base
- Implementación inicial del sistema de rutas
- Integración con Mapbox para visualización de mapas
- Estructura base del proyecto con arquitectura MVVM
- Consumo de APIs REST para rutas y pórticos