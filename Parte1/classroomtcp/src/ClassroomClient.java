import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClassroomClient {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket("127.0.0.1", 35000);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Bienvenido al ClassroomServer.");
        System.out.println("Estado actual de los salones:");
        System.out.println(in.readLine());
        System.out.print("Ingrese la acción (CONSULTAR_SALON, RESERVAR_SALON o LIBERAR_SALON): ");
        String action = scanner.nextLine().trim().toUpperCase();
        System.out.print("Ingrese el código del salón (ej: E301): ");
        String code = scanner.nextLine().trim().toUpperCase();
        out.println(action + "," + code);
        String response = in.readLine();
        System.out.println("Respuesta del servidor: " + response);
        in.close();
        out.close();
        socket.close();
    }

}