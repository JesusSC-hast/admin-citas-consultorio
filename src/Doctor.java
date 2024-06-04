public class Doctor {
    private int id;
    private String nombreCompleto;
    private String especialidad;

    public Doctor(int id, String nombreCompleto, String especialidad) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
    }

    public int getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    @Override
    public String toString() {
        return id + "," + nombreCompleto + "," + especialidad;
    }
}