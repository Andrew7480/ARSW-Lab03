
import java.net.ServerSocket;

public class ClassroomServer {

    public static void main(String[] args) throws Exception {

        ClassroomRepository repository = new ClassroomRepository();
        ServerSocket serverSocket = new ServerSocket(35000);
        System.out.println("ClassroomServer TCP escuchando en puerto 35000...");


        while(true){
            
        }
    }
}
