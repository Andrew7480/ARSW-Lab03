import java.io.Serializable;

public class LabEquipment implements Serializable {
    private String code;
    private String name;
    private boolean isAvailable;
    private Lab lab;

    public enum Lab {
        LAB1, LAB2, LAB3
    }

    public LabEquipment(String code, String name, Lab lab) {
        this.code = code;
        this.name = name;
        this.isAvailable = true;
        this.lab = lab;
    }

    public boolean reservarEquipo() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }

    public boolean liberarEquipo() {
        if (!isAvailable) {
            isAvailable = true;
            return true;
        }
        return false;
    }

    public boolean isDisponible() {
        return isAvailable;
    }

    @Override
    public String toString() {
        return code + " - " + name + " - " + lab + " - " + (isAvailable ? "Available" : "Not Available");
    }

}
