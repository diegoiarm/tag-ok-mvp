# Manual de Usuario
## Panel de Administración Web — TAG OK

---

## Tabla de contenido

- [Datos del documento](#datos-del-documento)
- [Objetivo](#objetivo)
- [Usuarios involucrados](#usuarios-involucrados)
- [Consideraciones técnicas](#consideraciones-técnicas)
- [Iniciar sesión](#iniciar-sesión)
- [Panel de inicio](#panel-de-inicio)
- [Mapa](#mapa)
- [Usuarios](#usuarios)
- [Concesionarios](#concesionarios)
- [Pórticos](#pórticos)
- [Tarifas](#tarifas)
- [Reportes y estadísticas](#reportes-y-estadísticas)
- [Auditoría](#auditoría)
- [Carga masiva de datos](#carga-masiva-de-datos)
- [Cerrar sesión](#cerrar-sesión)

---

## Datos del documento

### Histórico de revisiones

| Versión | Fecha | Descripción / Cambio | Autor |
|---------|-------|----------------------|-------|
| 1.0 | 19/06/2026 | Versión inicial del manual de usuario administrador | |
| | | | |

### Información del proyecto

| Campo | Detalle |
|-------|---------|
| Organización | |
| Proyecto | TAG OK — Panel de Administración Web |
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

Este manual tiene como objetivo guiar al usuario administrador del **Panel de Administración Web de TAG OK** en el uso correcto y eficiente de la plataforma. El sistema permite gestionar las autopistas concesionadas, los pórticos de peaje, las tarifas vigentes, los usuarios registrados en la app móvil y el monitoreo general del sistema.

El manual describe paso a paso cada sección disponible en el panel, incluyendo la configuración de tarifas, la importación masiva de datos, la generación de reportes y el registro de auditoría de cambios.

---

## Usuarios involucrados

### Administrador

Usuario con acceso al panel web de administración. Dependiendo del rol asignado, podrá acceder a distintas secciones del sistema:

| Rol | Acceso |
|-----|--------|
| `super_admin` | Acceso completo a todas las secciones |
| `admin_usuarios` | Solo sección Usuarios |
| `admin_concesionarios` | Solo sección Concesionarios |
| `admin_porticos` | Solo sección Pórticos |
| `admin_tarifas` | Solo sección Tarifas |
| `admin_reportes` | Solo sección Reportes |
| `admin_auditoria` | Solo sección Auditoría |
| `admin_carga_masiva` | Solo sección Carga masiva |

Solo un `super_admin` puede asignar o modificar el rol de otros administradores.

---

## Consideraciones técnicas

- **Navegador:** Se recomienda utilizar un navegador moderno (Chrome, Edge o Firefox en su versión más reciente).
- **Conectividad:** Requiere conexión a internet para comunicarse con los servicios de backend.
- **Autenticación:** El sistema utiliza tokens JWT emitidos por Supabase. La sesión se mantiene activa mientras el navegador permanezca abierto; al cerrarlo, es posible que se requiera volver a iniciar sesión.
- **Resolución:** El panel está optimizado para escritorio. En pantallas pequeñas algunas columnas de las tablas se ocultan automáticamente para mantener la legibilidad.

---

## Iniciar sesión

Al acceder a la URL del panel de administración, si no hay una sesión activa, el sistema redirige automáticamente a la pantalla de inicio de sesión.

[CAPTURA: Pantalla de inicio de sesión]

### Pasos para ingresar

1. Ingresar el **correo electrónico** registrado como administrador.
2. Ingresar la **contraseña**. Es posible alternar la visibilidad del texto tocando el ícono del ojo.
3. Hacer clic en el botón **"Iniciar sesión"**.

Si las credenciales son correctas, el sistema redirige automáticamente al panel de inicio. En caso de error, se muestra un mensaje de alerta indicando el problema.

> Si el usuario ya tiene una sesión activa, el sistema redirige directamente al panel sin mostrar el formulario de inicio de sesión.

---

## Panel de inicio

El panel de inicio ofrece una visión general del estado del sistema, indicadores clave de uso y accesos rápidos a las funciones más frecuentes.

[CAPTURA: Panel de inicio completo]

### Saludo y encabezado

En la parte superior se muestra un saludo personalizado con el nombre del administrador y un ícono que varía según la hora del día (mañana, tarde o noche). A la derecha se encuentra el botón **"Actualizar"** para recargar todos los datos del panel.

### Indicadores operativos

Cuatro tarjetas resumen el estado actual del sistema:

- **Concesionarias:** cantidad total de autopistas concesionadas registradas.
- **Pórticos:** total de pórticos, con desglose de cuántos están activos y cuántos tienen tarifas pendientes de configurar.
- **Usuarios:** total de usuarios de la app móvil, con desglose de usuarios activos y porcentaje de adopción.
- **Gasto en peajes:** monto total acumulado de cruces registrados en el sistema.

[CAPTURA: Tarjetas de indicadores operativos]

### Indicadores de uso del producto

Una segunda fila de tarjetas muestra métricas de actividad:

- **Rutas consultadas:** total histórico y cantidad en los últimos 30 días.
- **Estimaciones de tarifa:** total histórico y cantidad en los últimos 30 días.
- **Cruces registrados:** total de cruces y número de usuarios que los generaron.
- **Vehículos registrados:** total de vehículos asociados a cuentas de la app.

### Gráficos

[CAPTURA: Sección de gráficos]

El panel incluye cuatro gráficos de análisis:

- **Registros de usuarios por mes:** gráfico de área que muestra la evolución acumulada de nuevos registros.
- **Uso de producto por mes:** gráfico de barras que compara rutas consultadas y estimaciones de tarifa mes a mes.
- **Distribución por tipo de vehículo:** gráfico de donut que muestra el porcentaje de cada tipo de vehículo registrado (moto, auto, camioneta, bus, camión, camión con remolque).
- **Actividad de inicio de sesión:** gráfico de barras que agrupa usuarios según la fecha de su último acceso (hoy, últimos 7 días, últimos 30 días, nunca).

### Actividad reciente

Muestra las últimas 6 acciones registradas en el sistema con el usuario que las realizó, la descripción del cambio y la fecha. Hacer clic en **"Ver todo"** redirige a la sección de Auditoría.

### Accesos rápidos

Cuatro botones de acceso directo a las secciones más utilizadas:

- **Gestionar pórticos**
- **Configurar tarifas**
- **Ver reportes**
- **Importar JSONs**

---

## Mapa

La sección de mapa muestra visualmente los pórticos de peaje registrados sobre un mapa interactivo centrado en Santiago.

[CAPTURA: Pantalla de mapa con pórticos]

### Navegación del mapa

El mapa permite desplazarse libremente y ajustar el nivel de zoom con los controles nativos de Leaflet (rueda del ratón o botones +/−).

### Panel de filtros

Hacer clic en el botón **"Filtros"** (esquina superior derecha del mapa) despliega un panel con las siguientes opciones:

- **Buscar:** campo de texto para filtrar por código o nombre de pórtico.
- **Autopista:** desplegable para mostrar solo los pórticos de una autopista específica.
- **Estado:** filtrar entre Todos, Activos e Inactivos.
- **Tarifa:** filtrar entre Todos, Con tarifa y Sin tarifa.

El panel muestra un contador con la cantidad de pórticos visibles según los filtros aplicados. Para restablecer todos los filtros, hacer clic en **"Limpiar"**.

[CAPTURA: Panel de filtros del mapa]

### Marcadores en el mapa

Cada pórtico aparece como un marcador sobre el mapa. Al hacer clic en un marcador se despliega una ventana emergente con la información del pórtico (nombre, autopista, estado y tarifa estimada según tipo de vehículo).

El botón **"Actualizar"** (encabezado de la página) recarga los datos de pórticos y la ruta activa.

---

## Usuarios

La sección de usuarios permite revisar y administrar todas las cuentas registradas en la aplicación móvil TAG OK.

[CAPTURA: Pantalla de usuarios — listado]

### Indicadores

Cuatro tarjetas resumen el estado de los usuarios:

- **Total:** cantidad total de cuentas registradas.
- **Activos:** cuentas habilitadas (badge verde).
- **Inactivos:** cuentas deshabilitadas (badge rojo).
- **Con vehículo:** usuarios que tienen al menos un vehículo registrado.

### Filtros

- **Buscar:** filtra por correo electrónico o patente de vehículo.
- **Estado:** Todos / Activos / Inactivos.
- **Vehículos:** Todos / Con vehículo / Sin vehículo.

### Tabla de usuarios

La tabla muestra por cada usuario: avatar con iniciales, correo electrónico, estado (Activo/Inactivo), rol, vehículos registrados, fecha de registro y último acceso.

Hacer clic en una fila abre el **panel de detalle** de ese usuario.

### Panel de detalle de usuario

[CAPTURA: Panel de detalle de usuario]

Al seleccionar un usuario se despliega un panel lateral con la siguiente información y acciones:

**Información personal:**
- Correo electrónico
- ID de usuario
- Teléfono (si está registrado)
- Fecha de registro
- Último acceso

**Gestión de rol** (solo disponible para `super_admin`):

Desplegable que permite asignar uno de los siguientes roles:

| Opción | Descripción |
|--------|-------------|
| Sin acceso al panel | El usuario no puede acceder al panel de administración |
| `super_admin` | Acceso completo |
| `admin_usuarios` | Solo sección Usuarios |
| `admin_concesionarios` | Solo sección Concesionarios |
| `admin_porticos` | Solo sección Pórticos |
| `admin_tarifas` | Solo sección Tarifas |
| `admin_reportes` | Solo sección Reportes |
| `admin_auditoria` | Solo sección Auditoría |
| `admin_carga_masiva` | Solo sección Carga masiva |

> No es posible modificar el rol de la propia cuenta con la que se ha iniciado sesión.

**Estado de la cuenta:**

Interruptor (switch) para activar o desactivar la cuenta del usuario. Al desactivar una cuenta, se solicita confirmación antes de aplicar el cambio. Una vez confirmado, el usuario no podrá iniciar sesión en la app móvil.

**Vehículos registrados:**

Tabla con los vehículos asociados a la cuenta: patente, tipo de vehículo (con ícono) y categoría de peaje.

---

## Concesionarios

La sección de concesionarios permite gestionar las autopistas concesionadas registradas en el sistema, incluyendo sus pórticos y tramos asociados.

[CAPTURA: Pantalla de concesionarios — listado]

### Indicadores

- **Total:** cantidad de autopistas registradas.
- **Por pórtico:** autopistas con cobro individual por pórtico.
- **Por tramo:** autopistas con cobro por tramo (entrada-salida).
- **Pórticos totales:** suma de pórticos en todas las autopistas.

### Filtros

- **Buscar:** filtra por nombre o código de autopista.
- **Tipo de cobro:** Todos / Por pórtico / Por tramo.

### Tabla de autopistas

Muestra nombre, código, tipo de cobro (badge), cantidad de pórticos y cantidad de tramos. La columna de acciones incluye un botón de eliminar (ícono de papelera rojo).

> Antes de eliminar una autopista, el sistema solicita confirmación. La operación es irreversible.

### Acciones del encabezado

- **Actualizar:** recarga la lista de autopistas.
- **Importar JSON:** abre el panel para cargar un archivo JSON con los datos de una autopista completa.
- **Nueva concesionaria:** abre el formulario para crear una nueva autopista manualmente.

### Crear nueva concesionaria

[CAPTURA: Formulario de nueva concesionaria]

El formulario solicita:

- **Código** (obligatorio, único)
- **Nombre** (obligatorio)
- **Tipo de cobro:** Por pórtico / Por tramo
- **Descripción** (opcional)

Hacer clic en **"Crear"** guarda la autopista. El sistema muestra un mensaje de éxito o error según corresponda.

### Detalle de autopista

[CAPTURA: Panel de detalle de autopista]

Al hacer clic en una fila de la tabla se despliega un panel con:

- Metadatos: código, nombre, tipo de cobro, fechas de creación y última actualización.
- **Tabla de pórticos:** listado de los pórticos asociados con código, nombre, sentido, coordenadas y estado.
- **Tabla de tramos** (si aplica): listado de tramos con entrada, salida, distancia y estado.
- Botón **"Exportar JSON":** descarga el JSON completo de la autopista con todos sus datos.

### Importar autopista desde JSON

[CAPTURA: Panel de importación de JSON]

El panel permite cargar un archivo `.json` con la estructura completa de una autopista (metadatos, pórticos o tramos, y tarifas). El sistema valida el archivo antes de procesarlo e informa el resultado de la importación.

---

## Pórticos

La sección de pórticos permite registrar, editar, activar/desactivar y eliminar los pórticos de peaje de cada autopista.

[CAPTURA: Pantalla de pórticos — listado]

### Indicadores

- **Total:** cantidad total de pórticos registrados.
- **Vigentes:** pórticos activos (badge verde).
- **Inactivos:** pórticos desactivados (badge rojo).
- **Autopistas:** número de autopistas con al menos un pórtico.

### Filtros

- **Buscar:** filtra por nombre, código o autopista.
- **Autopista:** desplegable para filtrar por una autopista específica.
- **Estado:** Todos / Vigentes / Inactivos.

### Tabla de pórticos

Muestra nombre, código, autopista, sentido, coordenadas geográficas y estado. La columna de acciones incluye tres botones por fila:

- **Ícono de encendido/apagado:** activa o desactiva el pórtico instantáneamente (verde = activo, gris = inactivo).
- **Ícono de lápiz (editar):** abre el formulario de edición.
- **Ícono de papelera (eliminar):** solicita confirmación y elimina el pórtico.

### Acciones del encabezado

- **Actualizar:** recarga la lista de pórticos.
- **Carga masiva:** abre el panel de importación masiva de pórticos.
- **Nuevo pórtico:** abre el formulario de creación.

### Crear o editar un pórtico

[CAPTURA: Formulario de pórtico]

El formulario incluye los siguientes campos:

- **Código** (obligatorio, único)
- **Nombre** (obligatorio)
- **Autopista** (desplegable, obligatorio)
- **Sentido** (opcional, ejemplo: "Norte-Sur")
- **Latitud** y **Longitud** (opcionales, para geolocalización en el mapa)
- **Activo:** interruptor para definir si el pórtico está vigente al crearlo

Hacer clic en **"Guardar"** crea o actualiza el pórtico. El sistema confirma la operación con un mensaje de éxito.

---

## Tarifas

La sección de tarifas permite configurar los valores de cobro para cada pórtico o tramo según el tipo de vehículo y el horario.

[CAPTURA: Pantalla de tarifas]

La sección se organiza en dos pestañas:

### Pestaña Pórticos

Muestra el listado de pórticos con cobro directo (autopistas de tipo "Por pórtico"). Para cada pórtico se indica si ya tiene tarifa configurada (badge verde **"Configurada"**) o si está pendiente (badge gris **"Pendiente"**).

**Filtros disponibles:**
- Buscar por nombre de pórtico.
- Filtrar por autopista.

Hacer clic en el botón **"Editar tarifas"** de cualquier fila abre el editor de tarifas para ese pórtico.

### Pestaña Tramos

Muestra el listado de tramos de las autopistas con cobro por recorrido (tipo "Por tramo"). Para cada tramo se muestra la entrada, la salida, la distancia en kilómetros y si tiene tarifa configurada.

**Filtros disponibles:**
- Buscar por nombre de tramo o autopista.

Hacer clic en **"Editar tarifas"** abre el editor de tarifas para ese tramo.

### Editor de tarifas

[CAPTURA: Editor de tarifas por tipo de vehículo]

El editor muestra una tarjeta por cada tipo de vehículo (Moto, Auto, Camioneta, Bus, Camión, Camión con remolque). Dentro de cada tarjeta se ingresan los valores de tarifa según la modalidad del pórtico o tramo:

- **TBFP** (Tarifa Base en Función del Período): valor en pesos chilenos.
- **TBP** (Tarifa Base por Pórtico): disponible en modo "Por pórtico".
- **TS** (Tarifa Seccionada): disponible en modo "Por tramo".

**Reglas de calendario (horario):**

Es posible definir reglas temporales que establecen distintas tarifas según el día y el horario. Se pueden configurar rangos horarios para los tipos de día:

- **Laboral**
- **Sábado / Festivo**
- **Domingo**

Cada regla puede agregar o eliminar rangos horarios con sus valores correspondientes.

Hacer clic en **"Guardar"** actualiza la configuración de tarifas del pórtico o tramo. El sistema confirma con un mensaje de éxito.

---

## Reportes y estadísticas

La sección de reportes centraliza los indicadores de adopción, uso funcional y estado operativo del sistema.

[CAPTURA: Pantalla de reportes]

### Controles de período

En el encabezado se puede seleccionar el rango de tiempo de análisis:

- Últimos 7 días
- Últimos 30 días
- Últimos 90 días
- Todo el período

El botón **"Actualizar"** recarga todos los datos con el período seleccionado.

### Exportar datos

El botón **"Exportar"** permite descargar la información en distintos formatos:

- **Excel (.xlsx):** reporte consolidado con todos los indicadores.
- **Usuarios (CSV):** listado completo de usuarios registrados.
- **Vehículos (CSV):** listado completo de vehículos registrados.

### Adopción de la plataforma

[CAPTURA: Indicadores de adopción]

Cuatro tarjetas resumen el estado de los usuarios:

- **Usuarios registrados:** total de cuentas creadas.
- **Activos:** usuarios habilitados.
- **Inactivos:** usuarios deshabilitados.
- **Con vehículo:** usuarios que tienen al menos un vehículo registrado, con el porcentaje de adopción.

### Uso funcional

Cuatro tarjetas de actividad de la aplicación móvil:

- **Rutas consultadas:** total y cantidad en los últimos 30 días.
- **Estimaciones de tarifa:** total y cantidad en los últimos 30 días.
- **Cruces registrados:** total de cruces con la cantidad de usuarios que los generaron.
- **Gasto en peajes:** monto total acumulado en pesos chilenos.

### Gráficos de análisis

[CAPTURA: Gráficos de reportes]

- **Registros por mes:** evolución mensual y acumulada de nuevos usuarios (gráfico de área).
- **Uso de producto por mes:** rutas consultadas y estimaciones de tarifa comparadas mes a mes (gráfico de barras).
- **Distribución por tipo de vehículo:** proporción de cada tipo de vehículo registrado (gráfico de donut).
- **Actividad de inicio de sesión:** usuarios agrupados por antigüedad de su último acceso — Hoy, Últimos 7 días, Últimos 30 días, Nunca (gráfico de barras).

### Estado operativo

Indicadores del estado de la infraestructura de datos:

- Pórticos activos e inactivos.
- Pórticos con tarifa configurada y cantidad pendiente.
- Número de concesionarias.
- Cambios realizados en los últimos 7 y 30 días.
- Fecha y hora de la última actualización.

---

## Auditoría

La sección de auditoría registra automáticamente todos los cambios administrativos realizados en el sistema: qué se modificó, quién lo hizo y cuándo.

[CAPTURA: Pantalla de auditoría]

### Tabla de auditoría

Cada fila de la tabla corresponde a una acción registrada e incluye:

- **Fecha:** fecha y hora exacta del cambio (formato AAAA-MM-DD HH:mm:ss).
- **Usuario:** correo electrónico del administrador que realizó el cambio. Si el cambio fue automático, se muestra "Sistema".
- **Acción:** tipo de operación realizada, con badge de color:

| Acción | Color |
|--------|-------|
| CREAR | Azul (primario) |
| ACTUALIZAR | Gris secundario |
| ACTIVAR | Gris secundario |
| DESACTIVAR | Contorno gris |
| ELIMINAR | Rojo |
| CONFIGURAR_TARIFA | Gris secundario |
| CARGA_MASIVA | Contorno gris |

- **Entidad:** tipo de elemento afectado (por ejemplo: "Pórtico", "Tarifa", "Usuario").
- **Detalle:** descripción específica del cambio o "—" si no aplica.

### Filtros

- **Acción:** desplegable para filtrar por tipo de acción (Todas, CREAR, ACTUALIZAR, ACTIVAR, DESACTIVAR, ELIMINAR, CONFIGURAR_TARIFA, CARGA_MASIVA).

El botón **"Actualizar"** recarga el registro de auditoría.

> El registro de auditoría es de solo lectura. No es posible editar ni eliminar entradas del historial.

---

## Carga masiva de datos

La sección de carga masiva permite importar grandes volúmenes de datos al sistema desde archivos externos, sin necesidad de ingresar cada registro manualmente.

[CAPTURA: Pantalla de carga masiva]

Existen dos tipos de importación disponibles:

### Importar Concesionarias

Permite cargar el JSON completo de una autopista, incluyendo sus metadatos, pórticos o tramos y las tarifas configuradas.

**Formato:** JSON

**Cómo importar:**

1. Hacer clic en **"Cargar archivo"** dentro de la tarjeta "Concesionarias".
2. En el panel que se abre, seleccionar o arrastrar el archivo `.json`.
3. El sistema valida la estructura del archivo y muestra errores si los hubiera.
4. Confirmar la carga. El sistema informa cuántos elementos fueron importados correctamente.

**Plantillas disponibles:**

El botón **"Plantilla"** ofrece dos formatos de descarga para guiar la preparación del archivo:

- **Cobro por pórtico:** plantilla JSON para autopistas con cobro individual.
- **Cobro por tramo:** plantilla JSON para autopistas con cobro por recorrido entrada-salida.

[CAPTURA: Panel de importación de concesionaria]

### Importar Pórticos

Permite agregar pórticos a autopistas ya existentes en el sistema a partir de un archivo.

**Formatos:** JSON o CSV

**Campos requeridos por cada pórtico:**

| Campo | Descripción |
|-------|-------------|
| `autopistaCodigo` | Código de la autopista a la que pertenece el pórtico |
| `codigo` | Código único del pórtico |
| `nombre` | Nombre del pórtico |
| `sentido` | Dirección del flujo vehicular |
| `latitud` | Latitud geográfica (puede ser nulo) |
| `longitud` | Longitud geográfica (puede ser nulo) |

**Cómo importar:**

1. Hacer clic en **"Cargar archivo"** dentro de la tarjeta "Pórticos".
2. Seleccionar o arrastrar el archivo `.json` o `.csv`.
3. El sistema valida cada fila del archivo e informa errores de formato o datos inválidos antes de procesar.
4. Confirmar la carga. Se muestra un resumen con la cantidad de pórticos creados y los que presentaron errores.

**Plantillas disponibles:**

- **JSON:** plantilla con la estructura de objeto esperada.
- **CSV:** plantilla con los encabezados de columnas requeridos.

[CAPTURA: Panel de importación de pórticos con resultado]

---

## Cerrar sesión

La opción para cerrar sesión se encuentra en la parte inferior de la barra de navegación lateral, dentro del menú de usuario.

[CAPTURA: Menú de usuario en la barra lateral]

**Pasos:**

1. Hacer clic en el nombre o avatar del usuario en la esquina inferior izquierda de la barra lateral.
2. En el menú desplegable que aparece, seleccionar **"Cerrar sesión"**.

El sistema invalida la sesión activa y redirige a la pantalla de inicio de sesión.

> Se recomienda cerrar sesión siempre que se utilice el panel en un equipo compartido o de acceso público.

---

## Navegación general

El panel utiliza una **barra lateral** (sidebar) que puede contraerse para ganar espacio en pantalla.

[CAPTURA: Barra lateral expandida]

### Secciones de la barra lateral

**General:**
- **Inicio** — panel de indicadores y resumen.
- **Mapa** — visualización geográfica de pórticos.

**Administración** (visible según el rol del usuario):
- Usuarios
- Concesionarios
- Pórticos
- Tarifas
- Reportes
- Auditoría
- Carga masiva

### Colapsar la barra lateral

Hacer clic en el botón de colapso reduce la barra lateral a solo íconos, lo que amplía el área de trabajo. Al pasar el cursor sobre un ícono colapsado, aparece un tooltip con el nombre de la sección.

[CAPTURA: Barra lateral colapsada con tooltip]

---

*Manual de usuario — Panel de Administración Web TAG OK — Versión 1.0*
