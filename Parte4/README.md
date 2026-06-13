# Sistema de Bienestar Universitario — gRPC

## Resumen

Se implementó un servicio gRPC en Java para gestionar citas de bienestar universitario. El contrato del servicio está definido en el archivo `appointment.proto` usando Protocol Buffers; a partir de él, Maven genera automáticamente las clases Java para mensajes y el stub del servicio. El servidor corre en el puerto 50051 y el cliente lo consume usando un blocking stub.

A diferencia de los ejercicios anteriores (TCP, HTTP, RMI), el protocolo no es texto ni depende del ecosistema Java: cualquier lenguaje con soporte gRPC puede consumir el mismo servicio a partir del `.proto`.

### Estructura del proyecto

```
appointment-grpc/
├── src/main/proto/appointment.proto          ← contrato del servicio
├── src/main/java/.../AppointmentGrpcServer   ← servidor gRPC
├── src/main/java/.../AppointmentGrpcClient   ← cliente de demostración
└── pom.xml                                   ← build con generación de código proto
```

Las clases Java bajo `target/generated-sources/` son generadas automáticamente por el plugin `protobuf-maven-plugin`; no se editan manualmente.

### Entidades definidas en el proto

| Entidad | Campos |
|---|---|
| `Student` | `id`, `name`, `institutionalEmail` |
| `Appointment` | `appointmentId`, `studentId`, `serviceType`, `date`, `status` |
| `ServiceType` (enum) | `MEDICINE`, `PSYCHOLOGY`, `DENTISTRY` |
| `Status` (enum) | `REQUESTED`, `CANCELLED`, `ATTENDED` |

### Servicio definido en el proto

| RPC | Request | Response | Descripción |
|---|---|---|---|
| `RequestAppointment` | `AppointmentRequest` | `AppointmentResponse` | Crea una cita en estado `REQUESTED` |
| `CancelAppointment` | `CancelRequest` | `CancelResponse` | Cambia el estado de la cita a `CANCELLED` |
| `GetAppointments` | `StudentRequest` | `AppointmentList` | Retorna las citas activas de un estudiante (excluye canceladas) |

### Cómo ejecutar

Desde `Parte4/appointment-grpc/`:

```powershell
mvn compile
```

```powershell
# Terminal 1 — servidor gRPC (puerto 50051)
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.welfare.AppointmentGrpcServer'
```

```powershell
# Terminal 2 — cliente
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.welfare.AppointmentGrpcClient'
```

### Cumplimiento de requisitos

| Requisito | Estado |
|---|---|
| `rpc RequestAppointment` | Cumplido — cita queda en estado `REQUESTED` |
| `rpc CancelAppointment` | Cumplido — cambia estado a `CANCELLED` |
| `rpc GetAppointments` | Cumplido — filtra citas canceladas |
| Entidad `Student` (id, name, email) | Cumplido — definida en proto |
| Entidad `Appointment` (id, studentId, serviceType, date, status) | Cumplido |
| `ServiceType`: MEDICINE, PSYCHOLOGY, DENTISTRY | Cumplido |
| `Status`: REQUESTED, CANCELLED, ATTENDED | Cumplido |
| Información en memoria | Cumplido — `HashMap` en el servidor |
| Citas canceladas no aparecen como activas | Cumplido — filtradas en `GetAppointments` |

---

## Reflexión y conclusiones

### ¿Por qué el archivo `.proto` se considera un contrato?

Porque define de forma explícita, tipada y versionable todos los elementos del protocolo: los mensajes con sus campos y tipos, los enums con sus valores posibles, y los métodos del servicio con sus entradas y salidas. A diferencia del protocolo TCP (donde el contrato era una convención de texto entre dos archivos Java) o de HTTP (donde el contrato era implícito en las rutas), el `.proto` es la fuente de verdad desde la que se genera el código tanto del servidor como del cliente. Si el contrato cambia, el código generado cambia y las incompatibilidades se detectan en tiempo de compilación.

### ¿Qué tan fácil sería crear un cliente en otro lenguaje?

Muy fácil comparado con RMI. Basta con tomar el archivo `appointment.proto` y compilarlo con el generador de código para el lenguaje destino (`protoc` con el plugin correspondiente). gRPC tiene soporte oficial para Python, Go, C++, C#, JavaScript/TypeScript, Kotlin, entre otros. El cliente resultante usa exactamente las mismas definiciones de mensajes y métodos, sin necesidad de adaptar ningún protocolo manualmente.

### ¿Qué diferencias encuentra entre RMI y gRPC?

| Aspecto | RMI | gRPC |
|---|---|---|
| Lenguaje | Solo Java/JVM | Agnóstico — soporte en +10 lenguajes |
| Contrato | Interfaz Java (`Remote`) | Archivo `.proto` (Protocol Buffers) |
| Serialización | Serialización binaria de Java | Protocol Buffers (más compacto y rápido) |
| Interoperabilidad | Ninguna fuera de JVM | Total entre lenguajes compatibles con gRPC |
| Detección de errores | En tiempo de compilación (Java) | En tiempo de compilación (desde el proto) |
| Transporte | JRMP (protocolo propio de Java) | HTTP/2 (estándar) |
| Uso actual | Legado — raro en sistemas modernos | Ampliamente usado en microservicios |

---

## Conclusiones

gRPC cierra el ciclo de evolución recorrido en el taller: combina la expresividad de las llamadas a métodos de RMI con la interoperabilidad de HTTP, y reemplaza los contratos implícitos de TCP con un archivo `.proto` formal que es a la vez documentación, especificación y fuente de generación de código.

El archivo `appointment.proto` es el artefacto más valioso del ejercicio: a partir de él cualquier lenguaje compatible con gRPC puede generar su propio cliente o servidor sin necesidad de conocer la implementación Java. Esto resuelve el mayor problema de RMI y hace a gRPC una opción natural para sistemas distribuidos modernos con múltiples lenguajes.

Hasta este punto del taller se puede observar una progresión clara en el nivel de abstracción y en la formalidad del contrato de comunicación: de texto libre sobre TCP, pasando por rutas HTTP implícitas y una interfaz Java acoplada al lenguaje, hasta llegar a un contrato independiente del lenguaje verificado en tiempo de compilación. Cada estilo resuelve los problemas visibles del anterior, pero a su vez abre nuevas preguntas — sobre escalabilidad, descubrimiento de servicios, manejo de fallos — que los estilos arquitectónicos siguientes continuarán abordando.
