# Taller Integrador de Arquitecturas Distribuidas

Exploración progresiva de estilos arquitectónicos para exponer un repositorio de películas, partiendo de sockets TCP hasta llegar a servicios REST con frameworks modernos.

---

# Parte V - Arquitectura de Microservicios

Hasta este punto, aunque se ha cambiado la tecnología de comunicación, el sistema sigue concentrando toda la responsabilidad en un único servicio. Cuando un sistema crece, es común separar responsabilidades en servicios más pequeños, autónomos y altamente cohesivos.

La arquitectura de microservicios no significa crear muchos servicios sin criterio. Cada servicio debe tener una responsabilidad clara y debe evitar conocer detalles internos de otros servicios.

Como pasos primero se identifican las responsabilidades que se pueden separar.

MovieClient
 |---- localhost:50051 -> MovieService
 |---- localhost:50052 -> ReviewService
 |---- localhost:50053 -> RecommendationService

---

## Ejecución

Desde `movieExample4/movie-grpc/`:

```powershell
mvn compile
```

Abrir cuatro terminales, una por servicio:

```powershell
# Terminal 1 - MovieService (puerto 50051)
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.movie.MovieGrpcServer1'
```

```powershell
# Terminal 2 - ReviewService (puerto 50052)
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.review.ReviewGrpcServer'
```

```powershell
# Terminal 3 - RecommendationService (puerto 50053)
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.recommendation.RecommendationGrpcServer'
```

```powershell
# Terminal 4 - cliente (consulta los tres servicios directamente)
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.client.MovieGrpcClient'
```