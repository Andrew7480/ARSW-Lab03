# Sistema de Gestión de Salones — TCP

## Resumen

Se implementó un servidor TCP en Java para gestionar la reserva de salones de la Escuela usando el patrón cliente-servidor. El servidor mantiene en memoria los salones **E301, E302, E303 y E304**, cada uno con un estado (`SALON_DISPONIBLE` o `SALON_RESERVADO`). Al conectarse, el cliente recibe automáticamente la lista completa de salones con sus estados actuales. Luego puede enviar una acción al servidor para reservar o liberar un salón.

### Clases principales

| Clase | Responsabilidad |
|---|---|
| `ClassroomServer` | Escucha en el puerto 35000, acepta conexiones y delega la lógica |
| `ClassroomClient` | Se conecta al servidor, muestra el estado de los salones e interactúa con el usuario |
| `ClassroomRepository` | Mantiene el mapa de salones en memoria |
| `Classroom` | Representa un salón con su código y estado |
| `ClassroomState` | Enum con los estados posibles: `SALON_DISPONIBLE`, `SALON_RESERVADO` |
| `OperationEnum` | Enum con las respuestas del protocolo |

### Protocolo implementado

El separador es `,` y los comandos siguen exactamente el protocolo sugerido.

| Comando | Respuestas posibles |
|---|---|
| `CONSULTAR_SALON,E303` | `SALON_DISPONIBLE` \| `SALON_RESERVADO` \| `ERROR_SALON_NO_EXISTE` |
| `RESERVAR_SALON,E303` | `RESERVA_EXITOSA` \| `ERROR_OPERACION_INVALIDA` \| `ERROR_SALON_NO_EXISTE` |
| `LIBERAR_SALON,E303` | `LIBERACION_EXITOSA` \| `ERROR_OPERACION_INVALIDA` \| `ERROR_SALON_NO_EXISTE` |

Al conectarse, el servidor también envía la lista completa de todos los salones con su estado actual, antes de que el cliente envíe su comando.

### Cumplimiento de requisitos

| Requisito | Estado |
|---|---|
| Lista inicial E301–E304 en memoria | Cumplido |
| Estado disponible/reservado por salón | Cumplido |
| Consultar estado de un salón (`CONSULTAR_SALON`) | Cumplido |
| Reservar un salón disponible (`RESERVAR_SALON`) | Cumplido — retorna error si ya está reservado |
| Liberar un salón reservado (`LIBERAR_SALON`) | Cumplido — retorna error si ya está disponible |
| Todas las respuestas del protocolo | Cumplido |

---

## Reflexión y conclusiones

### ¿Qué tan fácil sería agregar una nueva operación al protocolo?

Relativamente fácil en la implementación actual: basta con agregar un nuevo `case` en el `switch` de `processRequest` y, si hace falta, un nuevo valor en `OperationEnum`. Sin embargo, el protocolo no está definido en ningún archivo formal (como un esquema o contrato explícito), sino en convenciones de texto dispersas entre cliente y servidor. Esto significa que cualquier cambio requiere coordinar manualmente ambos lados, lo que se vuelve propenso a errores conforme el sistema crece.

### ¿Qué ocurre si dos clientes intentan reservar el mismo salón al mismo tiempo?

El servidor es **monohilo**: atiende un cliente a la vez con un bucle `while(true) { accept() → procesar → cerrar }`. Esto evita condiciones de carrera accidentalmente, pero a costa de bloquear a todos los demás clientes mientras uno está siendo atendido. Si el servidor fuera multihilo (usando `Thread` o un `ExecutorService`), el acceso concurrente al `ClassroomRepository` podría producir una condición de carrera donde dos clientes lean el estado "disponible" al mismo tiempo y ambos reciban `RESERVA_EXITOSA` para el mismo salón. La solución correcta sería sincronizar el acceso al repositorio (por ejemplo con `synchronized` o usando un `ConcurrentHashMap`).

### ¿Dónde está definido realmente el contrato de comunicación?

En **convenciones de texto implícitas**. No existe un archivo formal de especificación del protocolo; el contrato vive en el código del cliente (cómo construye el mensaje) y en el `processRequest` del servidor (cómo lo parsea). Esto es frágil: si alguien cambia el separador de `:` a `,`, o agrega un espacio extra, la comunicación falla silenciosamente. Un diseño más robusto definiría el protocolo en un archivo separado o usaría un formato estructurado (JSON, Protobuf) que haga el contrato explícito y verificable.
