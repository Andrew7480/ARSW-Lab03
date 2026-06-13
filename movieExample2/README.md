# Taller Integrador de Arquitecturas Distribuidas

Exploración progresiva de estilos arquitectónicos para exponer un repositorio de películas, partiendo de sockets TCP hasta llegar a servicios REST con frameworks modernos.

---

#  Parte III - RPC con Java RMI

RMI permite que un objeto en una máquina virtual de Java invoque métodos de un objeto ubicado en otra máquina virtual. Con RMI se deja de diseñar manualmente el formato de los mensajes, y la comunicación se expresa como invocación remota de métodos. 

RMI es importante como tecnología histórica y conceptual, porque permite entender el modelo RPC. Sin
embargo, está fuertemente asociado al ecosistema Java.

---

## Ejecución

Compilar desde `movieExample2/`:

```bash
javac -d out src/*.java
```

Abrir dos terminales:

```bash
# Terminal 1 - servidor RMI (publica el servicio en el puerto 23000)
java -cp out MovieRmiServer
```

```bash
# Terminal 2 - cliente RMI
java -cp out MovieRmiClient
```