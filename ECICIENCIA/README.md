# Plataforma ECICIENCIA — Diseño Arquitectónico

## Descripción

Diseño de la arquitectura distribuida para la plataforma de gestión del evento ECICIENCIA de la Escuela. El sistema permite registrar asistentes, consultar la agenda, reservar cupos en talleres y controlar el aforo de cada actividad.

---

## 1. Microservicios identificados

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| `AttendeeService` | 50061 | Registro y consulta de asistentes |
| `AgendaService` | 50062 | Catálogo de actividades y consulta por franja horaria |
| `WorkshopService` | 50063 | Reserva de cupos, control de aforo y cancelaciones |
| `ECICIENCIAGateway` | — | Punto de entrada único para el cliente |

### Responsabilidad y datos propios de cada servicio

**AttendeeService**
- Registra nuevos asistentes con nombre, correo institucional y tipo (estudiante / docente / externo).
- Datos propios: `Attendee { id, name, email, type, registrationDate }`.

**AgendaService**
- Expone el catálogo completo de actividades del evento.
- Permite filtrar por franja horaria (`timeSlot`) para que el asistente pueda planear su día.
- Datos propios: `Activity { id, title, speaker, timeSlot, location, type }`.
- No gestiona reservas; solo describe qué ocurre y cuándo.

**WorkshopService**
- Gestiona la disponibilidad de cupos para talleres y charlas con aforo limitado.
- Al reservar, verifica que haya cupos disponibles antes de confirmar.
- Al cancelar, libera el cupo inmediatamente.
- Datos propios: `Workshop { id, totalSpots, takenSpots }`, `Reservation { id, attendeeId, workshopId }`.

**ECICIENCIAGateway**
- Único punto de entrada para la app o página web del evento.
- Orquesta llamadas hacia los servicios internos en operaciones compuestas como `registerAndGetAgenda` o `reserveWithCapacityCheck`.
- El cliente no conoce los puertos ni contratos internos.

---

## 2. Diagrama de arquitectura

```
                   Cliente (App / Web)
                          |
                 ECICIENCIAGateway
                  /       |       \
                 /        |        \
          :50061       :50062       :50063
             |             |            |
     AttendeeService  AgendaService  WorkshopService
     (registro)       (agenda,       (cupos, aforo,
                       franjas)       reservas)
```

Cada servicio es un proceso independiente con su propio estado en memoria (o base de datos en producción). El gateway mantiene un canal gRPC hacia cada uno.

---

## 3. Contratos gRPC propuestos

### WorkshopService — [`proto/workshop.proto`](proto/workshop.proto)

Es el contrato más completo porque centraliza las dos funcionalidades más críticas del evento: reservas y control de aforo.

| RPC | Request | Response | Descripción |
|---|---|---|---|
| `GetWorkshops` | `WorkshopListRequest` | `WorkshopList` | Lista todos los talleres con ocupación actual |
| `GetCapacity` | `WorkshopIdRequest` | `CapacityResponse` | Cupos totales, tomados y disponibles de un taller |
| `ReserveSpot` | `ReservationRequest` | `ReservationResponse` | Reserva un cupo; retorna error si el taller está lleno |
| `CancelReservation` | `CancelRequest` | `CancelResponse` | Cancela una reserva y libera el cupo |
| `GetReservations` | `AttendeeIdRequest` | `ReservationList` | Talleres reservados por un asistente |

### AttendeeService — contrato sugerido

```proto
service AttendeeService {
  rpc RegisterAttendee (AttendeeRequest)  returns (AttendeeResponse);
  rpc GetAttendee      (AttendeeIdRequest) returns (Attendee);
}
```

### AgendaService — contrato sugerido

```proto
service AgendaService {
  rpc GetActivities          (AgendaRequest)    returns (ActivityList);
  rpc GetActivitiesByTimeSlot (TimeSlotRequest) returns (ActivityList);
}
```

---

## 4. Descripción del Gateway

El `ECICIENCIAGateway` expone operaciones de negocio que ocultan la complejidad interna:

| Operación del Gateway | Servicios invocados | Descripción |
|---|---|---|
| `registerAttendee(data)` | `AttendeeService` | Registra un nuevo asistente |
| `getFullAgenda(timeSlot?)` | `AgendaService` | Devuelve agenda completa o filtrada por franja |
| `reserveWorkshop(attendeeId, workshopId)` | `WorkshopService` | Verifica aforo y reserva en una sola llamada |
| `getMySchedule(attendeeId)` | `WorkshopService` + `AgendaService` | Reservas del asistente enriquecidas con datos de agenda |
| `getCapacityOverview()` | `WorkshopService` | Estado de aforo de todos los talleres |

El gateway también es el lugar donde se añadirían en el futuro: autenticación (validar el token del asistente antes de enrutar), rate limiting (evitar que un asistente intente reservar masivamente) y manejo de fallos parciales (si `AgendaService` cae, las reservas siguen funcionando).

---

## 5. Justificación: ¿por qué no un monolito?

Un único servicio que manejara registro, agenda y reservas tendría tres razones de cambio mezcladas:

- **Registro de asistentes** cambia cuando varía el proceso de inscripción al evento (campos requeridos, validaciones).
- **Agenda** cambia cuando se agregan, modifican o cancelan actividades, lo que puede ocurrir muchas veces el mismo día del evento.
- **Reservas y aforo** son operaciones de alta concurrencia: cuando abre la convocatoria, muchos usuarios intentan reservar simultáneamente. Este componente necesita escalar horizontalmente de forma independiente.

Con un monolito, escalar las reservas significa escalar todo el sistema (incluyendo el registro y la agenda que tienen carga baja). Con microservicios, solo `WorkshopService` necesita múltiples instancias durante los picos de demanda.

---

## 6. Reflexión final — Evolución arquitectónica del taller

A lo largo del taller se recorrieron seis estilos arquitectónicos, cada uno respondiendo a una limitación visible del anterior.

**TCP (Parte 1)** mostró la comunicación en su forma más cruda: para transmitir una operación hay que diseñar el protocolo desde cero, definir el separador, los comandos válidos y las respuestas posibles. El contrato vive en convenciones de texto entre dos archivos Java y no existe ningún mecanismo que detecte incompatibilidades antes de ejecutar el sistema.

**HTTP (Parte 2)** resolvió la necesidad de interoperabilidad: al adoptar un protocolo estándar, el servicio pasó a ser consumible desde cualquier herramienta que entienda HTTP sin importar el lenguaje. Sin embargo, construirlo sin framework expuso cuánto trabajo rutinario (parseo de rutas, extracción de parámetros, manejo de errores) hace invisible un framework moderno.

**RMI (Parte 3)** eliminó el diseño manual del formato de mensajes: el cliente llama a métodos Java sobre un objeto remoto como si fuera local. El contrato pasó a estar en código tipado y verificable en tiempo de compilación. El costo fue el acoplamiento total al ecosistema JVM, que dejó de ser aceptable en un mundo donde los sistemas distribuidos mezclan lenguajes.

**gRPC (Parte 4)** combinó lo mejor de RMI y HTTP: invocación de métodos con contratos formales en `.proto`, serialización eficiente con Protocol Buffers y soporte en más de diez lenguajes sobre HTTP/2. El archivo `.proto` se convirtió en la fuente de verdad del sistema — no en un archivo de documentación sino en el artefacto desde el que se genera el código de cliente y servidor.

**Microservicios (Parte 5)** evidenció que cambiar el protocolo no es suficiente si toda la lógica sigue concentrada en un único proceso. Separar por dominio de negocio permitió que cada servicio cambiara, escalara y se desplegara de forma independiente. Pero la separación trasladó el problema al cliente: ahora era el cliente quien debía conocer todos los servicios.

**API Gateway (Parte 6)** resolvió ese acoplamiento centralizando el conocimiento de la topología en un único punto. El cliente habla con un solo componente que orquesta los servicios internos. El nuevo problema que emergió fue que el gateway puede volverse un punto de falla único o acumular lógica de negocio que no le pertenece.

La plataforma **ECICIENCIA** aplica las lecciones de todas estas etapas: tres microservicios con responsabilidades claras, contratos formales en `.proto`, y un gateway que expone operaciones de negocio compuestas sin exponer la topología interna. El recorrido del taller demuestra que las decisiones arquitectónicas no son elecciones estéticas sino respuestas concretas a problemas concretos — y que cada solución trae consigo las semillas de la siguiente limitación a resolver.
