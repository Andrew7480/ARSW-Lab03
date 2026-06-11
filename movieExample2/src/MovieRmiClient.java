import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MovieRmiClient {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23000);
        MovieService service = (MovieService) registry.lookup("movieService");
        Movie movie = service.getMovie(1);
        System.out.println("Película recibida: " + movie);
    }
}