import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LabInventoryService extends Remote {
    List<String> consultarEquipos() throws RemoteException;
    LabEquipment getEquipment(String code) throws RemoteException;
    String consultarEquipo(String codigo) throws RemoteException;
    boolean isDisponible(String codigo) throws RemoteException;
    boolean reservarEquipo(String codigo) throws RemoteException;
    boolean liberarEquipo(String codigo) throws RemoteException;
}