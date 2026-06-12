import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabInventoryServiceImpl extends UnicastRemoteObject implements LabInventoryService {
    private Map<String, LabEquipment> inventory = new HashMap<>();

    public LabInventoryServiceImpl() throws RemoteException {
        inventory.put("EQ001", new LabEquipment("EQ001", "Microscope", LabEquipment.Lab.LAB1));
        inventory.put("EQ002", new LabEquipment("EQ002", "Centrifuge", LabEquipment.Lab.LAB2));
        inventory.put("EQ003", new LabEquipment("EQ003", "Spectrophotometer", LabEquipment.Lab.LAB3));
    }

    @Override
    public List<String> consultarEquipos() throws RemoteException {
        List<String> result = new ArrayList<>();
        for (LabEquipment equipment : inventory.values()) {
            result.add(equipment.toString());
        }
        return result;
    }

    @Override
    public LabEquipment getEquipment(String code) throws RemoteException {
        return inventory.get(code);
    }

    @Override
    public String consultarEquipo(String codigo) throws RemoteException {
        LabEquipment equipment = inventory.get(codigo);
        if (equipment != null) {
            return equipment.toString();
        } else {
            return "Equipo no encontrado";
        }
    }

    @Override
    public boolean reservarEquipo(String codigo) throws RemoteException {
        LabEquipment equipment = inventory.get(codigo);
        if (equipment != null) {
            return equipment.reservarEquipo();
        }
        return false;
    }

    @Override
    public boolean liberarEquipo(String codigo) throws RemoteException {
        LabEquipment equipment = inventory.get(codigo);
        if (equipment != null) {
            return equipment.liberarEquipo();
        }
        return false;
    }

    @Override
    public boolean isDisponible(String codigo) throws RemoteException {
        LabEquipment equipment = inventory.get(codigo);
        if (equipment != null) {
            return equipment.isDisponible();
        }
        return false;
    }
}
