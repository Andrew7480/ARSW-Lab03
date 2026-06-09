# Parte I - Arquitectura Cliente-Servidor con Sockets TCP

La arquitectura cliente-servidor permite que un programa cliente solicite un servicio a un programa servidor que se encuentra escuchando en un puerto específico. En esta primera aproximación, el protocolo de comunicación será diseñado manualmente mediante mensajes de texto enviados por sockets TCP.

```text
Cliente Java
 |
 | Mensaje de texto: MOVIE:1
 v
Servidor TCP Java
 |
 | Respuesta: 1,Interstellar,Christopher Nolan,2014
 v
Cliente muestra el resultado
```

Esta solución es útil para entender los fundamentos de comunicación distribuida, pero obliga al desarrollador a definir manualmente el formato de los mensajes, la validación, los errores y las respuestas.

## Estructura del proyecto

```text
movietcp/
│
├── src/
│   ├── Movie.java
│   ├── MovieRepository.java
│   ├── MovieServer.java
│   └── MovieClient.java
│
└── out/
```

## Compilación

Desde la raíz del proyecto ejecutar:

```bash
javac -d out src\*.java
```

Este comando compila todos los archivos fuente y genera los archivos `.class` dentro de la carpeta `out`.

## Ejecución

### Iniciar el servidor

Abrir una terminal en la raíz del proyecto y ejecutar:

```bash
java -cp out MovieServer
```

Salida esperada:

```text
MovieServer TCP escuchando en puerto 35000...
```

### Iniciar el cliente

Abrir una segunda terminal y ejecutar:

```bash
java -cp out MovieClient
```

El cliente solicitará el identificador de una película:

```text
Ingrese el ID de la película: 1
```
