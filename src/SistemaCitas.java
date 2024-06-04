import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class SistemaCitas {
    private List<Doctor> doctores;
    private List<Paciente> pacientes;
    private List<Cita> citas;
    private List<Administrador> administradores;
    private final String dbPath = "db/";

    public SistemaCitas() {
        doctores = new ArrayList<>();
        pacientes = new ArrayList<>();
        citas = new ArrayList<>();
        administradores = new ArrayList<>();
        crearDirectorioDB();
        cargarDatos();
    }

    private void crearDirectorioDB() {
        File directorio = new File(dbPath);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    private void cargarDatos() {
        try {
            cargarDoctores();
            cargarPacientes();
            cargarCitas();
            cargarAdministradores();
        } catch (IOException e) {
            System.out.println("Error cargando datos: " + e.getMessage());
        }
    }

    private void cargarDoctores() throws IOException {
        Path path = Paths.get(dbPath + "doctores.csv");
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(",");
                Doctor doctor = new Doctor(Integer.parseInt(parts[0]), parts[1], parts[2]);
                doctores.add(doctor);
            }
        }
    }

    private void cargarPacientes() throws IOException {
        Path path = Paths.get(dbPath + "pacientes.csv");
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(",");
                Paciente paciente = new Paciente(Integer.parseInt(parts[0]), parts[1]);
                pacientes.add(paciente);
            }
        }
    }

    private void cargarCitas() throws IOException {
        Path path = Paths.get(dbPath + "citas.csv");
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(",");
                LocalDateTime fechaHora = LocalDateTime.parse(parts[1]);
                Doctor doctor = buscarDoctor(Integer.parseInt(parts[3]));
                Paciente paciente = buscarPaciente(Integer.parseInt(parts[4]));
                Cita cita = new Cita(Integer.parseInt(parts[0]), fechaHora, parts[2], doctor, paciente);
                citas.add(cita);
            }
        }
    }

    private void cargarAdministradores() throws IOException {
        Path path = Paths.get(dbPath + "administradores.csv");
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(",");
                Administrador admin = new Administrador(parts[0], parts[1]);
                administradores.add(admin);
            }
        }

        // Añadir un administrador por defecto si no hay ninguno
        if (administradores.isEmpty()) {
            Administrador adminDefault = new Administrador("admin", "admin123");
            administradores.add(adminDefault);
            guardarAdministradores();
            System.out.println("Se ha añadido un administrador por defecto: ID='admin', Password='admin123'");
        }
    }

    private Doctor buscarDoctor(int id) {
        return doctores.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
    }

    private Paciente buscarPaciente(int id) {
        return pacientes.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public void guardarDoctores() throws IOException {
        Path path = Paths.get(dbPath + "doctores.csv");
        BufferedWriter writer = Files.newBufferedWriter(path);
        for (Doctor doctor : doctores) {
            writer.write(doctor.toString());
            writer.newLine();
        }
        writer.close();
    }

    public void guardarPacientes() throws IOException {
        Path path = Paths.get(dbPath + "pacientes.csv");
        BufferedWriter writer = Files.newBufferedWriter(path);
        for (Paciente paciente : pacientes) {
            writer.write(paciente.toString());
            writer.newLine();
        }
        writer.close();
    }

    public void guardarCitas() throws IOException {
        Path path = Paths.get(dbPath + "citas.csv");
        BufferedWriter writer = Files.newBufferedWriter(path);
        for (Cita cita : citas) {
            writer.write(cita.toString());
            writer.newLine();
        }
        writer.close();
    }

    public void guardarAdministradores() throws IOException {
        Path path = Paths.get(dbPath + "administradores.csv");
        BufferedWriter writer = Files.newBufferedWriter(path);
        for (Administrador admin : administradores) {
            writer.write(admin.getId() + "," + admin.getPassword());
            writer.newLine();
        }
        writer.close();
    }

    public void agregarDoctor(Doctor doctor) {
        doctores.add(doctor);
    }

    public void agregarPaciente(Paciente paciente) {
        pacientes.add(paciente);
    }

    public void agregarCita(Cita cita) {
        citas.add(cita);
    }

    public boolean autenticarAdmin(String id, String password) {
        return administradores.stream().anyMatch(a -> a.autenticar(id, password));
    }

    public void ejecutar() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Bienvenido al sistema de administración de citas");
            System.out.println("1. Iniciar sesión como administrador");
            System.out.println("2. Salir");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            if (opcion == 1) {
                System.out.print("ID: ");
                String id = scanner.nextLine();
                System.out.print("Contraseña: ");
                String password = scanner.nextLine();

                if (autenticarAdmin(id, password)) {
                    menuAdmin(scanner);
                } else {
                    System.out.println("ID o contraseña incorrectos");
                }
            } else if (opcion == 2) {
                break;
            } else {
                System.out.println("Opción no válida");
            }
        }
    }

    private void menuAdmin(Scanner scanner) {
        while (true) {
            System.out.println("Menú de Administrador");
            System.out.println("1. Dar de alta doctor");
            System.out.println("2. Dar de alta paciente");
            System.out.println("3. Crear cita");
            System.out.println("4. Salir");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (opcion) {
                case 1:
                    darDeAltaDoctor(scanner);
                    break;
                case 2:
                    darDeAltaPaciente(scanner);
                    break;
                case 3:
                    crearCita(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void darDeAltaDoctor(Scanner scanner) {
        System.out.print("ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        System.out.print("Nombre completo: ");
        String nombreCompleto = scanner.nextLine();
        System.out.print("Especialidad: ");
        String especialidad = scanner.nextLine();

        Doctor doctor = new Doctor(id, nombreCompleto, especialidad);
        agregarDoctor(doctor);

        try {
            guardarDoctores();
        } catch (IOException e) {
            System.out.println("Error guardando doctores: " + e.getMessage());
        }

        System.out.println("Doctor dado de alta exitosamente");
    }

    private void darDeAltaPaciente(Scanner scanner) {
        System.out.print("ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        System.out.print("Nombre completo: ");
        String nombreCompleto = scanner.nextLine();

        Paciente paciente = new Paciente(id, nombreCompleto);
        agregarPaciente(paciente);

        try {
            guardarPacientes();
        } catch (IOException e) {
            System.out.println("Error guardando pacientes: " + e.getMessage());
        }

        System.out.println("Paciente dado de alta exitosamente");
    }

    private void crearCita(Scanner scanner) {
        System.out.print("ID de la cita: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        System.out.print("Fecha y hora (YYYY-MM-DDTHH:MM): ");
        String fechaHoraStr = scanner.nextLine();
        LocalDateTime fechaHora;
        try {
            fechaHora = LocalDateTime.parse(fechaHoraStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Formato de fecha y hora incorrecto. Use el formato YYYY-MM-DDTHH:MM.");
            return;
        }
        System.out.print("Motivo: ");
        String motivo = scanner.nextLine();
        System.out.print("ID del doctor: ");
        int idDoctor = scanner.nextInt();
        System.out.print("ID del paciente: ");
        int idPaciente = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        Doctor doctor = buscarDoctor(idDoctor);
        Paciente paciente = buscarPaciente(idPaciente);

        if (doctor == null || paciente == null) {
            System.out.println("Doctor o paciente no encontrados");
            return;
        }

        Cita cita = new Cita(id, fechaHora, motivo, doctor, paciente);
        agregarCita(cita);

        try {
            guardarCitas();
        } catch (IOException e) {
            System.out.println("Error guardando citas: " + e.getMessage());
        }

        System.out.println("Cita creada exitosamente");
    }

    public static void main(String[] args) {
        SistemaCitas sistema = new SistemaCitas();
        sistema.ejecutar();
    }
}
