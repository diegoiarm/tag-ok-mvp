# 🚗 TAG OK - Control y trazabilidad de gastos de peajes

![Estado del Proyecto](https://img.shields.io/badge/Estado-En_Desarrollo_(MVP)-blue)
![Arquitectura](https://img.shields.io/badge/Arquitectura-Microservicios-brightgreen)

**TAG OK** es una solución tecnológica integral diseñada para otorgarle al conductor la capacidad real de planificar, registrar y reaccionar frente a sus gastos por uso de vías concesionadas (TAG) en Santiago de Chile. Desarrollado como un MVP para **Grupo Sentte**, el sistema transforma un gasto históricamente reactivo y "a ciegas" en una decisión consciente y planificada.

## Sobre el proyecto
Actualmente, los conductores urbanos enfrentan incertidumbre financiera y falta de trazabilidad en los gastos de transporte debido a la fragmentación de la información entregada por las distintas concesionarias. 

TAG OK soluciona esta problemática mediante un ecosistema compuesto por una **aplicación móvil** orientada al cliente (B2C) y un **panel web administrativo**, todo conectado a través de una API centralizada en la nube.

## Características principales:
### Módulo de rutas (Java, Springboot, PostgreSQL, PostGIS)
* Información de pórticos del sistema TAG en Santiago.
* Información de calles de Santiago.
* Cálculo de tarifas asociado al cruce de pórticos del sistema TAG.
* Generación de ruta en la ciudad de Santiago, calculando el valor monetario asociado.

### Módulo de historial (Java, Springboot, MongoDB)
* Guarda el historial de cruce de pórticos del sistema TAG.

### Módulo de preferencias (Java, Springboot, PostgreSQL)
* Guarda las preferencias del usuario (Vehículos, Presupuestos, etc)

### 📱 Aplicación móvil (para conductores)
* Visualización de autopistas urbanas y el estado actual de los pórticos TAG a través de un mapa interactivo.
* Cálculo de costos por trayecto, ruta u horario, permitiendo al usuario fijar un presupuesto máximo.
* Carga de viajes y consulta de gastos acumulados asociados a cada vehículo registrado.
* Notificaciones en tiempo real para anticipar cobros y evitar exceder el presupuesto.
* Descarga del historial de gastos en formato Excel o JSON.

### 💻 Panel web (para administradores)
* Gestión completa (ingresar, editar, eliminar) de los pórticos de la ciudad y sus tarifas.
* Administración y visualización de conductores y vehículos registrados.

## Stack tecnológico

El proyecto está construido bajo una arquitectura modular y escalable, utilizando los siguientes lenguajes y herramientas:

* **Frontend móvil:** Kotlin con Jetpack Compose (Android).
* **Backend:** Java con Spring Framework (Arquitectura de microservicios dirigida por eventos).
* **Base de Datos:** PostgreSQL con extensiones espaciales **PostGIS** y **pgRouting** para el motor de mapas y rutas, MongoDB para el servicio de historial.
* **Gestor de eventos:** Apache Kafka

### Infraestructura cloud
El despliegue aprovecha los modelos IaaS, PaaS y SaaS con enfoque en el Tier Gratuito para las 12 semanas de desarrollo:
* **PaaS (Base de Datos):** **Supabase** como base de datos gestionada, soportando PostGIS nativamente.
* **SaaS (Autenticación):** **Supabase Auth** actuando como IDaaS para un registro e inicio de sesión seguro.
* **IaaS (Servidores):** Máquinas virtuales (AWS EC2 / GCP) para alojar los microservicios en Spring Boot.

## Requisitos previos e instalación
*Por definirse.*

## Equipo de desarrollo
Proyecto desarrollado bajo la metodología ágil **Scrum** combinada con **Aprendizaje Basado en Proyectos (ABP)**.

* **Paulina Troncoso** - UX/UI y Desarrolladora Móvil.
* **Ricardo Sánchez** - Desarrollador Backend y Analista de Base de Datos.
* **Diego Rodríguez** - Desarrollador Frontend y QA.

---
*Este proyecto es de carácter académico, desarrollado para la asignatura de Taller Aplicado de Programación (Portafolio).*
