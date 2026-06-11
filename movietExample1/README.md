# Taller Integrador de Arquitecturas Distribuidas

Exploración progresiva de estilos arquitectónicos para exponer un repositorio de películas, partiendo de sockets TCP hasta llegar a servicios REST con frameworks modernos.

---

## Parte I - Arquitectura Cliente-Servidor con Sockets TCP

La arquitectura cliente-servidor permite que un programa cliente solicite un servicio a un programa servidor que se encuentra escuchando en un puerto específico. En esta primera aproximación, el protocolo de comunicación se diseña manualmente mediante mensajes de texto enviados por sockets TCP.

Esta solución es útil para entender los fundamentos de la comunicación distribuida, pero obliga al desarrollador a definir manualmente el formato de los mensajes, la validación, los errores y las respuestas. Además, requiere un cliente Java dedicado, por lo que no puede consumirse desde un navegador ni desde herramientas genéricas como `curl`.

---

## Parte II - Arquitectura HTTP con Java puro

Exponer la funcionalidad mediante HTTP permite que cualquier navegador o herramienta estándar pueda consumir el servicio sin necesidad de un cliente Java dedicado. Para ello se usa la clase `HttpServer` del paquete `com.sun.net.httpserver`, disponible en el JDK sin dependencias externas.

Esta solución introduce conceptos clave del protocolo HTTP: método, ruta, parámetros de consulta y respuesta. Sin embargo, la API devuelve HTML directamente, lo que mezcla datos y presentación y dificulta el consumo desde otros sistemas.

---
