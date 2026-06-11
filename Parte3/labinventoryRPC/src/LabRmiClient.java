import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LabRmiClient {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23000);
        LabInventoryService service = (LabInventoryService) registry.lookup("labInventoryService");
        LabEquipment equipment = service.getEquipment("EQ001");
        System.out.println("Equipo recibido: " + equipment);

        String EQ001Info = service.consultarEquipo("EQ001");
        System.out.println("Información del equipo EQ001: " + EQ001Info);

        boolean reservado = service.reservarEquipo("EQ001");
        System.out.println("Reserva del equipo EQ001: " + (reservado ? "Exitosa" : "Fallida"));

        boolean disponible = service.isDisponible("EQ001");
        System.out.println("¿El equipo EQ001 está disponible? " + (disponible ? "Sí" : "No"));

        boolean liberado = service.liberarEquipo("EQ001");
        System.out.println("Liberación del equipo EQ001: " + (liberado ? "Exitosa" : "Fallida"));

        disponible = service.isDisponible("EQ001");
        System.out.println("¿El equipo EQ001 está disponible? " + (disponible ? "Sí" : "No"));
    }
}
