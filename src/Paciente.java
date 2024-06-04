public class Paciente {
    private int id;
    private String nombreCompleto;

    public Paciente(int id, String nombreCompleto) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
    }

    public int getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    @Override
    public String toString() {
        return id + "," + nombreCompleto;
    }
}