import dao.*;
import modelo.Usuario;
import modelo.Recurso;
import modelo.Solicitud;
import modelo.ReservaSala;

import java.util.List;
import java.util.Scanner;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        System.out.println("===== SISTEMA DE BIBLIOTECA UNIVERSITARIA =====");

        int intentos = 0;
        Usuario u = null;

        // -----------------------------
        // LOGIN (3 intentos)
        // -----------------------------
        while (intentos < 3 && u == null) {
            System.out.print("Usuario: ");
            String user = sc.nextLine();
            System.out.print("Contraseña: ");
            String pass = sc.nextLine();

            u = usuarioDAO.validarLogin(user, pass);

            if (u == null) {
                intentos++;
                System.out.println(" Credenciales inválidas. Intento " + intentos + "/3");

                if (intentos == 3) {
                    System.out.println(" Demasiados intentos fallidos.");
                    return;
                }
            }
        }

        if (u == null) return;

        // -----------------------------
        // PRIMER INICIO - CAMBIO DE CONTRASEÑA
        // -----------------------------
        if (u.isPrimerInicio()) {
            System.out.println("\n Es su primer inicio. Debe cambiar su contraseña.");
            System.out.print("Nueva contraseña: ");
            String nueva = sc.nextLine();

            if (usuarioDAO.cambiarPassword(u.getId(), nueva)) {
                System.out.println(" Contraseña cambiada. Inicie sesión nuevamente.");
                return;
            } else {
                System.out.println(" Error cambiando contraseña.");
                return;
            }
        }

        System.out.println("\nBienvenido " + u.getUsername() + " (" + u.getRol() + ")");

        String rol = u.getRol().toLowerCase();

        if (rol.contains("admin")) menuAdministrador(sc, usuarioDAO);
        else if (rol.contains("alumno") || rol.contains("estudiante")) menuEstudiante(sc, u);
        else if (rol.contains("profesor")) menuProfesor(sc, u);
        else if (rol.contains("bibliotec")) menuBibliotecario(sc);
        else System.out.println("Rol no reconocido.");
    }

    // ==========================================================
    //                      MENU ADMINISTRADOR
    // ==========================================================
    private static void menuAdministrador(Scanner sc, UsuarioDAO dao) {
        while (true) {
            System.out.println("\n--- MENÚ ADMINISTRADOR ---");
            System.out.println("1. Crear usuario");
            System.out.println("2. Listar usuarios");
            System.out.println("3. Cambiar contraseña");
            System.out.println("4. Eliminar usuario");
            System.out.println("5. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1 -> crearUsuario(sc, dao);
                case 2 -> listarUsuarios(dao);
                case 3 -> cambiarPasswordUsuario(sc, dao);
                case 4 -> eliminarUsuario(sc, dao);
                case 5 -> { return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void crearUsuario(Scanner sc, UsuarioDAO dao) {
        try {
            System.out.print("Código Univ.: ");
            String codigo = sc.nextLine();
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Apellido: ");
            String apellido = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Nuevo usuario: ");
            String username = sc.nextLine();
            System.out.print("Contraseña inicial: ");
            String password = sc.nextLine();
            System.out.print("Rol (Alumno/Profesor/Bibliotecario/Administrador): ");
            String rol = sc.nextLine();

            if (dao.crearUsuarioConPersona(codigo, nombre, apellido, email, username, password, rol))
                System.out.println("✔ Usuario creado.");
            else
                System.out.println(" Error creando usuario.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarUsuarios(UsuarioDAO dao) {
        List<Usuario> lista = dao.listarUsuarios();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios.");
            return;
        }
        lista.forEach(System.out::println);
    }

    private static void cambiarPasswordUsuario(Scanner sc, UsuarioDAO dao) {
        System.out.print("ID usuario: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Nueva contraseña: ");
        String nueva = sc.nextLine();

        if (dao.cambiarPassword(id, nueva))
            System.out.println("✔ Contraseña cambiada.");
        else
            System.out.println(" No se pudo cambiar.");
    }

    private static void eliminarUsuario(Scanner sc, UsuarioDAO dao) {
        System.out.print("ID usuario a eliminar: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("¿Confirmar? (s/n): ");
        if (!sc.nextLine().equalsIgnoreCase("s")) return;

        if (dao.eliminarUsuario(id))
            System.out.println("🗑 Usuario eliminado.");
        else
            System.out.println(" No se pudo eliminar.");
    }

    // ==========================================================
    //                        MENU ESTUDIANTE
    // ==========================================================
    private static void menuEstudiante(Scanner sc, Usuario u) {

        SolicitudDAO solDAO = new SolicitudDAO();
        RecursoDAO recursoDAO = new RecursoDAO();
        ReservaSalaDAO salaDAO = new ReservaSalaDAO();

        while (true) {
            System.out.println("\n--- MENÚ ESTUDIANTE ---");
            System.out.println("1. Solicitar préstamo");
            System.out.println("2. Reservar sala");
            System.out.println("3. Ver mis solicitudes");
            System.out.println("4. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1 -> solicitarPrestamo(sc, solDAO, recursoDAO, u);
                case 2 -> reservarSala(sc, salaDAO, u);
                case 3 -> verSolicitudesUsuario(solDAO, u);
                case 4 -> { return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    // ==========================================================
    //                       MENU PROFESOR
    // ==========================================================
    private static void menuProfesor(Scanner sc, Usuario u) {

        SolicitudDAO solDAO = new SolicitudDAO();
        RecursoDAO recursoDAO = new RecursoDAO();
        ReservaSalaDAO salaDAO = new ReservaSalaDAO();

        while (true) {
            System.out.println("\n--- MENÚ PROFESOR ---");
            System.out.println("1. Solicitar préstamo");
            System.out.println("2. Reservar sala");
            System.out.println("3. Ver mis solicitudes");
            System.out.println("4. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1 -> solicitarPrestamo(sc, solDAO, recursoDAO, u);
                case 2 -> reservarSala(sc, salaDAO, u);
                case 3 -> verSolicitudesUsuario(solDAO, u);
                case 4 -> { return; }
                default -> System.out.println(" Opción no válida.");
            }
        }
    }

    // ==========================================================
    //                       MENU BIBLIOTECARIO
    // ==========================================================
    private static void menuBibliotecario(Scanner sc) {

        SolicitudDAO solDAO = new SolicitudDAO();
        ReservaSalaDAO salaDAO = new ReservaSalaDAO();

        while (true) {
            System.out.println("\n--- MENÚ BIBLIOTECARIO ---");
            System.out.println("1. Ver solicitudes de préstamo");
            System.out.println("2. Ver reservas de sala");
            System.out.println("3. Procesar solicitudes (aprobar / rechazar)");
            System.out.println("4. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1 -> solDAO.verSolicitudesPendientes().forEach(System.out::println);
                case 2 -> salaDAO.listarReservas().forEach(System.out::println);
                case 3 -> procesarSolicitudes(sc, solDAO);
                case 4 -> { return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    // ==========================================================
    //  FUNCIONES AUXILIARES (PRÉSTAMOS, SALAS, SOLICITUDES)
    // ==========================================================

    private static void solicitarPrestamo(Scanner sc, SolicitudDAO solDAO, RecursoDAO recursoDAO, Usuario u) {

        System.out.println("\nSeleccione el tipo de material:");
        System.out.println("1. Libro");
        System.out.println("2. Tesis");
        System.out.println("3. Audiolibro");
        System.out.println("4. Multimedia");
        System.out.println("5. Tablet");
        System.out.println("6. PC");
        System.out.println("7. Revista");
        System.out.println("8. DVD");
        System.out.print("Opción: ");

        int tipo = Integer.parseInt(sc.nextLine());

        // Convertimos a String si RecursoDAO requiere tipo como String
        String tipoStr = switch (tipo) {
            case 1 -> "Libro";
            case 2 -> "Tesis";
            case 3 -> "Audiolibro";
            case 4 -> "Multimedia";
            case 5 -> "Tablet";
            case 6 -> "PC";
            case 7 -> "Revista";
            case 8 -> "DVD";
            default -> "";
        };

        List<Recurso> disponibles = recursoDAO.listarDisponiblesPorTipo(tipoStr);

        if (disponibles.isEmpty()) {
            System.out.println("No hay materiales disponibles.");
            return;
        }

        System.out.println("\nMateriales disponibles:");
        disponibles.forEach(System.out::println);

        System.out.print("ID de copia a solicitar: ");
        int copiaID = Integer.parseInt(sc.nextLine());

        if (solDAO.crearSolicitud(u.getId(), "Material", copiaID))
            System.out.println("✔ Solicitud registrada.");
        else
            System.out.println("Error creando solicitud.");
    }

    private static void reservarSala(Scanner sc, ReservaSalaDAO salaDAO, Usuario u) {
        try {
            System.out.print("ID de sala: ");
            int sala = Integer.parseInt(sc.nextLine());
            System.out.print("Fecha inicio (YYYY-MM-DD HH:MM): ");
            String fi = sc.nextLine();
            System.out.print("Fecha fin (YYYY-MM-DD HH:MM): ");
            String ff = sc.nextLine();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Timestamp fechaInicio = new Timestamp(sdf.parse(fi).getTime());
            Timestamp fechaFin = new Timestamp(sdf.parse(ff).getTime());

            if (salaDAO.reservarSala(sala, u.getPersonaId(), fechaInicio, fechaFin))
                System.out.println("✔ Reserva registrada.");
            else
                System.out.println("Error registrando reserva.");
        } catch (ParseException e) {
            System.out.println("Formato de fecha inválido. Use YYYY-MM-DD HH:MM");
        } catch (NumberFormatException e) {
            System.out.println("ID de sala inválido.");
        }
    }

    private static void verSolicitudesUsuario(SolicitudDAO solDAO, Usuario u) {
        solDAO.verSolicitudesUsuario(u.getId()).forEach(System.out::println);
    }

    private static void procesarSolicitudes(Scanner sc, SolicitudDAO solDAO) {
        List<Solicitud> lista = solDAO.verSolicitudesPendientes();
        lista.forEach(System.out::println);

        System.out.print("ID de solicitud a procesar: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Nuevo estado (Aprobado / Rechazado): ");
        String estado = sc.nextLine();

        if (solDAO.actualizarEstadoSolicitud(id, estado))
            System.out.println("Estado actualizado.");
        else
            System.out.println("Error actualizando estado.");
    }
}
