
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClassroomServer {
    @SuppressWarnings("java:S2189")
    public static void main(String[] args) throws Exception {

        ClassroomRepository repository = new ClassroomRepository();
        ServerSocket serverSocket = new ServerSocket(35000);
        System.out.println("ClassroomServer TCP escuchando en puerto 35000...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(repository.listClassrooms());
            String request = in.readLine();
            String response = processRequest(request, repository);
            out.println(response);
            in.close();
            out.close();
            clientSocket.close();
        }
    }

    private static String processRequest(String request, ClassroomRepository repository) {
        if (request == null || !request.contains(",")) {
            return "ERROR: formato inválido. Use ACCION,CODIGO_SALON";
        }

        try {
            String[] parts = request.split(",", 2);
            String action = parts[0].trim().toUpperCase();
            String code = parts[1].trim().toUpperCase();
            Classroom classroom = repository.findById(code);
            if (classroom == null) {
                return OperationEnum.ERROR_SALON_NO_EXISTE.name();
            }
            switch (action) {
                case "CONSULTAR_SALON":
                    return classroom.getState().name();
                case "RESERVAR_SALON":
                    if (classroom.getState() == ClassroomState.SALON_RESERVADO) {
                        return OperationEnum.ERROR_OPERACION_INVALIDA.name();
                    }
                    repository.reserveClassroom(code);
                    return OperationEnum.RESERVA_EXITOSA.name();
                case "LIBERAR_SALON":
                    if (classroom.getState() == ClassroomState.SALON_DISPONIBLE) {
                        return OperationEnum.ERROR_OPERACION_INVALIDA.name();
                    }
                    repository.releaseClassroom(code);
                    return OperationEnum.LIBERACION_EXITOSA.name();
                default:
                    return OperationEnum.ERROR_OPERACION_INVALIDA.name();
            }
        } catch (Exception e) {
            return "ERROR: solicitud inválida";
        }
    }
}
