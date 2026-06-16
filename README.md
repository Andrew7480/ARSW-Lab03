# ARSW - Lab 03: Protocolos de Comunicación Distribuida

Este repositorio contiene la solución del Laboratorio 3 del curso ARSW, donde se exploraron distintos protocolos y mecanismos de comunicación entre procesos distribuidos en Java, partiendo de sockets TCP hasta llegar a gRPC con múltiples microservicios.

---

## Estructura del Proyecto

```
ARSW-Lab03/
├── Parte1/classroomtcp/       # Servidor de salones via TCP (Sockets)
├── Parte2/classroomhttp/      # Servidor de salones via HTTP
├── Parte3/labinventoryRPC/    # Inventario de laboratorio via Java RMI
├── Parte4/appointment-grpc/   # Citas de bienestar via gRPC (un servicio)
├── Parte5/appointment-grpc/   # Sistema de bienestar con múltiples servidores gRPC
├── Parte6/appointment-grpc/   # Versión optimizada con llamadas paralelas (eficiencia)
├── movieExample1/             # Ejemplo base: servidor TCP de películas
├── movieExample2/             # Ejemplo: películas con Java RMI
├── movieExample3/             # Ejemplo: películas con gRPC básico
├── movieExample4/             # Ejemplo: múltiples servicios gRPC (movie + reviews + recommendations)
└── movieExample5/             # Ejemplo: gateway agregador de microservicios gRPC
```

---

## Parte 1 — Servidor TCP de Salones (`Parte1/classroomtcp`)

**Protocolo:** Java Sockets (TCP) en puerto `35000`

Se implementó un servidor y cliente TCP para gestionar el estado de salones de clase. El servidor recibe comandos en texto plano con el formato `ACCION,CODIGO_SALON` y responde con el resultado de la operación.

**Operaciones soportadas:**
- `CONSULTAR_SALON,<codigo>` — devuelve el estado actual del salón.
- `RESERVAR_SALON,<codigo>` — reserva el salón si está disponible.
- `LIBERAR_SALON,<codigo>` — libera el salón si estaba reservado.

**Archivos clave:**
- [ClassroomServer.java](Parte1/classroomtcp/src/ClassroomServer.java)
- [ClassroomClient.java](Parte1/classroomtcp/src/ClassroomClient.java)
- [ClassroomRepository.java](Parte1/classroomtcp/src/ClassroomRepository.java)

---

## Parte 2 — Servidor HTTP de Salones (`Parte2/classroomhttp`)

**Protocolo:** HTTP en puerto `8080` usando `com.sun.net.httpserver`

Se migró la lógica de la Parte 1 a un servidor HTTP con tres endpoints REST:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET`  | `/rooms?id=<codigo>` | Consulta el estado de un salón |
| `POST` | `/rooms/reserve?id=<codigo>` | Reserva un salón |
| `POST` | `/rooms/release?id=<codigo>` | Libera un salón |

**Archivos clave:**
- [ClassroomHttpServer.java](Parte2/classroomhttp/src/ClassroomHttpServer.java)

---

## Parte 3 — Inventario de Laboratorio con Java RMI (`Parte3/labinventoryRPC`)

**Protocolo:** Java RMI (Remote Method Invocation) en puerto `23000`

Se implementó un sistema de inventario de equipos de laboratorio usando RMI, permitiendo invocar métodos remotos como si fueran llamadas locales.

**Operaciones del servicio:**
- `consultarEquipos()` — lista todos los equipos registrados.
- `getEquipment(codigo)` — obtiene un equipo por código.
- `reservarEquipo(codigo)` — reserva un equipo disponible.
- `liberarEquipo(codigo)` — libera un equipo reservado.
- `isDisponible(codigo)` — verifica disponibilidad.

**Equipos precargados:** Microscopio (LAB1), Centrífuga (LAB2), Espectrofotómetro (LAB3).

**Archivos clave:**
- [LabRmiServer.java](Parte3/labinventoryRPC/src/LabRmiServer.java)
- [LabRmiClient.java](Parte3/labinventoryRPC/src/LabRmiClient.java)
- [LabInventoryServiceImpl.java](Parte3/labinventoryRPC/src/LabInventoryServiceImpl.java)

---

## Parte 4 — Citas de Bienestar con gRPC (`Parte4/appointment-grpc`)

**Protocolo:** gRPC en puerto `50051`

Se implementó un servidor gRPC para el sistema de citas de la oficina de bienestar universitario. El contrato del servicio se define en un archivo `.proto`.

**Operaciones del servicio:**
- `requestAppointment` — solicita una nueva cita.
- `cancelAppointment` — cancela una cita existente.
- `getAppointments` — consulta las citas activas de un estudiante.

**Tipos de servicio disponibles:** `DENTISTRY`, `PSYCHOLOGY`, entre otros.

**Archivos clave:**
- [AppointmentGrpcServer.java](Parte4/appointment-grpc/src/main/java/edu/eci/arsw/welfare/AppointmentGrpcServer.java)
- [AppointmentGrpcClient.java](Parte4/appointment-grpc/src/main/java/edu/eci/arsw/welfare/AppointmentGrpcClient.java)

---

## Parte 5 — Sistema de Bienestar con Múltiples Microservicios gRPC (`Parte5/appointment-grpc`)

Se extendió la Parte 4 para separar los servicios de bienestar en **cuatro microservicios gRPC independientes**:

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `AppointmentGrpcServer` | `50051` | Gestión de citas médicas |
| `MedicalGrpcServer` | `50052` | Consulta de especialidades médicas |
| `GymGrpcServer` | `50053` | Reservas de sesiones de gimnasio |
| `RecreationGrpcServer` | `50054` | Reservas de recursos recreativos |

El cliente `WelfareClient` se conecta a los cuatro servicios de forma **secuencial** y consolida la información.

**Archivos clave:**
- [WelfareClient.java](Parte5/appointment-grpc/src/main/java/edu/eci/arsw/welfare/WelfareClient.java)
- [AppointmentGrpcServer1.java](Parte5/appointment-grpc/src/main/java/edu/eci/arsw/welfare/appointment/AppointmentGrpcServer1.java)
- [MedicalGrpcServer.java](Parte5/appointment-grpc/src/main/java/edu/eci/arsw/welfare/medical/MedicalGrpcServer.java)
- [GymGrpcServer.java](Parte5/appointment-grpc/src/main/java/edu/eci/arsw/welfare/gym/GymGrpcServer.java)
- [RecreationGrpcServer.java](Parte5/appointment-grpc/src/main/java/edu/eci/arsw/welfare/recreation/RecreationGrpcServer.java)

---

## Parte 6 — Optimización con Llamadas Paralelas (`Parte6/appointment-grpc`)

Se mejoró la arquitectura de la Parte 5 optimizando el cliente para realizar las **llamadas a los cuatro microservicios en paralelo** (usando `CompletableFuture` o hilos concurrentes), reduciendo el tiempo de respuesta total al eliminar la espera secuencial entre servicios.

La misma arquitectura de cuatro servidores gRPC se mantiene, pero el cliente aprovecha la concurrencia para consultarlos simultáneamente.

**Archivos clave:**
- [WelfareClient.java](Parte6/appointment-grpc/src/main/java/edu/eci/arsw/welfare/WelfareClient.java)

---

## Tecnologías y Conceptos

| Tecnología | Parte |
|------------|-------|
| Java Sockets (TCP) | Parte 1, movieExample1 |
| HTTP con `com.sun.net.httpserver` | Parte 2, movieExample1 |
| Java RMI | Parte 3, movieExample2 |
| gRPC + Protocol Buffers | Partes 4-6, movieExamples 3-5 |
| Concurrencia / Paralelismo | Parte 6 |

---

## Progresión de Ejemplos (`movieExample*`)

Los ejemplos con películas acompañan cada parte del laboratorio como guía de referencia:

- **movieExample1** — Servidor TCP + HTTP de películas.
- **movieExample2** — Servicio de películas con Java RMI.
- **movieExample3** — Servicio gRPC básico de películas.
- **movieExample4** — Tres microservicios gRPC independientes: películas, reseñas y recomendaciones.
- **movieExample5** — Patrón Gateway que agrega los tres microservicios gRPC anteriores en un único punto de acceso.

