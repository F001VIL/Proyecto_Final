import dao.*;
import modelo.Usuario;
import modelo.Video;
import servicio.*;
import modelo.AccesoDigital;
import modelo.Book;
import modelo.Digital;
import modelo.Ebook;
import modelo.Journal;
import modelo.Material;
import modelo.Penalidad;
import modelo.Physical;
import modelo.Prestamo;
import modelo.PrestamoDigitalResumen;
import modelo.Recurso;
import modelo.RecursoCopia;
import modelo.Solicitud;
import modelo.Thesis;
import modelo.ReservaSala;
import modelo.FacilityResource;
import modelo.Room;
import modelo.CampusSpace;

import java.util.List;
import java.util.Scanner;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;


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
            System.out.println("4. Actualizar usuario");
            System.out.println("5. Eliminar usuario");
            System.out.println("6. Gestionar monitores");
            System.out.println("7. Gestionar teclados");
            System.out.println("8. Gestionar mouses");
            System.out.println("9. Gestionar tarjetas gráficas");
            System.out.println("10. Gestionar computadoras de escritorio");
            System.out.println("11. Gestionar laptops");
            System.out.println("12. Gestionar tablets");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1 -> crearUsuario(sc, dao);
                case 2 -> listarUsuarios(dao);
                case 3 -> cambiarPasswordUsuario(sc, dao);
                case 4 -> actualizarUsuario(sc, dao);
                case 5 -> eliminarUsuario(sc, dao);
                case 6 -> new MonitorManager().run();
                case 7 -> new KeyboardManager().run();
                case 8 -> new MouseManager().run();
                case 9 -> new GraphicCardService().run();
                case 10 -> new DesktopPcService().run();
                case 11 -> new LaptopService().run();
                case 12 -> new TabletService().run();
                case 0 -> { return; }
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

    private static void actualizarUsuario(Scanner sc, UsuarioDAO usuarioDAO) {
        System.out.println("\n--- ACTUALIZAR USUARIO ---");

        
        listarUsuarios(usuarioDAO);  

        System.out.print("Ingrese el ID del usuario a modificar: ");
        int usuarioId = Integer.parseInt(sc.nextLine());

        
        Usuario actual = usuarioDAO.obtenerPorId(usuarioId);
        if (actual == null) {
            System.out.println("No se encontró usuario con ese ID.");
            return;
        }

        System.out.println("\nDeje vacío y presione Enter para NO cambiar ese campo.");

        
        System.out.println("\n--- Datos de cuenta ---");
        System.out.println("Username actual: " + actual.getUsername());
        System.out.print("Nuevo username: ");
        String nuevoUsername = sc.nextLine().trim();
        if (nuevoUsername.isEmpty()) {
            nuevoUsername = actual.getUsername();
        }

        System.out.println("Rol actual: " + actual.getRol());
        System.out.print("Nuevo rol (Administrador / Alumno / Bibliotecario): ");
        String nuevoRol = sc.nextLine().trim();
        if (nuevoRol.isEmpty()) {
            nuevoRol = actual.getRol();
        }

        System.out.println("Activo actual (1 = activo, 0 = inactivo): " + (actual.isActivo() ? 1 : 0));
        System.out.print("Nuevo estado activo (1/0, vacío = mantener): ");
        String activoStr = sc.nextLine().trim();
        boolean nuevoActivo;
        if (activoStr.isEmpty()) {
            nuevoActivo = actual.isActivo();
        } else {
            nuevoActivo = activoStr.equals("1");
        }


        System.out.println("\n--- Datos personales ---");
        System.out.println("Nombre actual: " + actual.getNombre());
        System.out.print("Nuevo nombre: ");
        String nuevoNombre = sc.nextLine().trim();
        if (nuevoNombre.isEmpty()) {
            nuevoNombre = actual.getNombre();
        }

        System.out.println("Apellido actual: " + actual.getApellido());
        System.out.print("Nuevo apellido: ");
        String nuevoApellido = sc.nextLine().trim();
        if (nuevoApellido.isEmpty()) {
            nuevoApellido = actual.getApellido();
        }

        System.out.println("Fecha de nacimiento actual: " + (actual.getFechaNacimiento() != null ? actual.getFechaNacimiento() : "NULL"));
        System.out.print("Nueva fecha de nacimiento (yyyy-MM-dd): ");
        String fechaStr = sc.nextLine().trim();
        LocalDate nuevaFechaNac = actual.getFechaNacimiento();
        if (!fechaStr.isEmpty()) {
            try {
                nuevaFechaNac = LocalDate.parse(fechaStr);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Se dejará NULL.");
            }
        }

        System.out.println("Email actual: " + actual.getEmail());
        System.out.print("Nuevo email: ");
        String nuevoEmail = sc.nextLine().trim();
        if (nuevoEmail.isEmpty()) {
            nuevoEmail = actual.getEmail();
        }

        // ====== Guardar cambios ======
        boolean ok = usuarioDAO.actualizarUsuarioConPersona(
                usuarioId,
                nuevoUsername,
                nuevoRol,
                nuevoActivo,
                actual.getPersonaId(),
                nuevoNombre,
                nuevoApellido,
                nuevaFechaNac,
                nuevoEmail
        );

        if (ok) {
            System.out.println("✔ Usuario actualizado correctamente.");
        } else {
            System.out.println("✖ No se pudo actualizar el usuario.");
        }
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
            System.out.println("3. Gestionar mis reservas de sala");
            System.out.println("4. Ver mis solicitudes");
            System.out.println("5. Devolver material digital");
            System.out.println("6. Gestionar préstamo de recursos tecnológicos");
            System.out.println("7. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1 -> solicitarPrestamo(sc, solDAO, recursoDAO, u);
                case 2 -> {
                    listarSalas(new SalaDAO());
                    reservarSala(sc, salaDAO, u);
                }
                case 3 -> gestionarMisReservasSala(sc, salaDAO, u);
                case 4 -> verSolicitudesUsuario(solDAO, u);
                case 5 -> devolverMaterialDigital(sc, u.getId());
                case 6 -> new LoanStudentManager().run(u.getId());
                case 7 -> { return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void devolverMaterialDigital(Scanner sc, int usuarioId) {

        PrestamoDigitalDAO prestamoDigitalDAO = new PrestamoDigitalDAO();

        List<PrestamoDigitalResumen> lista =
                prestamoDigitalDAO.listarPrestamosActivosPorPersona(usuarioId);

        if (lista.isEmpty()) {
            System.out.println("No tiene préstamos digitales activos.");
            return;
        }

        System.out.println("\n--- PRÉSTAMOS DIGITALES ACTIVOS ---");
        for (PrestamoDigitalResumen p : lista) {
            System.out.println("ID préstamo: " + p.getPrestamoDigitalId()
                    + " | Título: " + p.getTitulo()
                    + " | Vence: " + p.getFechaVencimiento());
        }

        System.out.print("Ingrese el ID del préstamo digital a devolver: ");
        int id = Integer.parseInt(sc.nextLine());

        if (prestamoDigitalDAO.devolverPrestamoDigital(id)) {
            System.out.println("✔ Material digital devuelto. Ya puede solicitar otro.");
        } else {
            System.out.println("No se pudo registrar la devolución digital.");
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
            System.out.println("3. Gestionar mis reservas de sala");
            System.out.println("4. Ver mis solicitudes");
            System.out.println("5. Devolver material digital");
            System.out.println("6. Gestionar préstamo de recursos tecnológicos");
            System.out.println("7. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1 -> solicitarPrestamo(sc, solDAO, recursoDAO, u);
                case 2 -> {
                    listarSalas(new SalaDAO());
                    reservarSala(sc, salaDAO, u);
                }
                case 3 -> gestionarMisReservasSala(sc, salaDAO, u);
                case 4 -> verSolicitudesUsuario(solDAO, u);
                case 5 -> devolverMaterialDigital(sc, u.getId());
                case 6 -> new LoanStudentManager().run(u.getId());
                case 7 -> { return; }
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
        RecursoDAO recursoDAO = new RecursoDAO();
        RecursoCopiaDAO recursoCopiaDAO = new RecursoCopiaDAO();
        

        while (true) {
            System.out.println("\n--- MENÚ BIBLIOTECARIO ---");
            System.out.println("1. Registrar material"); 
            System.out.println("2. Ver materiales disponibles"); 
            System.out.println("3. Ver solicitudes de préstamo");
            System.out.println("4. Ver reservas de sala");
            System.out.println("5. Procesar solicitudes (aprobar / rechazar)");
            System.out.println("6. Registrar devolución de préstamo");
            System.out.println("7. Registrar penalidad posterior");
            System.out.println("8. Modificar monto de penalidad");
            System.out.println("9. Ver penalidades y registrar pago");
            System.out.println("10. Registrar sala o campus");
            System.out.println("11. Ver salas y campus");
            System.out.println("12. Modificar sala o campus");
            System.out.println("13. Eliminar sala o campus");
            System.out.println("14. Eliminar recurso");
            System.out.println("15. Modificar material / copia");
            System.out.println("16. Eliminar reserva de sala");
            System.out.println("17. Ver historial de préstamos");
            System.out.println("18. Ver historial de penalidades");
            System.out.println("19. Salir");
            System.out.print("Opción: ");

            int op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1 -> registrarMaterial(sc, recursoDAO);
                case 2 -> listarMaterialesDisponibles(recursoDAO, recursoCopiaDAO);
                case 3 -> solDAO.verSolicitudesPendientes().forEach(System.out::println);
                case 4 -> salaDAO.obtenerReservas().forEach(System.out::println);
                case 5 -> procesarSolicitudes(sc, solDAO);
                case 6 -> registrarDevolucion(sc);
                case 7 -> registrarPenalidadPosterior(sc);
                case 8 -> modificarMontoPenalidad(sc);
                case 9 -> gestionarPenalidades(sc);
                case 10 -> registrarSala(sc, new SalaDAO());
                case 11 -> listarSalas(new SalaDAO());
                case 12 -> modificarSala(sc, new SalaDAO());
                case 13 -> eliminarSala(sc, new SalaDAO());
                case 14 -> eliminarRecurso(sc, recursoDAO, recursoCopiaDAO);
                case 15 -> modificarMaterialOCopia(sc, recursoDAO, recursoCopiaDAO);
                case 16 -> eliminarReserva(sc, new ReservaSalaDAO());
                case 17 -> verHistorialPrestamos(new PrestamoDAO());
                case 18 -> verHistorialPenalidades(); 
                case 19 -> { return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }
    private static void eliminarRecurso(Scanner sc, RecursoDAO recursoDAO, RecursoCopiaDAO recursoCopiaDAO) {
        System.out.println("\n=== ELIMINAR RECURSO ===");

        List<Recurso> disponibles = recursoDAO.listarDisponibles();

        if (disponibles.isEmpty()) {
            System.out.println("No hay recursos registrados.");
            return;
        }

        System.out.println("\n--- RECURSOS DISPONIBLES ---");
        disponibles.forEach(r ->
                System.out.println(
                        "ID: " + r.getRecursoId() +
                                " | Título: " + r.getTitulo() +
                                " | Tipo: " + r.getTipoRecursoId()
                )
        );

        System.out.print("\nIngrese el ID del recurso a eliminar: ");
        int recursoId = Integer.parseInt(sc.nextLine());

        System.out.print("¿Está seguro que desea eliminarlo? (s/n): ");
        if (!sc.nextLine().equalsIgnoreCase("s")) {
            System.out.println("Cancelado.");
            return;
        }

        if (recursoDAO.eliminarRecurso(recursoId)) {
            System.out.println("✔ Recurso eliminado correctamente.");
        } else {
            System.out.println("Error eliminando recurso.");
        }

        System.out.println("\nLista actualizada de recursos:");
        listarMaterialesDisponibles(recursoDAO, recursoCopiaDAO);
    }

    private static void listarMaterialesDisponibles(RecursoDAO recursoDAO, RecursoCopiaDAO copiaDAO) {
        System.out.println("\n--- MATERIALES DISPONIBLES ---");
        List<Recurso> recursos = recursoDAO.listarTodos();

        if (recursos.isEmpty()) {
            System.out.println("No hay materiales registrados.");
            return;
        }

        for (Recurso r : recursos) {

            String tipoNombre = switch (r.getTipoRecursoId()) {
                case 1 -> "Libro";
                case 2 -> "Tesis";
                case 3 -> "Libro virtual";
                case 4 -> "Multimedia";
                case 5 -> "Revista";
                default -> "Desconocido";
            };

            System.out.println("\nID: " + r.getRecursoId()
                    + " | Título: " + r.getTitulo()
                    + " | Tipo: " + tipoNombre);

            // Fisicos → mostrar copias
            if (r.getTipoRecursoId() == 1 || r.getTipoRecursoId() == 5) {
                int total = copiaDAO.contarCopiasFisicas(r.getRecursoId());
                int disponibles = copiaDAO.contarCopiasDisponibles(r.getRecursoId());

                System.out.println("  Copias físicas: " + total
                        + " | Disponibles: " + disponibles);
            } else {
                // Digital
                System.out.println("  Acceso digital");
            }
        }
    }

    private static void registrarMaterial(Scanner sc, RecursoDAO recursoDAO) {
        RecursoCopiaDAO copiaDAO = new RecursoCopiaDAO();

        System.out.println("\n--- REGISTRAR MATERIAL ---");
        System.out.println("1. Libro");
        System.out.println("2. Tesis");
        System.out.println("3. Audiolibro");
        System.out.println("4. Multimedia (video)");
        System.out.println("5. Revista");
        System.out.print("Tipo: ");
        int tipo = Integer.parseInt(sc.nextLine());

        System.out.print("Título: ");
        String titulo = sc.nextLine();
        System.out.print("Autor: ");
        String autor = sc.nextLine();
        System.out.print("Idioma: ");
        String language = sc.nextLine();
        System.out.print("Año de publicación: ");
        int anio = Integer.parseInt(sc.nextLine());

        java.time.LocalDate fechaPub = java.time.LocalDate.of(anio, 1, 1);

        Material material;

        switch (tipo) {
            case 1 -> {
                System.out.print("Formato (tapa dura, rústica, etc.): ");
                String formato = sc.nextLine();
                System.out.print("Sede: ");
                String sede = sc.nextLine();
                System.out.print("Stock: ");
                int stock = Integer.parseInt(sc.nextLine());
                System.out.print("Páginas: ");
                int paginas = Integer.parseInt(sc.nextLine());
                System.out.print("ISBN: ");
                String isbn = sc.nextLine();
                System.out.print("Editorial: ");
                String editorial = sc.nextLine();
                System.out.print("Edición: ");
                String edicion = sc.nextLine();

                material = new Book(0, titulo, autor, language, fechaPub,formato, sede, stock, isbn, editorial, edicion, paginas);
            }
            case 2 -> {
                System.out.print("Formato (PDF, DOCX, etc.): ");
                String formato = sc.nextLine();
                System.out.print("Tamaño del archivo (MB): ");
                int fileSize = Integer.parseInt(sc.nextLine());
                System.out.print("País: ");
                String country = sc.nextLine();
                System.out.print("Universidad: ");
                String university = sc.nextLine();
                System.out.print("Grado (Bachiller, Magíster, etc.): ");
                String degree = sc.nextLine();
                System.out.print("ISSN (si tiene): ");
                String issn = sc.nextLine();

                material = new Thesis(0, titulo, autor, language, fechaPub, formato, fileSize, country, university, degree, issn);
            }
            case 3 -> {
                System.out.print("Formato (MP3, M4B, etc.): ");
                String formato = sc.nextLine();
                System.out.print("Tamaño del archivo (MB): ");
                int fileSize = Integer.parseInt(sc.nextLine());
                System.out.print("Nº de pistas / páginas lógicas: ");
                int paginas = Integer.parseInt(sc.nextLine());
                System.out.print("ISBN (si tiene): ");
                String isbn = sc.nextLine();
                System.out.print("Editorial: ");
                String editorial = sc.nextLine();
                System.out.print("Edición: ");
                String edicion = sc.nextLine();

                material = new Ebook(0, titulo, autor, language, fechaPub,formato, fileSize, paginas, isbn, editorial, edicion);
            }
            case 4 -> {
                System.out.print("Formato del archivo (mp4, mkv, etc.): ");
                String formato = sc.nextLine();
                System.out.print("Tamaño del archivo (MB): ");
                int fileSize = Integer.parseInt(sc.nextLine());
                System.out.print("Duración (minutos): ");
                int minutos = Integer.parseInt(sc.nextLine());
                System.out.print("Resolución (1080p, 4K, etc.): ");
                String resolucion = sc.nextLine();

                material = new Video( 0, titulo, autor, language, fechaPub,formato, fileSize, minutos, resolucion);
            }
            case 5 -> {
                System.out.print("Formato (revista impresa): ");
                String formato = sc.nextLine();
                System.out.print("Sede: ");
                String sede = sc.nextLine();
                System.out.print("Stock: ");
                int stock = Integer.parseInt(sc.nextLine());
                System.out.print("Volumen (número): ");
                int volume = Integer.parseInt(sc.nextLine());
                System.out.print("ISSN: ");
                String issn = sc.nextLine();
                System.out.print("Editorial: ");
                String editorial = sc.nextLine();
                System.out.print("Edición / número especial: ");
                String edicion = sc.nextLine();

                material = new Journal(0, titulo, autor, language, fechaPub,formato, sede, stock, issn, editorial, edicion, volume);
            }
            default -> {
                System.out.println("Tipo no válido.");
                return;
            }
        }

        if (material instanceof Digital d) {
            System.out.print("URL de acceso: ");
            String url = sc.nextLine();
            d.setUrlAcceso(url);

            System.out.print("Licencia: ");
            String licencia = sc.nextLine();
            d.setLicencia(licencia);

            System.out.print("Usuarios concurrentes: ");
            int usuariosConc = Integer.parseInt(sc.nextLine());
            d.setUsuariosConcurrentes(usuariosConc);
        }
        
        System.out.println("\nCitación generada:");
        System.out.println(material.getCitacion());

        String isbnIssn = null;
        String edicion = null;
        String editorial = null;
        Integer anioPublicacion = null;
        String idioma = null;


        if (material.getPublicationDate() != null) {
            anioPublicacion = material.getPublicationDate().getYear();
        }

        idioma = language;

        // Según el tipo de material, extraemos ISBN / ISSN / editorial / edición
        if (material instanceof Physical p) {
            isbnIssn = p.getIsbn();
            editorial = p.getEditorial();
            edicion = p.getEdition();
        }

        
        if (material instanceof Ebook e) {
            isbnIssn = e.getIsbn();
            editorial = e.getEditorial();
            edicion = e.getEdition();
        }

        if (material instanceof Thesis t) {
            if (t.getIssn() != null && !t.getIssn().isBlank()) {
                isbnIssn = t.getIssn();
            }
        }


        String descripcion = "Registrado por bibliotecario";
        int tipoRecursoId = switch (tipo) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5 -> 5;
            default -> 0;
        };

        int recursoId = recursoDAO.insertarRecurso(titulo,descripcion,tipoRecursoId,isbnIssn, edicion, editorial ,anioPublicacion,idioma);
        if (recursoId <= 0) {
            System.out.println("Error al registrar el recurso en la BD.");
            return;
        }

        RecursoAutorDAO recursoAutorDAO = new RecursoAutorDAO();
        boolean okAutor = recursoAutorDAO.insertarAutor(recursoId, material.getAuthor());

        if (!okAutor) {
            System.out.println("Advertencia: no se pudo registrar el autor en la tabla RecursoAutor.");
        }

        if (material instanceof Thesis t) {
            TesisDAO tesisDAO = new TesisDAO();
            boolean okTesis = tesisDAO.insertarTesisDetalle(
                    recursoId,
                    t.getCountry(),
                    t.getUniversity(),
                    t.getDegree()
            );
            if (!okTesis) {
                System.out.println("Advertencia: la tesis se registró en Recurso, "+ "pero no se pudo guardar el detalle (TesisDetalle).");
            }
        }


        if (material instanceof Physical p) {

            int stock = p.getStock();
            for (int i = 1; i <= stock; i++) {
                System.out.print("Código de copia " + i + " (ej. LIB-0001): ");
                String codigo = sc.nextLine();
                System.out.print("Ubicación física de la copia " + i + " (ej. Estante A1): ");
                String ubicacion = sc.nextLine();

                if (copiaDAO.insertarCopiaFisica(recursoId, codigo, ubicacion)) {
                    System.out.println("✔ Copia " + i + " registrada.");
                } else {
                    System.out.println("Error al registrar la copia " + i + ".");
                }
            }

            System.out.println("✔ Material físico registrado con éxito.");

        } else if (material instanceof Digital d) {

            boolean ok = recursoDAO.insertarAccesoDigital(
                    recursoId,
                    d.getUrlAcceso(),
                    d.getLicencia(),
                    d.getUsuariosConcurrentes()
            );
            if (ok) {
                System.out.println("✔ Material digital registrado con éxito.");
            } else {
                System.out.println("Error al registrar acceso digital.");
            }
        } else {
            System.out.println("Advertencia: tipo digital sin datos de acceso.");
        }
    }

    private static void modificarMaterialOCopia(Scanner sc,RecursoDAO recursoDAO,RecursoCopiaDAO recursoCopiaDAO) {
        // 1) Mostrar lista de materiales 
        List<Recurso> lista = recursoDAO.listarTodos();

        if (lista.isEmpty()) {
            System.out.println("No hay recursos registrados.");
            return;
        }

        System.out.println("\n--- MATERIALES REGISTRADOS ---");
        lista.forEach(r ->
                System.out.println("ID: " + r.getRecursoId()
                        + " | Título: " + r.getTitulo()
                        + " | Tipo: " + r.getTipoRecursoId())
        );

        System.out.print("Ingrese el ID del recurso a modificar: ");
        int recursoId = Integer.parseInt(sc.nextLine());

        Recurso recurso = recursoDAO.obtenerPorId(recursoId);
        if (recurso == null) {
            System.out.println("No se encontró el recurso.");
            return;
        }

        int tipo = recurso.getTipoRecursoId();

        boolean esDigital = (tipo == 2 || tipo == 3 || tipo == 4);

        System.out.println("\nRecurso seleccionado: " + recurso.getTitulo()
                + " | Tipo: " + recurso.getTipoRecursoId());

        System.out.println("¿Qué desea modificar?");
        System.out.println("1. Datos del material");
        System.out.println("2. Copia física (estante / código / estado)");
        if (esDigital) {
            System.out.println("3. Acceso digital (URL / licencia / usuarios)");
        }
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");
        int op = Integer.parseInt(sc.nextLine());

        switch (op) {
            case 1 -> modificarDatosMaterial(sc, recursoDAO, recurso);
            case 2 -> modificarCopiaFisica(sc, recursoCopiaDAO, recursoId); 
            case 3 -> {
                if (!esDigital) {
                    System.out.println("Opción no válida para recursos físicos.");
                } else {
                    modificarAccesoDigital(sc, recursoDAO, recurso.getRecursoId());
                }
            }
            case 0 -> { return; }
            default -> System.out.println("Opción no válida.");
        }
    }

    private static void modificarDatosMaterial(Scanner sc,RecursoDAO recursoDAO,Recurso recurso) {

        System.out.println("\n--- MODIFICAR DATOS DEL MATERIAL ---");
        System.out.println("(Deje vacío y presione Enter para mantener el valor actual)");

        System.out.println("Título actual: " + recurso.getTitulo());
        System.out.print("Nuevo título: ");
        String nuevoTitulo = sc.nextLine();
        if (!nuevoTitulo.isBlank()) {
            recurso.setTitulo(nuevoTitulo);
        }

        System.out.println("Descripción actual: " + recurso.getDescripcion());
        System.out.print("Nueva descripción: ");
        String nuevaDesc = sc.nextLine();
        if (!nuevaDesc.isBlank()) {
            recurso.setDescripcion(nuevaDesc);
        }

        System.out.println("ISBN/ISSN actual: " + recurso.getIsbn());
        System.out.print("Nuevo ISBN/ISSN: ");
        String nuevoIsbn = sc.nextLine();
        if (!nuevoIsbn.isBlank()) {
            recurso.setIsbn(nuevoIsbn);
        }

        System.out.println("Edición actual: " + recurso.getEdicion());
        System.out.print("Nueva edición: ");
        String nuevaEdicion = sc.nextLine();
        if (!nuevaEdicion.isBlank()) {
            recurso.setEdicion(nuevaEdicion);
        }

        System.out.println("Editorial actual: " + recurso.getEditorial());
        System.out.print("Nueva editorial: ");
        String nuevaEditorial = sc.nextLine();
        if (!nuevaEditorial.isBlank()) {
            recurso.setEditorial(nuevaEditorial);
        }

        System.out.println("Año publicación actual: " + recurso.getAnoPublicacion());
        System.out.print("Nuevo año de publicación: ");
        String nuevoAnioStr = sc.nextLine();
        if (!nuevoAnioStr.isBlank()) {
            try {
                int nuevoAnio = Integer.parseInt(nuevoAnioStr);
                recurso.setAnoPublicacion(nuevoAnio);
            } catch (NumberFormatException e) {
                System.out.println("Año inválido. Se mantiene el actual.");
            }
        }

        System.out.println("Idioma actual: " + recurso.getIdioma());
        System.out.print("Nuevo idioma: ");
        String nuevoIdioma = sc.nextLine();
        if (!nuevoIdioma.isBlank()) {
            recurso.setIdioma(nuevoIdioma);
        }

        boolean ok = recursoDAO.actualizarDatosBasicos(recurso);
        if (ok) {
            System.out.println("✔ Material actualizado correctamente.");
        } else {
            System.out.println("✖ No se pudo actualizar el material.");
        }
    }

    private static void modificarCopiaFisica(Scanner sc,RecursoCopiaDAO copiaDAO,int recursoId) {

        System.out.println("\n--- MODIFICAR COPIA FÍSICA ---");

        // 1) Listar copias del recurso
        List<RecursoCopia> copias = copiaDAO.listarCopiasPorRecurso(recursoId);

        if (copias.isEmpty()) {
            System.out.println("Este recurso no tiene copias físicas registradas.");
            return;
        }


        System.out.println("Copias del recurso:");
        for (RecursoCopia c : copias) {
            System.out.println(
                "CopiaID: " + c.getCopiaId() +
                " | Código: " + c.getCodigoCopia() +
                " | Ubicación: " + c.getUbicacion() +
                " | EstadoID: " + c.getEstadoId()
            );
        }

        // 2) Elegir copia
        System.out.print("Ingrese el ID de la copia a modificar: ");
        int copiaId = Integer.parseInt(sc.nextLine());

        RecursoCopia copiaSeleccionada = null;
        for (RecursoCopia c : copias) {
            if (c.getCopiaId() == copiaId) {
                copiaSeleccionada = c;
                break;
            }
        }

        if (copiaSeleccionada == null) {
            System.out.println("No se encontró la copia con ese ID.");
            return;
        }

        // Verificar estado de la copia
        int estado = copiaSeleccionada.getEstadoId();   
        if (estado == 2 || estado == 3) {               
            System.out.println("La copia está prestada o reservada.");
            System.out.println("No se permite modificar código ni ubicación.");
            return;
        }

        
        // 3) Pedir nuevos datos 
        System.out.print("Nuevo código de copia (deje vacío para mantener): ");
        String nuevoCodigo = sc.nextLine();

        System.out.print("Nueva ubicación (deje vacío para mantener): ");
        String nuevaUbicacion = sc.nextLine();

        // 4) Validar que haya al menos un cambio
        if (nuevoCodigo.isBlank() && nuevaUbicacion.isBlank()) {
            System.out.println("No se ingresaron cambios. Operación cancelada.");
            return;
        }

        // Para los que dejó en blanco, necesitamos leer los valores actuales
        
        if (nuevoCodigo.isBlank() || nuevaUbicacion.isBlank()) {
            RecursoCopia copiaActual = null;
            for (RecursoCopia c : copias) {
                if (c.getCopiaId() == copiaId) {
                    copiaActual = c;
                    break;
                }
            }
            if (copiaActual == null) {
                System.out.println("No se encontró la copia seleccionada.");
                return;
            }

            if (nuevoCodigo.isBlank()) {
                nuevoCodigo = copiaActual.getCodigoCopia();
            }
            if (nuevaUbicacion.isBlank()) {
                nuevaUbicacion = copiaActual.getUbicacion();
            }
        }

        boolean ok = copiaDAO.actualizarCodigoUbicacion(copiaId, nuevoCodigo, nuevaUbicacion);
        if (ok) {
            System.out.println("✔ Copia actualizada correctamente.");
        } else {
            System.out.println("✖ No se pudo actualizar la copia.");
        }
    }


    private static void modificarAccesoDigital(Scanner sc,RecursoDAO recursoDAO,int recursoId) {

        AccesoDigital acceso = recursoDAO.obtenerAccesoDigitalPorRecurso(recursoId);

        if (acceso == null) {
            System.out.println("Este recurso no tiene acceso digital configurado.");
            return;
        }

        System.out.println("\n--- ACCESO DIGITAL ACTUAL ---");
        System.out.println("URL: " + acceso.getUrlAcceso());
        System.out.println("Licencia: " + acceso.getLicencia());
        System.out.println("Usuarios concurrentes: " + acceso.getUsuariosConcurrentes());

        System.out.print("Nueva URL (enter para mantener): ");
        String nuevaUrl = sc.nextLine().trim();
        if (!nuevaUrl.isEmpty()) {
            acceso.setUrlAcceso(nuevaUrl);
        }

        System.out.print("Nueva licencia (enter para mantener): ");
        String nuevaLicencia = sc.nextLine().trim();
        if (!nuevaLicencia.isEmpty()) {
            acceso.setLicencia(nuevaLicencia);
        }

        System.out.print("Nuevos usuarios concurrentes (enter para mantener): ");
        String usuariosTxt = sc.nextLine().trim();
        if (!usuariosTxt.isEmpty()) {
            try {
                acceso.setUsuariosConcurrentes(Integer.parseInt(usuariosTxt));
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Se mantienen los usuarios actuales.");
            }
        }

        if (recursoDAO.actualizarAccesoDigital(acceso)) {
            System.out.println("✔ Acceso digital actualizado correctamente.");
        } else {
            System.out.println("✘ No se pudo actualizar el acceso digital.");
        }
    }




    // ==========================================================
    //  FUNCIONES AUXILIARES (PRÉSTAMOS, SALAS, SOLICITUDES)
    // ==========================================================


    private static void solicitarPrestamo(Scanner sc, SolicitudDAO solDAO, RecursoDAO recursoDAO, Usuario u) {

        PenalidadDAO penalidadDAO = new PenalidadDAO();
        if (penalidadDAO.tienePenalidadActiva(u.getId())) {
            System.out.println("❌ No puede solicitar préstamo: tiene una penalidad activa.");
            return;
        }
        
        
        System.out.println("\nSeleccione el tipo de material:");
        System.out.println("1. Libro");
        System.out.println("2. Tesis");
        System.out.println("3. LibroVirtual");
        System.out.println("4. Multimedia");
        System.out.println("5. Revista");
        System.out.print("Opción: ");

        int tipo = Integer.parseInt(sc.nextLine());

        String tipoStr = switch (tipo) {
            case 1 -> "Libro";
            case 2 -> "Tesis";
            case 3 -> "Libro virtual";
            case 4 -> "Multimedia";
            case 5 -> "Revista";
            default -> "";
        };

        List<Recurso> disponibles = recursoDAO.listarDisponiblesPorTipo(tipoStr);

        if (disponibles.isEmpty()) {
            System.out.println("No hay materiales disponibles.");
            return;
        }

        boolean esDigital = tipoStr.equalsIgnoreCase("Tesis")
            || tipoStr.equalsIgnoreCase("Libro virtual")
            || tipoStr.equalsIgnoreCase("Multimedia");

        System.out.println("\nMateriales disponibles:");
        

        for (Recurso r : disponibles) {
            if (esDigital) {
                System.out.println(
                        "ID: " + r.getRecursoId() +
                        " | Título: " + r.getTitulo() +
                        " | Acceso digital"
                );
            } else {
                System.out.println(
                        "ID: " + r.getRecursoId() +
                        " | Título: " + r.getTitulo() +
                        " | Copias disponibles: " + r.getStock()
                );
            }
        }

        System.out.print("ID de copia a solicitar: ");
        int copiaID = Integer.parseInt(sc.nextLine());

        if (solDAO.crearSolicitud(u.getId(), tipoStr, copiaID)) {
            System.out.println("✔ Solicitud registrada.");
        } else {
            System.out.println("Error creando solicitud.");
        }
    }

    private static void modificarMontoPenalidad(Scanner sc) {
        PenalidadDAO penalidadDAO = new PenalidadDAO();
        List<Penalidad> lista = penalidadDAO.listarHistorial();

        if (lista.isEmpty()) {
            System.out.println("No hay penalidades registradas.");
            return;
        }

        System.out.println("\n--- PENALIDADES REGISTRADAS ---");
        for (Penalidad p : lista) {
            System.out.println(
                "ID: " + p.getPenalidadId() +
                " | PrestamoID: " + p.getPrestamoId() +
                " | PersonaID: " + p.getPersonaId() +
                " | Monto actual: " + p.getMonto() +
                " | Pagada: " + (p.isPagada() ? "Sí" : "No")
            );
        }

        System.out.print("Ingrese el ID de la penalidad a modificar: ");
        int penalidadId = Integer.parseInt(sc.nextLine());

        System.out.print("Nuevo monto: ");
        BigDecimal nuevoMonto = new BigDecimal(sc.nextLine());

        boolean ok = penalidadDAO.actualizarMonto(penalidadId, nuevoMonto);

        if (ok) {
            System.out.println("✓ Monto de penalidad actualizado correctamente.");
        } else {
            System.out.println("No se pudo actualizar el monto.");
        }
    }

    private static void gestionarMisReservasSala(Scanner sc, ReservaSalaDAO reservaDAO, Usuario u) {

        System.out.println("\n--- GESTIONAR MIS RESERVAS DE SALA ---");

        var reservas = reservaDAO.obtenerPorUsuario(u.getId());

        if (reservas.isEmpty()) {
            System.out.println("No tienes reservas registradas.");
            return;
        }

        // Mostrar reservas
        for (ReservaSala r : reservas) {
            String nombreSala = (r.getRecurso() != null)
                    ? r.getRecurso().getName()
                    : ("SalaID " + r.getSalaId());

            System.out.println("ID Reserva: " + r.getReservaId() +
                            " | Sala: " + nombreSala +
                            " | Inicio: " + r.getFechaInicio() +
                            " | Fin: " + r.getFechaFin() +
                            " | Estado: " + r.getEstadoReserva() +
                            " | Personas: " + r.getNumeroPersonas());
        }

        System.out.print("\nIngrese el ID de la reserva a gestionar: ");
        int id = Integer.parseInt(sc.nextLine());

        // buscar la reserva seleccionada
        ReservaSala seleccionada = null;
        for (ReservaSala r : reservas) {
            if (r.getReservaId() == id) {
                seleccionada = r;
                break;
            }
        }

        if (seleccionada == null) {
            System.out.println("No se encontró una reserva con ese ID entre tus reservas.");
            return;
        }

        // si ya está cancelada, no tiene sentido modificarla
        if ("Cancelada".equalsIgnoreCase(seleccionada.getEstadoReserva())) {
            System.out.println("La reserva ya está cancelada. No se puede modificar.");
            return;
        }

        System.out.println("\n1. Modificar fecha / hora / número de personas");
        System.out.println("2. Cancelar reserva");
        System.out.println("3. Volver");
        System.out.print("Elija opción: ");
        String opStr = sc.nextLine();

        switch (opStr) {
            case "1" -> modificarMiReservaSala(sc, reservaDAO, seleccionada, u);
            case "2" -> cancelarMiReservaSala(sc, reservaDAO, seleccionada, u);
            default -> System.out.println("Volviendo al menú...");
        }
    }

    private static void modificarMiReservaSala(Scanner sc, ReservaSalaDAO reservaDAO,ReservaSala reserva, Usuario u) {

        SalaDAO salaDAO = new SalaDAO();
        FacilityResource sala = salaDAO.obtenerPorId(reserva.getSalaId());

        if (sala == null) {
            System.out.println("No se encontró la sala asociada a la reserva.");
            return;
        }

        System.out.println("\n--- MODIFICAR RESERVA ---");
        System.out.println("Sala: " + sala.getName() + " | Capacidad: " + sala.getCapacity());
        System.out.println("Valores actuales:");
        System.out.println("Inicio: " + reserva.getFechaInicio());
        System.out.println("Fin: " + reserva.getFechaFin());
        System.out.println("Personas: " + reserva.getNumeroPersonas());
        System.out.println("Presione ENTER para mantener un valor.");

        
        int numPersonas;
        while (true) {
            System.out.print("Nuevo número de personas (" + reserva.getNumeroPersonas() + "): ");
            String numStr = sc.nextLine();

            if (numStr.isEmpty()) {
                numPersonas = reserva.getNumeroPersonas();
                break;
            }

            try {
                numPersonas = Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
                continue;
            }

            if (numPersonas <= 0) {
                System.out.println("Debe ser mayor que 0.");
            } else if (numPersonas > sala.getCapacity()) {
                System.out.println("No puede exceder la capacidad (" + sala.getCapacity() + ").");
            } else {
                break;
            }
        }

        
        Timestamp inicio = reserva.getFechaInicio();
        Timestamp fin = reserva.getFechaFin();

        
        System.out.print("Nueva fecha inicio (yyyy-MM-dd HH:mm) o ENTER para mantener: ");
        String fIniStr = sc.nextLine();
        if (!fIniStr.isEmpty()) {
            inicio = leerTimestampDesdeTexto(fIniStr);
            if (inicio == null) {
                System.out.println("Formato inválido. No se realizó la modificación.");
                return;
            }
        }

        
        System.out.print("Nueva fecha fin (yyyy-MM-dd HH:mm) o ENTER para mantener: ");
        String fFinStr = sc.nextLine();
        if (!fFinStr.isEmpty()) {
            fin = leerTimestampDesdeTexto(fFinStr);
            if (fin == null) {
                System.out.println("Formato inválido. No se realizó la modificación.");
                return;
            }
        }

        
        Timestamp ahora = new Timestamp(System.currentTimeMillis());

        if (!inicio.after(ahora)) {
            System.out.println("La fecha de inicio debe ser posterior a la actual.");
            return;
        }

        if (!fin.after(inicio)) {
            System.out.println("La fecha de fin debe ser posterior a la de inicio.");
            return;
        }

        long diffMs = fin.getTime() - inicio.getTime();
        long maxMs = 3L * 60 * 60 * 1000;

        if (diffMs > maxMs) {
            System.out.println("No se puede reservar por más de 3 horas.");
            return;
        }

        
        boolean ok = reservaDAO.actualizarReservaUsuario(
                reserva.getReservaId(), u.getId(), inicio, fin, numPersonas
        );

        if (ok) {
            System.out.println("Reserva modificada correctamente.");
        } else {
            System.out.println("No se pudo modificar la reserva.");
        }
    }

    private static void cancelarMiReservaSala(Scanner sc, ReservaSalaDAO reservaDAO,ReservaSala reserva, Usuario u) {

        System.out.print("¿Seguro que deseas cancelar la reserva " + reserva.getReservaId() +"? (s/n): ");
        String r = sc.nextLine();

        if (!r.equalsIgnoreCase("s")) {
            System.out.println("Operación cancelada.");
            return;
        }

        boolean ok = reservaDAO.cancelarReservaUsuario(reserva.getReservaId(), u.getId());

        if (ok) {
            System.out.println("Reserva cancelada correctamente.");
        } else {
            System.out.println("No se pudo cancelar (puede que ya esté cancelada).");
        }
    }



    private static void verHistorialPrestamos(PrestamoDAO prestamoDAO) {
        System.out.println("\n--- HISTORIAL DE PRÉSTAMOS ---");
        List<Prestamo> lista = prestamoDAO.listarTodos();

        if (lista.isEmpty()) {
            System.out.println("No hay préstamos registrados.");
            return;
        }

        for (Prestamo p : lista) {
            // Calculamos el estado “al vuelo” SIN tener atributo
            String estado;
            if (p.getFechaDevolucion() == null) {
                estado = "Activo";
            } else {
                estado = "Devuelto";
            }

            System.out.println(
                "ID: " + p.getPrestamoId() +
                " | CopiaID: " + p.getCopiaId() +
                " | PersonaID: " + p.getPersonaId() +
                " | Fecha préstamo: " + p.getFechaPrestamo() +
                " | Vence: " + p.getFechaVencimiento() +
                " | Devuelto: " + p.getFechaDevolucion() +
                " | Estado: " + estado
            );
        }
    }

    private static void verHistorialPenalidades() {
        PenalidadDAO penalidadDAO = new PenalidadDAO();
        List<Penalidad> lista = penalidadDAO.listarHistorial();

        if (lista.isEmpty()) {
            System.out.println("No hay penalidades registradas.");
            return;
        }

        System.out.println("\n--- HISTORIAL DE PENALIDADES ---");
        for (Penalidad p : lista) {
            System.out.println(
                "ID: " + p.getPenalidadId() +
                " | PrestamoID: " + p.getPrestamoId() +
                " | PersonaID: " + p.getPersonaId() +
                " | Tipo: " + p.getTipoPenalidadId() +
                " | Monto: " + p.getMonto() +
                " | Fecha: " + p.getFechaAplicacion() +
                " | Pagada: " + (p.isPagada() ? "Sí" : "No") +
                " | Obs: " + p.getObservacion()
            );
        }
    }


    private static void reservarSala(Scanner sc, ReservaSalaDAO reservaDAO, Usuario u) {

        SalaDAO salaDAO = new SalaDAO();

        System.out.println("\n--- RESERVAR SALA ---");

        System.out.print("Ingrese ID de la sala: ");
        int salaId = Integer.parseInt(sc.nextLine());

        FacilityResource sala = salaDAO.obtenerPorId(salaId);

        if (sala == null) {
            System.out.println("Sala no encontrada.");
            return;
        }

        System.out.println("Sala seleccionada: " + sala.getName() +
                        " | Capacidad: " + sala.getCapacity());

        
        int numPersonas;
        while (true) {
            System.out.print("Ingrese número de personas: ");
            String numStr = sc.nextLine();

            try {
                numPersonas = Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
                continue;
            }

            if (numPersonas <= 0) {
                System.out.println("El número de personas debe ser mayor que 0.");
            } else if (numPersonas > sala.getCapacity()) {
                System.out.println("No puede exceder la capacidad de la sala (" +
                                sala.getCapacity() + ").");
            } else {
                break; // válido
            }
        }

        
        Timestamp inicio;
        Timestamp fin;

        while (true) {
            
            inicio = leerTimestamp(sc,
                    "Ingrese fecha inicio (formato exacto yyyy-MM-dd HH:mm): ");

            
            fin = leerTimestamp(sc,
                    "Ingrese fecha fin (formato exacto yyyy-MM-dd HH:mm): ");

            
            if (!fin.after(inicio)) {
                System.out.println("La fecha de fin debe ser posterior a la de inicio.");
                continue;
            }

            
            Timestamp ahora = new Timestamp(System.currentTimeMillis());
            if (!inicio.after(ahora)) {
                System.out.println("La fecha de inicio debe ser posterior a la fecha actual.");
                continue;
            }

            
            long diffMs = fin.getTime() - inicio.getTime();
            long maxMs = 3L * 60 * 60 * 1000; // 3 horas en milisegundos

            if (diffMs > maxMs) {
                System.out.println("No se puede reservar por más de 3 horas.");
                continue;
            }

            break; 
        }

        
        ReservaSala reserva = new ReservaSala(sala, u.getId(), inicio, fin, numPersonas);

        if (reservaDAO.crearReserva(reserva)) {
            System.out.println("Reserva realizada con éxito.");
        } else {
            System.out.println("ERROR: No se pudo realizar la reserva.");
        }
    }

    private static Timestamp leerTimestamp(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine();

            try {
                return Timestamp.valueOf(input + ":00");
            } catch (IllegalArgumentException e) {
                System.out.println("Formato inválido. Ejemplo válido: 2025-12-05 14:30");
            }
        }
    }

    private static Timestamp leerTimestampDesdeTexto(String input) {
        try {
            // Timestamp.valueOf exige "yyyy-MM-dd HH:mm:ss"
            return Timestamp.valueOf(input + ":00");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }



    private static void eliminarReserva(Scanner sc, ReservaSalaDAO reservaDAO) {

        System.out.println("\n--- ELIMINAR RESERVA ---");
        System.out.print("ID de reserva: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("¿Seguro que desea cancelar esta reserva? (s/n): ");
        if (!sc.nextLine().equalsIgnoreCase("s")) {
            System.out.println("Cancelado.");
            return;
        }

        if (reservaDAO.eliminarReserva(id)) {
            System.out.println("Reserva eliminada.");
        } else {
            System.out.println("No se pudo eliminar.");
        }
    }

    private static void verSolicitudesUsuario(SolicitudDAO solDAO, Usuario u) {
        solDAO.verSolicitudesUsuario(u.getId()).forEach(System.out::println);
    }

    private static void procesarSolicitudes(Scanner sc, SolicitudDAO solDAO) {
        

        List<Solicitud> lista = solDAO.verSolicitudesPendientes();
        if (lista.isEmpty()) {
            System.out.println("No hay solicitudes pendientes.");
            return;
        }

        lista.forEach(System.out::println);

        System.out.print("ID de solicitud a procesar: ");
        int id = Integer.parseInt(sc.nextLine());


        Solicitud solicitud = solDAO.obtenerPorId(id);
        if (solicitud == null) {
            System.out.println("No se encontró la solicitud con ese ID.");
            return;
        }

        if (!solicitud.getEstado().equalsIgnoreCase("Pendiente")) {
            System.out.println("❌ La solicitud ya fue procesada anteriormente (estado actual: "
                    + solicitud.getEstado() + "). No puede modificarse.");
            return;
        }

        System.out.print("Nuevo estado (Aprobado / Rechazado): ");
        String estadoIngresado = sc.nextLine().trim();

        String estado;
        if (estadoIngresado.equalsIgnoreCase("Aprobado")) {
            estado = "aprobado";      
        } else if (estadoIngresado.equalsIgnoreCase("Rechazado")) {
            estado = "rechazado";
        } else {
            System.out.println("❌ Estado inválido. Debe ser 'Aprobado' o 'Rechazado'.");
            return;
        }

        
        System.out.println("Solicitud seleccionada: " + solicitud);

        if (estado.equalsIgnoreCase("Aprobado")) {
            String tipo = solicitud.getTipoMaterial();

            boolean esFisico = tipo.equalsIgnoreCase("Libro")
                            || tipo.equalsIgnoreCase("Revista");

            PrestamoService prestamoService = new PrestamoService();

            if (esFisico) {

                boolean okPrestamo = prestamoService.prestarRecurso(solicitud);

                if (!okPrestamo) {
                    System.out.println("No se pudo generar el préstamo físico. La solicitud seguirá como Pendiente.");
                    return;
                }

            } else {
                boolean okDig = prestamoService.prestarRecursoDigital(solicitud);

                if (!okDig) {
                    System.out.println("No se pudo generar el préstamo digital. La solicitud seguirá como Pendiente.");
                    return;
                }
            }
        }

        if (solDAO.actualizarEstadoSolicitud(id, estado)) {
            System.out.println("Estado de la solicitud actualizado a: " + estado);
        } else {
            System.out.println("Error actualizando estado.");
        }
    }

    private static void registrarDevolucion(Scanner sc) {

        PrestamoDAO prestamoDAO = new PrestamoDAO();
        PrestamoService prestamoService = new PrestamoService();

        System.out.println("=== Registrar devolución de préstamo ===");

        List<Prestamo> activos = prestamoDAO.listarPrestamosActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay préstamos activos.");
            return;
        }

        System.out.println("--- PRÉSTAMOS FÍSICOS ACTIVOS ---");
        for (Prestamo p : activos) {
            System.out.println(
                    "ID préstamo: " + p.getPrestamoId() +
                    " | CopiaID: " + p.getCopiaId() +
                    " | UsuarioID: " + p.getPersonaId() +
                    " | Vence: " + p.getFechaVencimiento()
            );
        }

        System.out.print("Ingrese ID del préstamo: ");
        int prestamoId = Integer.parseInt(sc.nextLine());

        Prestamo prestamo = prestamoDAO.obtenerPorId(prestamoId); 
        if (prestamo == null) {
            System.out.println("No se encontró el préstamo.");
            return;
        }

        if (prestamo.getFechaDevolucion() != null) {
            System.out.println("Este préstamo ya fue devuelto.");
            return;
        }

        System.out.println("Seleccione estado de la devolución:");
        System.out.println("1) Devuelto normal");
        System.out.println("2) Devuelto con daño");
        System.out.println("3) No devuelto");
        System.out.println("4) Devuelto con retraso");
        System.out.print("Opción: ");

        int opcion = Integer.parseInt(sc.nextLine());
        if (opcion < 1 || opcion > 4) {
            System.out.println("Opción no válida.");
            return;
        }

        // monto para daño o pérdida
        BigDecimal montoExtra = BigDecimal.ZERO;
        if (opcion == 2 || opcion == 3) {
            System.out.print("Ingrese monto por daño/pérdida: ");
            montoExtra = new BigDecimal(sc.nextLine());
        }


        boolean ok = prestamoService.devolverRecursoConPenalidad(
                prestamo,
                opcion,
                montoExtra
        );

        if (ok) {
            System.out.println(" Devolución registrada correctamente.");
        } else {
            System.out.println(" Error registrando la devolución.");
        }
    }

    private static void registrarPenalidadPosterior(Scanner sc) {
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        PrestamoService prestamoService = new PrestamoService();
        PenalidadDAO penalidadDAO = new PenalidadDAO();

        System.out.println("=== Registrar penalidad posterior ===");
        System.out.print("Ingrese ID del préstamo: ");
        int prestamoId = Integer.parseInt(sc.nextLine());

        Prestamo prestamo = prestamoDAO.obtenerPorId(prestamoId);
        if (prestamo == null) {
            System.out.println("No se encontró el préstamo.");
            return;
        }

        if (prestamo.getFechaDevolucion() == null) {
            System.out.println("El préstamo aún no ha sido devuelto. Use la opción de devolución.");
            return;
        }

        if (penalidadDAO.existePenalidadPorPrestamo(prestamoId)) {
            System.out.println("Ya existe una penalidad asociada a este préstamo.");
            return;
        }

        System.out.println("Tipo de penalidad:");
        System.out.println("2) Daño");
        System.out.println("3) Pérdida");
        int tipo = Integer.parseInt(sc.nextLine());

        if (tipo != 2 && tipo != 3) {
            System.out.println("Tipo de penalidad no válido. Solo 2 (Daño) o 3 (Pérdida).");
            return;
        }

        System.out.print("Monto de la penalidad: ");
        BigDecimal monto = new BigDecimal(sc.nextLine());

        boolean ok = prestamoService.registrarPenalidadPosterior(
                prestamo,
                tipo,
                monto,
                "Penalidad registrada luego del inventario"
        );

        if (ok) {
            System.out.println(" Penalidad registrada.");
        } else {
            System.out.println(" No se pudo registrar la penalidad.");
        }
    }

    private static void gestionarPenalidades(Scanner sc) {
        PenalidadDAO penalidadDAO = new PenalidadDAO();

        System.out.print("Ingrese ID de la persona: ");
        int personaId = Integer.parseInt(sc.nextLine());

        List<Penalidad> activas = penalidadDAO.listarPenalidadesActivasPorPersona(personaId);

        if (activas.isEmpty()) {
            System.out.println("No tiene penalidades activas.");
            return;
        }

        System.out.println("Penalidades activas:");
        for (Penalidad p : activas) {
            System.out.println("ID: " + p.getPenalidadId() +
                    " | Tipo: " + p.getTipoPenalidadId() +
                    " | Monto: " + p.getMonto());
        }

        System.out.print("Ingrese el ID de la penalidad que se ha pagado: ");
        int penalidadId = Integer.parseInt(sc.nextLine());

        if (penalidadDAO.marcarPenalidadPagada(penalidadId)) {
            System.out.println(" Penalidad marcada como pagada.");
        } else {
            System.out.println(" No se pudo actualizar la penalidad.");
        }
    }



    // ================= REGISTRAR SALA =================

    private static void registrarSala(Scanner sc, SalaDAO salaDAO) {

        System.out.println("\n--- REGISTRAR SALA ---");

        System.out.println("Tipo de recurso:");
        System.out.println("1. Room");
        System.out.println("2. Campus Space");
        System.out.print("Opción: ");
        String tipo = sc.nextLine();

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Ubicación: ");
        String ubicacion = sc.nextLine();

        System.out.print("Capacidad: ");
        int capacidad = Integer.parseInt(sc.nextLine());

        System.out.print("Descripción: ");
        String descripcion = sc.nextLine();

        FacilityResource sala;

        if (tipo.equals("1")) {
            sala = new Room(0, nombre, ubicacion, capacidad, descripcion, "GENERAL");
        } else {
            sala = new CampusSpace(0, nombre, ubicacion, capacidad, descripcion, "GENERAL");
        }

        if (salaDAO.insertarSala(sala)) {
            System.out.println("Sala registrada correctamente.");
        } else {
            System.out.println("ERROR: No se pudo registrar.");
        }
    }



// ================= LISTAR SALAS =================

    private static void listarSalas(SalaDAO salaDAO) {

        System.out.println("\n--- LISTA DE SALAS ---");

        var salas = salaDAO.obtenerTodas();

        if (salas.isEmpty()) {
            System.out.println("No hay salas registradas.");
            return;
        }

        for (var s : salas) {
            System.out.println("ID: " + s.getId() + " | Nombre: " + s.getName() +
                            " | Tipo: " + s.getTipoRecurso() +
                            " | Capacidad: " + s.getCapacity());
        }
    }

    private static void modificarSala(Scanner sc, SalaDAO salaDAO) {

        System.out.println("\n--- MODIFICAR SALA ---");
        System.out.print("Ingrese ID de la sala: ");
        int salaId = Integer.parseInt(sc.nextLine());

        FacilityResource original = salaDAO.obtenerPorId(salaId);

        if (original == null) {
            System.out.println("Sala no encontrada.");
            return;
        }

        System.out.println("Presione ENTER para mantener el valor actual.");

        System.out.print("Nuevo nombre (" + original.getName() + "): ");
        String nombre = sc.nextLine();
        if (nombre.isEmpty()) nombre = original.getName();

        System.out.print("Nueva ubicación (" + original.getLocation() + "): ");
        String ubicacion = sc.nextLine();
        if (ubicacion.isEmpty()) ubicacion = original.getLocation();

        System.out.print("Nueva capacidad (" + original.getCapacity() + "): ");
        String capInput = sc.nextLine();
        int capacidad = capInput.isEmpty() ? original.getCapacity() : Integer.parseInt(capInput);

        System.out.print("Nueva descripción (" + original.getDescription() + "): ");
        String descripcion = sc.nextLine();
        if (descripcion.isEmpty()) descripcion = original.getDescription();

        FacilityResource nueva;

        if (original.getTipoRecurso().equalsIgnoreCase("ROOM")) {
            nueva = new Room(salaId, nombre, ubicacion, capacidad, descripcion, "GENERAL");
        } else {
            nueva = new CampusSpace(salaId, nombre, ubicacion, capacidad, descripcion, "GENERAL");
        }

        if (salaDAO.actualizarSala(nueva, original)) {
            System.out.println("Sala actualizada correctamente.");
        } else {
            System.out.println("Error al actualizar sala.");
        }
    }

    private static void eliminarSala(Scanner sc, SalaDAO salaDAO) {

        ReservaSalaDAO reservaDAO = new ReservaSalaDAO();

        System.out.println("\n--- ELIMINAR SALA ---");
        System.out.print("Ingrese ID de la sala: ");
        int salaId = Integer.parseInt(sc.nextLine());

        FacilityResource sala = salaDAO.obtenerPorId(salaId);

        if (sala == null) {
            System.out.println("No existe sala con ese ID.");
            return;
        }

        
        if (reservaDAO.existenReservasPorSala(salaId)) {
            System.out.println("No se puede eliminar la sala '" + sala.getName() + "' porque tiene reservas asociadas.");
            System.out.println("Primero elimine o cancele esas reservas (opción: Eliminar reserva de sala).");
            return;
        }

        System.out.println("¿Seguro que desea eliminar la sala '" + sala.getName() + "'? (s/n)");
        if (!sc.nextLine().equalsIgnoreCase("s")) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (salaDAO.eliminarSala(salaId)) {
            System.out.println("Sala eliminada.");
        } else {
            System.out.println("No se pudo eliminar.");
        }
    }
}
