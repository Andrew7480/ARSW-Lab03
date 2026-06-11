
public class Classroom {
    private String code;
    private ClassroomState classroomState = ClassroomState.SALON_DISPONIBLE;


    public Classroom(String code) {
        this.code = code;
    }

    public String toText() {
        return code + "," + classroomState;
    }

    public String getCode(){
        return code;
    }

    public ClassroomState getState(){
        return classroomState;
    }

    public void reservar(){
        classroomState = ClassroomState.SALON_RESERVADO;
    }

    public void liberar(){
        classroomState = ClassroomState.SALON_DISPONIBLE;
    }
}
