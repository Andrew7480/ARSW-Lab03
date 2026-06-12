# Inventario de Laboratorios — RMI

## Resumen

Se implementó un sistema RMI en Java para consultar y reservar equipos de laboratorio. A diferencia de los ejercicios TCP y HTTP, en RMI el cliente invoca métodos Java directamente sobre un objeto remoto sin diseñar manualmente el formato de los mensajes. El contrato de comunicación está definido por la interfaz `LabInventoryService`, que extiende `Remote`.

El servidor publica el servicio en el puerto 23000 a través del RMI Registry. El cliente lo localiza por nombre (`labInventoryService`) y llama a los métodos como si el objeto fuera local.

### Clases principales

| Clase / Interfaz | Responsabilidad |
|---|---|
| `LabInventoryService` | Interfaz remota — define el contrato de comunicación |
| `LabInventoryServiceImpl` | Implementación del servicio; mantiene el inventario en memoria |
| `LabEquipment` | Objeto serializable que representa un equipo (código, nombre, laboratorio, estado) |
| `LabRmiServer` | Crea el RMI Registry en el puerto 23000 y publica el servicio |
| `LabRmiClient` | Se conecta al registry, obtiene la referencia remota y demuestra todos los métodos |

### Métodos de la interfaz remota

| Método | Descripción | Retorno |
|---|---|---|
| `consultarEquipos()` | Lista todos los equipos con su estado | `List<String>` |
| `consultarEquipo(codigo)` | Detalle de un equipo específico | `String` |
| `getEquipment(codigo)` | Objeto completo del equipo | `LabEquipment` |
| `isDisponible(codigo)` | Verifica si un equipo está disponible | `boolean` |
| `reservarEquipo(codigo)` | Reserva un equipo disponible; retorna `false` si ya está reservado | `boolean` |
| `liberarEquipo(codigo)` | Libera un equipo reservado; retorna `false` si ya está disponible | `boolean` |

### Datos del inventario inicial

| Código | Nombre | Laboratorio |
|---|---|---|
| EQ001 | Microscope | LAB1 |
| EQ002 | Centrifuge | LAB2 |
| EQ003 | Spectrophotometer | LAB3 |

### Cómo ejecutar

```bash
# Compilar
javac *.java

# Terminal 1 — levantar el servidor
java LabRmiServer

# Terminal 2 — ejecutar el cliente
java LabRmiClient
```

### Cumplimiento de requisitos

| Requisito | Estado |
|---|---|
| `List<String> consultarEquipos()` | Cumplido |
| `String consultarEquipo(String codigo)` | Cumplido |
| `boolean reservarEquipo(String codigo)` | Cumplido — retorna `false` si ya está reservado |
| `boolean liberarEquipo(String codigo)` | Cumplido — retorna `false` si ya está disponible |
| Datos: código, nombre, laboratorio, estado | Cumplido |
| Objeto `LabEquipment` serializable | Cumplido |

---

## Reflexión y conclusiones

### ¿Qué cambió al pasar de HTTP a RMI?

En HTTP el cliente construía una URL y leía texto o HTML; en RMI el cliente llama a un método Java con parámetros y recibe objetos Java directamente. Desaparece todo el trabajo manual de serializar, transmitir y parsear mensajes: el stub generado por RMI hace eso de forma transparente. El código del cliente es más expresivo y se asemeja a una llamada local, pero a costa de estar completamente acoplado a Java.

### ¿Dónde está definido el contrato de comunicación?

En la interfaz `LabInventoryService`. A diferencia del protocolo TCP (convención de texto entre cliente y servidor) y HTTP (rutas y métodos del estándar), en RMI el contrato es código Java tipado y verificado en tiempo de compilación. Si un método cambia de firma, el cliente no compila, lo que hace que las incompatibilidades se detecten antes de ejecutar el sistema.

### ¿Qué problemas tendría este sistema si un cliente no está escrito en Java?

RMI es exclusivo del ecosistema JVM: usa serialización binaria de Java y el protocolo JRMP, que no tienen implementaciones estándar en otros lenguajes. Un cliente Python, JavaScript o Go no puede conectarse directamente al registro RMI ni invocar los métodos. Para interoperabilidad multi-lenguaje se necesitaría reemplazar RMI por una solución agnóstica al lenguaje como REST/HTTP, gRPC (con Protobuf) o un broker de mensajes, que son las alternativas modernas al modelo RPC.

---

## Conclusiones

RMI representa un salto conceptual respecto a TCP y HTTP: el desarrollador deja de pensar en mensajes y empieza a pensar en métodos. La interfaz `LabInventoryService` describe lo que el sistema puede hacer en términos de Java puro, y el mecanismo de transporte (sockets, serialización, registro) queda completamente oculto. Esto hace el código del cliente más expresivo y más cercano a la lógica del negocio.

El contrato tipado en código Java es una mejora real sobre las convenciones de texto de las partes anteriores: si un método cambia de firma, el cliente no compila. Sin embargo, esa ventaja viene atada a Java, lo que es una limitación importante en sistemas modernos donde diferentes servicios pueden estar escritos en distintos lenguajes.

Esta parte también introduce la noción de registro de servicios (RMI Registry), un precursor directo de los sistemas de descubrimiento de servicios modernos como Consul o Eureka en arquitecturas de microservicios.
