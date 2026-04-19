### Portico

Representa un punto de cobro (TAG) dentro de una autopista.

Es el centro del dominio de tarifas, responsable de:

- agrupar reglas tarifarias
- definir su calendario de aplicación
- mantener consistencia entre entidades relacionadas

#### Atributos relevantes

- codigo: identificador del pórtico
- sentido: dirección del flujo (ej: NS (Norte-Sur), PO (Poniente-Oriente))
- latitud / longitud: ubicación geográfica

#### Relaciones

- Tiene muchas `ReglaTarifaria`
- Tiene un `CalendarioTarifario`
- Pertenece a una `Autopista`