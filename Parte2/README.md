# Gestión de Salones vía HTTP

## Resumen

Se transformó el sistema de gestión de salones TCP a un servidor HTTP en Java usando `com.sun.net.httpserver`, sin ningún framework externo. El servidor expone cuatro rutas que permiten consultar, reservar y liberar los salones **E301, E302, E303 y E304** desde cualquier navegador, curl o Postman.

### Clases principales

| Clase | Responsabilidad |
|---|---|
| `ClassroomHttpServer` | Configura el servidor en el puerto 8080 y registra los tres contextos HTTP |
| `ClassroomHandler` | Maneja `GET /rooms` y `GET /rooms?id=E303` |
| `ReserveHandler` | Maneja `POST /rooms/reserve?id=E303` |
| `ReleaseHandler` | Maneja `POST /rooms/release?id=E303` |
| `ClassroomRepository` | Mantiene el mapa de salones en memoria |
| `Classroom` / `ClassroomState` | Modelo de datos del salón con su estado |

### Rutas implementadas

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/rooms` | Lista todos los salones con su estado | HTML con todos los salones |
| GET | `/rooms?id=E303` | Consulta el estado de un salón específico | HTML con el salón o mensaje de no encontrado |
| POST | `/rooms/reserve?id=E303` | Reserva un salón | HTML confirmando la reserva |
| POST | `/rooms/release?id=E303` | Libera un salón | HTML confirmando la liberación |

### Cómo probar

Compile y ejecute:

```bash
javac *.java
java ClassroomHttpServer
```

Ejemplos con curl:

```bash
# Listar todos los salones
curl http://localhost:8080/rooms

# Consultar un salón específico
curl http://localhost:8080/rooms?id=E303

# Reservar un salón
curl -X POST http://localhost:8080/rooms/reserve?id=E303

# Liberar un salón
curl -X POST http://localhost:8080/rooms/release?id=E303
```

### Cumplimiento de requisitos

| Requisito | Estado |
|---|---|
| `GET /rooms` lista todos los salones | Cumplido |
| `GET /rooms?id=E303` consulta un salón | Cumplido |
| `POST /rooms/reserve?id=E303` reserva un salón | Cumplido |
| `POST /rooms/release?id=E303` libera un salón | Cumplido |
| Respuestas en HTML simple | Cumplido |
| Sin framework externo | Cumplido |

---

## Reflexión y conclusiones

### ¿Qué ventajas ofrece HTTP frente a un protocolo de texto definido manualmente?

HTTP estandariza el contrato de comunicación: el método (`GET`, `POST`) indica la intención, la ruta identifica el recurso, y el código de estado resume el resultado. Esto permite que cualquier cliente —navegador, curl, Postman, otra aplicación— consuma el servicio sin necesidad de conocer un protocolo propio. En el ejercicio TCP era necesario un cliente Java que supiera exactamente el formato `ACCION,CODIGO`; con HTTP ese conocimiento ya está incorporado en la infraestructura web estándar.

### ¿Qué limitaciones tiene construir un servidor HTTP sin framework?

La clase `com.sun.net.httpserver` es suficiente para prototipos, pero expone las limitaciones del enfoque manual: no hay manejo automático de rutas con parámetros dinámicos, no hay validación de entrada, no hay gestión de errores centralizada, y el parseo de la query string (`extractId`) debe escribirse a mano. A medida que crecen las rutas, el código se vuelve repetitivo y difícil de mantener. Un framework como Spring Boot o Javalin resolvería todo esto con muy poco código adicional.

### ¿Cómo cambiaría esta solución si se usara JSON en lugar de HTML?

Los handlers cambiarían el cuerpo de la respuesta de HTML a un objeto JSON (por ejemplo `{"id":"E303","estado":"SALON_RESERVADO"}`), y se agregaría el encabezado `Content-Type: application/json`. La ventaja es que la respuesta sería consumible directamente por cualquier cliente JavaScript, móvil o servicio externo sin necesidad de parsear HTML. Además, los errores podrían comunicarse con una estructura uniforme como `{"error":"ERROR_SALON_NO_EXISTE"}` en lugar de texto libre, lo que hace el contrato más predecible y fácil de integrar.

---

## Conclusiones

Transformar el servidor TCP a HTTP deja en evidencia cuánto trabajo hace implícitamente el protocolo: el método HTTP (`GET` / `POST`) ya comunica la intención de la operación, la ruta identifica el recurso, y cualquier herramienta estándar (navegador, curl, Postman) puede interactuar con el servicio sin conocer un protocolo propio.

Al mismo tiempo, construir el servidor sin framework muestra exactamente qué resuelven los frameworks: el parseo de rutas, la extracción de parámetros, la gestión de errores y los encabezados de respuesta son responsabilidad del desarrollador. Con cuatro rutas esto es manejable; con veinte se vuelve insostenible.

La comparación con la Parte 1 también revela un salto en interoperabilidad: el servidor TCP necesitaba un cliente Java específico, mientras que el servidor HTTP puede ser consumido desde cualquier lenguaje o herramienta que entienda HTTP, que hoy es prácticamente todo.
