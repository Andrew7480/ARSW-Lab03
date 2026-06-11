import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LabInventoryService extends Remote {
    LabEquipment getEquipment(String code) throws RemoteException;
    String consultarEquipo(String codigo) throws RemoteException;
    boolean isDisponible(String codigo) throws RemoteException;
    boolean reservarEquipo(String codigo) throws RemoteException;
    boolean liberarEquipo(String codigo) throws RemoteException;
 
}