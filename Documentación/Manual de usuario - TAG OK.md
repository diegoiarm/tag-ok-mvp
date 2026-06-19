# Manual de Usuario
## Aplicación Móvil TAG OK

---

## Tabla de contenido

- [Datos del documento](#datos-del-documento)
- [Objetivo](#objetivo)
- [Usuarios involucrados](#usuarios-involucrados)
- [Consideraciones técnicas](#consideraciones-técnicas)
- [Iniciar sesión](#iniciar-sesión)
- [Registro de nuevo usuario](#registro-de-nuevo-usuario)
- [Inicio](#inicio)
- [Mapa](#mapa)
- [Planificar viaje](#planificar-viaje)
- [Historial](#historial)
- [Boleta](#boleta)
- [Verificar factura con IA](#verificar-factura-con-ia)
- [Presupuesto](#presupuesto)
- [Vehículos](#vehículos)
- [Perfil](#perfil)
- [Notificaciones](#notificaciones)
- [Cerrar sesión](#cerrar-sesión)

---

## Datos del documento

### Histórico de revisiones

| Versión | Fecha | Descripción / Cambio | Autor |
|---------|-------|----------------------|-------|
| 1.0 | 19/06/2026 | Versión inicial del manual de usuario | |
| | | | |

### Información del proyecto

| Campo | Detalle |
|-------|---------|
| Organización | |
| Proyecto | TAG OK — Plataforma de seguimiento de gastos en peajes |
| Fecha de inicio | |
| Fecha de término | |
| Docente | |

### Integrantes

| Nombre | Correo |
|--------|--------|
| | |
| | |

---

## Objetivo

Este manual tiene como objetivo guiar al conductor usuario de la aplicación móvil **TAG OK** en el uso correcto y eficiente de la plataforma. La aplicación permite registrar y controlar los gastos de peaje generados en autopistas urbanas de Santiago, Chile, mediante el seguimiento de cruces de pórticos en tiempo real, la generación de boletas y la comparación inteligente con facturas emitidas por las concesionarias.

El manual describe paso a paso las funcionalidades disponibles, incluyendo el registro de cuenta, planificación de rutas, consulta del historial de cruces, gestión del presupuesto y verificación de facturas mediante inteligencia artificial.

---

## Usuarios involucrados

### Conductor

Usuario que conduce un vehículo con TAG y requiere controlar sus gastos en peajes. A través de la aplicación puede:

- Registrarse y acceder a su cuenta.
- Registrar sus vehículos y dispositivos TAG.
- Planificar rutas y consultar el costo estimado de peajes antes de partir.
- Visualizar en el mapa los pórticos de peaje activos en su recorrido.
- Consultar el historial detallado de cruces por fecha, patente y autopista.
- Generar boletas digitales de sus gastos en peajes.
- Comparar su boleta contra la factura de la concesionaria usando inteligencia artificial.
- Configurar un presupuesto mensual con alertas de gasto.
- Gestionar sus vehículos registrados.
- Editar su información personal.

---

## Consideraciones técnicas

- **Permisos requeridos:** Acceso a la ubicación (para el mapa y seguimiento de ruta), acceso a la cámara y galería (para verificación de facturas con IA), notificaciones (para alertas de presupuesto).
- **Conectividad:** Requiere conexión a internet para sincronización con los servicios de rutas, historial y verificación de facturas.
- **Seguridad:** Todas las operaciones utilizan autenticación mediante tokens JWT emitidos por Supabase. Las credenciales nunca se almacenan en texto plano en el dispositivo.
- **Servidor:** La aplicación se comunica con el gateway de servicios TAG OK en `http://10.0.2.2:8080/api` (emulador) o con la dirección del servidor de producción según la configuración del despliegue.

---

## Iniciar sesión

El acceso a la aplicación se realiza mediante la pantalla de inicio de sesión, que se muestra automáticamente al abrir la app si el usuario no tiene una sesión activa.

[CAPTURA: Pantalla de inicio de sesión]

Para ingresar con correo electrónico y contraseña:

1. Ingresar el **correo electrónico** registrado en el campo correspondiente.
2. Ingresar la **contraseña** en el campo de contraseña. Es posible alternar la visibilidad de la contraseña tocando el ícono del ojo.
3. Tocar el botón **"Iniciar sesión"**.

Si las credenciales son correctas, la aplicación redirige automáticamente a la pantalla de inicio. En caso de credenciales inválidas, se muestra un mensaje de error en la parte inferior de la pantalla.

También es posible iniciar sesión con una cuenta de Google tocando el botón **"Continuar con Google"**.

Si el usuario no tiene cuenta, puede acceder al registro tocando el enlace **"Regístrate"** en la parte inferior de la pantalla.

> Si el usuario olvidó su contraseña, puede tocar el enlace **"Olvidé mi contraseña"** para iniciar el proceso de recuperación por correo electrónico.

---

## Registro de nuevo usuario

El proceso de registro se realiza en tres pasos sucesivos.

### Paso 1: Datos personales

[CAPTURA: Paso 1 — Datos personales]

Ingresar la siguiente información:

- **Nombre**
- **Apellidos**
- **Fecha de nacimiento** (seleccionada mediante un selector de calendario)
- **Ciudad**
- **Comuna**

Tocar **"Siguiente"** para avanzar al paso 2.

### Paso 2: Credenciales de acceso

[CAPTURA: Paso 2 — Credenciales]

Ingresar:

- **Correo electrónico**
- **Número celular**
- **Contraseña** (mínimo 8 caracteres)
- **Repetir contraseña** (debe coincidir con la anterior)

Tocar **"Siguiente"** para avanzar al paso 3.

### Paso 3: Vehículo

[CAPTURA: Paso 3 — Vehículo]

Registrar el primer vehículo asociado a la cuenta:

- **Tipo de vehículo:** seleccionar entre Auto, Motocicleta, Camioneta, Bus, Camión o Camión con remolque.
- **Categoría de peaje:** seleccionar la categoría correspondiente (1 a 6 según el tipo de vehículo).
- **Patente:** ingresar la patente en formato nuevo (ABCD-12) o antiguo (AB-1234).
- **Número de TAG:** ingresar el número impreso en el dispositivo TAG (10 a 12 dígitos).

Si se desea registrar más de un vehículo durante el registro, tocar **"Registrar otro vehículo"** antes de guardar.

Tocar **"Guardar"** para completar el registro. La aplicación crea la cuenta y redirige automáticamente a la pantalla de inicio.

---

## Inicio

La pantalla de inicio es el punto de partida de la aplicación. Muestra un resumen del estado actual y accesos directos a las funciones principales.

[CAPTURA: Pantalla de inicio]

### Elementos de la pantalla

- **Saludo y avatar:** en la parte superior se muestra el nombre del usuario y un ícono de notificaciones con un indicador numérico de alertas no leídas.
- **Vehículo activo:** tarjeta que muestra los vehículos registrados como chips seleccionables. El vehículo seleccionado se destaca y se utilizará en las acciones de planificación y seguimiento. También se muestra un chip con el signo "+" para agregar un nuevo vehículo directamente desde esta pantalla.
- **Botones de acción:** dos accesos directos:
  - **Planificar** — abre la pantalla de planificación de viaje.
  - **Historial** — abre el historial de cruces.
- **Mapa interactivo:** ocupa la parte inferior de la pantalla y muestra un mapa centrado en Santiago con los pórticos de peaje marcados. Permite navegar libremente.
- **Botón "Iniciar ruta":** botón flotante sobre el mapa que inicia el modo de ruta activa con el vehículo seleccionado.

Para cambiar el vehículo activo, tocar el chip correspondiente en la tarjeta "Vehículo activo".

---

## Mapa

La pantalla de mapa muestra la ubicación actual del conductor y los pórticos de peaje disponibles en el área visualizada.

[CAPTURA: Pantalla de mapa]

### Controles del mapa

- **Botón de ubicación (ícono de mira):** centra el mapa en la ubicación actual del dispositivo. Al tocarlo por primera vez, la aplicación solicita permiso de acceso a la ubicación.
- **Botón de acercar (+):** aumenta el nivel de zoom del mapa.
- **Botón de alejar (−):** reduce el nivel de zoom del mapa.

### Pórticos de peaje

Los pórticos de peaje se representan como marcadores sobre el mapa. Al tocar un marcador, se despliega un panel en la parte inferior con la información detallada del pórtico: nombre, autopista, tipo de tarifa y valor según el tipo de vehículo seleccionado.

[CAPTURA: Detalle de pórtico en el mapa]

### Selector de vehículo

Si no hay un vehículo seleccionado al abrir la pantalla, se muestra automáticamente un panel para seleccionar el vehículo activo. Esto afecta las tarifas mostradas en los pórticos.

### Panel de seguimiento en tiempo real

En la parte inferior de la pantalla se muestra un panel que indica si el seguimiento de ruta está activo y la última tarifa calculada al cruzar un pórtico.

- **"Iniciar seguimiento":** activa el rastreo de ubicación para detectar cruces automáticamente.
- **"Detener seguimiento":** desactiva el rastreo.

---

## Planificar viaje

La pantalla de planificación permite calcular una ruta entre un origen y un destino, y ver los pórticos de peaje que se cruzarán en el camino junto con el costo estimado.

[CAPTURA: Pantalla de planificación de viaje]

### Cómo planificar una ruta

1. Ingresar el **origen** en el campo de texto correspondiente. A medida que se escribe (desde 3 caracteres), la aplicación sugiere direcciones automáticamente. Tocar la sugerencia deseada para seleccionarla.
2. Ingresar el **destino** de la misma forma.
3. Seleccionar el **tipo de vehículo** mediante el selector disponible en el panel inferior.
4. Tocar el botón **"Calcular ruta"**.

[CAPTURA: Ruta calculada con pórticos marcados]

Una vez calculada la ruta:

- La trayectoria se muestra sobre el mapa.
- Los pórticos que se cruzarán en el recorrido quedan marcados.
- En el panel inferior se muestra el detalle de cada pórtico con su costo estimado según el tipo de vehículo.
- Al tocar un pórtico en la lista, el mapa se desplaza para centrarlo.

El botón **"Usar ejemplo"** carga una ruta de ejemplo predefinida para demostración.

Los controles del mapa (ubicación, zoom, ajustar a la ruta) están disponibles en la esquina superior derecha.

---

## Historial

La pantalla de historial muestra un registro completo de los cruces de pórtico realizados, organizados por año, mes y día, con opciones de filtrado.

[CAPTURA: Pantalla de historial — vista anual]

### Vista anual

Al ingresar al historial, se muestran tarjetas resumidas por año con el total de cruces y el monto gastado. Es posible filtrar por:

- **Patente:** filtrar los registros de un vehículo específico.
- **Autopista:** filtrar por autopista concesionada.
- **Ordenamiento:** ordenar los resultados por fecha o por monto.

Tocar una tarjeta de año para acceder al desglose mensual.

### Vista mensual

[CAPTURA: Vista mensual]

Muestra los meses con actividad registrada para el año seleccionado, con el total de cruces y gasto por mes. Tocar un mes para ver el calendario.

### Calendario mensual

[CAPTURA: Calendario mensual]

Vista de calendario que resalta los días con cruces registrados. Tocar un día para ver el detalle de ese día.

### Detalle del día

[CAPTURA: Detalle del día]

Muestra cada cruce individual del día seleccionado con la siguiente información:

- Nombre del pórtico
- Autopista
- Hora exacta del cruce
- Tipo de tarifa aplicada
- Monto cobrado

Para volver al nivel anterior, usar el botón de retroceso en la barra superior de la pantalla.

---

## Boleta

La pantalla de boleta permite generar un documento de cobro con el resumen de todos los cruces realizados en un período determinado.

[CAPTURA: Pantalla de boleta — formulario]

### Cómo generar una boleta

1. Seleccionar el **vehículo** (patente) en la sección "VEHÍCULO".
2. Definir el **período** de consulta indicando la fecha de inicio y la fecha de término en la sección "PERÍODO".
3. Opcionalmente, seleccionar una o más **autopistas** para filtrar los cruces en la sección "AUTOPISTAS (OPCIONAL)".
4. Tocar el botón **"Generar boleta"**.

[CAPTURA: Boleta generada]

La boleta generada muestra:

- **Total a pagar** en pesos chilenos.
- **Número de transacciones** incluidas.
- **Listado de ítems** con nombre del pórtico, autopista, hora del cruce, tipo de tarifa y valor individual.

Si se desea regenerar la boleta con distintos parámetros, tocar el botón **"Regenerar boleta"**.

### Verificar con IA

Una vez generada la boleta, aparece el botón **"Verificar factura con IA"**. Al tocarlo, la aplicación lleva directamente a la pantalla de comparación con los parámetros de la boleta actual precargados (ver sección [Verificar factura con IA](#verificar-factura-con-ia)).

---

## Verificar factura con IA

Esta funcionalidad permite comparar la boleta generada por TAG OK contra la factura oficial emitida por la concesionaria, utilizando inteligencia artificial para detectar diferencias.

[CAPTURA: Pantalla de verificación — selección de archivo]

La pantalla muestra en la parte superior un resumen del contexto de comparación: patente, período y autopistas seleccionadas.

### Cómo adjuntar la factura

Existen tres formas de adjuntar el documento de la concesionaria:

- **Tomar una foto:** abre la cámara del dispositivo para fotografiar la factura impresa.
- **Subir PDF:** abre el explorador de archivos para seleccionar el PDF descargado desde el portal de la concesionaria.
- **Elegir de la galería:** abre la galería de imágenes para seleccionar una foto previamente guardada.

[CAPTURA: Archivo adjunto listo para comparar]

Una vez seleccionado el archivo, aparece una vista previa del documento adjunto y el botón **"Comparar con IA"**.

### Realizar la comparación

Tocar **"Comparar con IA"**. La aplicación procesa el documento mediante inteligencia artificial, extrae los ítems cobrados en la factura y los compara uno a uno con los registros de TAG OK.

[CAPTURA: Resultado de la comparación]

El resultado muestra:

- Los ítems que **coinciden** entre la factura y la boleta TAG OK.
- Los ítems que **solo aparecen en la factura** (posibles cobros no registrados en la app).
- Los ítems que **solo aparecen en TAG OK** (cruces registrados pero no cobrados en la factura).
- Los ítems con **diferencia de monto** entre ambos documentos.

Para realizar una nueva comparación con otro archivo, tocar el botón **"Nueva comparación"**.

> La lectura del documento se realiza con inteligencia artificial y puede contener errores. Ante diferencias detectadas, siempre verificar directamente con la concesionaria.

---

## Presupuesto

La pantalla de presupuesto permite definir un límite de gasto mensual en peajes y recibir alertas cuando se acerca a ese límite.

[CAPTURA: Pantalla de presupuesto — sin configurar]

### Configurar por primera vez

Si aún no se ha configurado un presupuesto, se muestra una tarjeta con el mensaje "Sin presupuesto configurado". Tocar el botón **"Configurar presupuesto"** para abrir el formulario de configuración.

[CAPTURA: Formulario de configuración de presupuesto]

En el formulario ingresar:

- **Monto máximo mensual (CLP):** el límite de gasto que no se desea superar.
- **Alertas de presupuesto:** activar o desactivar las notificaciones mediante el interruptor. Cuando están activas, se configuran dos umbrales:
  - **Primera alerta:** porcentaje del presupuesto al que se enviará la primera notificación (por ejemplo, al 70%).
  - **Segunda alerta:** porcentaje para la segunda notificación (por ejemplo, al 90%).

Tocar **"Guardar"** para confirmar la configuración.

### Ver el presupuesto activo

[CAPTURA: Pantalla de presupuesto — activo con gráfico]

Una vez configurado, la pantalla muestra:

- **Filtro por vehículo:** chips para ver el gasto global o el de un vehículo específico (por patente). Tocar "Global" para ver el total de todos los vehículos.
- **Tarjeta de presupuesto mensual:** indica el monto máximo configurado, el gasto acumulado en el mes actual y el número de peajes cobrados.
- **Gráfico circular (donut):** muestra visualmente el porcentaje del presupuesto consumido. El color cambia según el nivel:
  - **Verde:** menos del 60% consumido.
  - **Naranja:** entre 60% y 85% consumido.
  - **Rojo:** más del 85% consumido.

Para modificar el presupuesto o sus alertas, tocar el botón **"Editar presupuesto"**.

---

## Vehículos

La pantalla de vehículos permite ver, agregar y eliminar los vehículos registrados en la cuenta.

[CAPTURA: Pantalla de vehículos — listado]

Cada tarjeta de vehículo muestra el tipo, la patente y el alias (si se definió uno). Para eliminar un vehículo, tocar el ícono de papelera en la tarjeta correspondiente y confirmar en el diálogo de confirmación.

### Agregar un nuevo vehículo

Tocar el botón flotante **"+"** en la esquina inferior derecha de la pantalla. Se abre un panel con el formulario de nuevo vehículo.

[CAPTURA: Panel para agregar vehículo]

Ingresar:

- **Tipo de vehículo:** seleccionar entre Automóvil, Motocicleta, Camioneta, Bus, Camión o Camión con remolque.
- **Patente:** ingresar en formato ABCD12 (nuevo) o AB1234 (antiguo). Se convierte automáticamente a mayúsculas.
- **Número de TAG (opcional):** número impreso en el dispositivo TAG.
- **Alias (opcional):** nombre de referencia para identificar fácilmente el vehículo, por ejemplo "Auto del trabajo".

Tocar **"Guardar vehículo"** para registrar el nuevo vehículo. El botón se habilita solo cuando la patente ha sido ingresada.

---

## Perfil

La pantalla de perfil centraliza la información personal del usuario y sus accesos rápidos.

[CAPTURA: Pantalla de perfil — vista de información]

### Información visible

- **Avatar:** círculo con las iniciales del nombre y apellido del usuario.
- **Nombre completo:** mostrado bajo el avatar.
- **Sección "INFORMACIÓN":** tarjeta con teléfono de contacto, correo electrónico y ciudad registrados.
- **Sección "ACCESOS RÁPIDOS":** accesos directos a las pantallas de **Vehículos** y **Mis rutas**.

### Editar información

Tocar el botón **"Editar"** para activar el modo de edición. Se habilitan los campos:

- **Nombre**
- **Apellidos**
- **Teléfono**
- **Ciudad**

El correo electrónico se muestra como referencia pero **no es editable** desde esta pantalla.

[CAPTURA: Pantalla de perfil — modo edición]

Tocar **"Guardar"** para confirmar los cambios o **"Cancelar"** para descartarlos.

---

## Notificaciones

La pantalla de notificaciones muestra el historial de alertas recibidas por el usuario.

[CAPTURA: Pantalla de notificaciones]

Las notificaciones pueden incluir:

- **Alertas de presupuesto:** aviso cuando el gasto mensual supera los umbrales configurados (primera y segunda alerta).

El ícono de campana en la pantalla de inicio muestra un indicador numérico rojo con la cantidad de notificaciones no leídas. Tocar el ícono para acceder a esta pantalla.

---

## Cerrar sesión

La opción de cerrar sesión se encuentra en la parte inferior de la pantalla de **Perfil**.

[CAPTURA: Botón de cerrar sesión en perfil]

Tocar el enlace **"Cerrar sesión"** (en rojo). La aplicación cierra la sesión activa, invalida el token de autenticación y redirige al usuario a la pantalla de inicio de sesión.

Se recomienda cerrar sesión al utilizar la aplicación en dispositivos compartidos para proteger los datos personales y el historial de cruces.

---

*Manual de usuario — TAG OK — Versión 1.0*
