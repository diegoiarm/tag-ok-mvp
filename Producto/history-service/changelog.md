# CHANGELOG

## [0.3.0]

### Added
- Soporte para carga lazy del historial.
- Construcción automática de historial anual, mensual y diario a partir de eventos de cruces.
- Agrupación de cruces por fecha para consolidar información diaria.
- Generación dinámica de documentos históricos por año.

### Changed
- El historial ahora se organiza utilizando la fecha real del cruce en lugar de la fecha de generación del evento.
- Mejorado el proceso de consolidación de cruces para soportar eventos históricos y reprocesamiento de datos.

---

## [0.2.0]

### Added
- Persistencia de historial anual en MongoDB.
- Manejo de eventos Kafka del tópico `portico-cruzado`.
- Exposición de endpoint `getAll` para pruebas y desarrollo.

---

## [0.1.0]

### Base
- Creación inicial del microservicio `history-service`.
- Definición de documentos y snapshots del historial.
- Integración básica con Spring Data MongoDB.