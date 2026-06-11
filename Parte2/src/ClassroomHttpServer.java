import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ClassroomHttpServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        ClassroomRepository repository = new ClassroomRepository();
        server.createContext("/rooms", new ClassroomHandler(repository));
        server.createContext("/rooms/reserve", new ReserveHandler(repository));
        server.createContext("/rooms/release", new ReleaseHandler(repository));
        server.setExecutor(null);
        server.start();
        System.out.println("ClassroomHttpServer escuchando en http://localhost:8080/rooms?id=E301");
    }

    static class ClassroomHandler implements HttpHandler {
        private ClassroomRepository repository;

        public ClassroomHandler(ClassroomRepository repository) {
            this.repository = repository;
        }

        @Override
        public void handle(HttpExchange exchange) {
            try {

                if (!exchange.getRequestMethod().equals("GET")) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String query = exchange.getRequestURI().getQuery();
                String response;

                if (query == null) {
                    response = "<html><body><h1>" + repository.listClassrooms() + "</h1></body></html>";
                } else {
                    String id = extractId(query);
                    Classroom classroom = repository.findById(id);

                    if (classroom == null) {
                        response = "<html><body><h1>Clase no encontrada</h1></body></html>";
                    } else {
                        response = "<html><body><h1>" + classroom.toText() + "</h1></body></html>";
                    }
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    static class ReserveHandler implements HttpHandler {
        private ClassroomRepository repository;

        public ReserveHandler(ClassroomRepository repository) {
            this.repository = repository;
        }

        @Override
        public void handle(HttpExchange exchange) {
            try {

                if (!exchange.getRequestMethod().equals("POST")) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String query = exchange.getRequestURI().getQuery();
                String id = extractId(query);

                Classroom classroom = repository.findById(id);

                String response;
                if (classroom == null) {
                    response = "<html><body><h1>Clase no encontrada</h1></body></html>";
                } else {
                    repository.reserveClassroom(id);
                    response = "<html><body><h1>Clase reservada exitosamente</h1></body></html>";
                }
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    static class ReleaseHandler implements HttpHandler {
        private ClassroomRepository repository;

        public ReleaseHandler(ClassroomRepository repository) {
            this.repository = repository;
        }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!exchange.getRequestMethod().equals("POST")) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String query = exchange.getRequestURI().getQuery();
                String id = extractId(query);
                Classroom classroom = repository.findById(id);
                String response;
                if (classroom == null) {
                    response = "<html><body><h1>Clase no encontrada</h1></body></html>";
                } else {
                    repository.releaseClassroom(id);
                    response = "<html><body><h1>Clase liberada exitosamente</h1></body></html>";
                }
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static String extractId(String query) {
        if (query == null || !query.startsWith("id=")) {
            return null;
        }
        return query.substring(3);
    }
}