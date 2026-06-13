# Taller Integrador de Arquitecturas Distribuidas

Exploración progresiva de estilos arquitectónicos para exponer un repositorio de películas, partiendo de sockets TCP hasta llegar a servicios REST con frameworks modernos.

---

# Parte VI - API Gateway

Cuando el cliente conoce todos los microservicios, queda acoplado a sus direcciones, puertos y contratos individuales. Un API Gateway centraliza el acceso y actúa como punto de entrada único hacia el sistema.

En este taller el Gateway se implementará como un programa Java sencillo. No se busca usar infraestructura avanzada, sino comprender el patrón arquitectónico.

Aunque el Gateway centraliza el acceso, también puede convertirse en un punto crítico. Si el Gateway
falla, el cliente pierde acceso a todo el sistema. Por eso, en arquitecturas reales se requieren estrategias
de disponibilidad, monitoreo y escalabilidad.

---

## Ejecución

Desde `movieExample5/movie-grpc/`:

```powershell
mvn compile
```

Abrir cinco terminales:

```powershell
# Terminal 1 - MovieService (puerto 50051)
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.movie.MovieGrpcServer2'
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
# Terminal 4 - API Gateway (punto de entrada único)
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.gateway.MovieGateway'
```

```powershell
# Terminal 5 - cliente (habla solo con el Gateway)
mvn exec:java '-Dexec.mainClass=edu.eci.arsw.client.MovieGrpcClient'
```
