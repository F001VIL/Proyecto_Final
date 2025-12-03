package servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;

import modelo.Prestamo;
import modelo.Solicitud;
import modelo.Usuario;
import dao.UsuarioDAO;
import dao.PenalidadDAO;
import dao.PrestamoDAO;
import dao.PrestamoDigitalDAO;
import dao.RecursoCopiaDAO;
import dao.RecursoDAO;

public class PrestamoService {
    
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final PrestamoDigitalDAO prestamoDigitalDAO = new PrestamoDigitalDAO();
    private final RecursoCopiaDAO recursoCopiaDAO = new RecursoCopiaDAO();
    private final RecursoDAO recursoDAO = new RecursoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PenalidadDAO penalidadDAO = new PenalidadDAO();

    private static final int MAX_ITEMS_SIMULTANEOS = 5;
    private static final int DIAS_MAX_PRESTAMO = 14;
    private static final int MAX_PRESTAMOS_DIGITALES = 3;
    private static final BigDecimal MONTO_RETRASO_POR_DIA = new BigDecimal("1.00");


    // ==========================================================
    //               APROBAR SOLICITUD → PRESTAR RECURSO
    // ==========================================================
    public boolean prestarRecurso(Solicitud solicitud) {
        
        int usuarioId = solicitud.getUsuarioId();
        int recursoId = solicitud.getMaterialId(); 

        Usuario u = usuarioDAO.obtenerPorId(usuarioId);
        if (u == null) {
            System.out.println("No se encontró el usuario de la solicitud.");
            return false;
        }
        int personaId = u.getPersonaId();

        // 1. Revisar penalidades
        if (penalidadDAO.tienePenalidadActiva(personaId)) {
            System.out.println("❌ El usuario tiene penalidades activas.");
            return false;
        }

        // 2. Revisar límite de préstamos
        List<Prestamo> activos = recursoDAO.listarPrestamosActivos(personaId);
        if (activos.size() >= MAX_ITEMS_SIMULTANEOS) {
            System.out.println("El usuario alcanzó el máximo (" + MAX_ITEMS_SIMULTANEOS + ").");
            return false;
        }

        // 3. Buscar una copia física disponible
        List<Integer> copias = recursoCopiaDAO.obtenerCopiasDisponibles(recursoId);
        if (copias.isEmpty()) {
            System.out.println("❌ No hay copias disponibles para este recurso.");
            return false;
        }

        int copiaId = copias.get(0);
        System.out.println("Copia asignada al préstamo: ID copia = " + copiaId);


        // 5. Fechas de préstamo
        LocalDateTime ahora = LocalDateTime.now();
        Timestamp fechaPrestamo = Timestamp.valueOf(ahora);
        Timestamp fechaVencimiento = Timestamp.valueOf(ahora.plusDays(DIAS_MAX_PRESTAMO));

        // 6. Registrar préstamo
        boolean okPrestamo = prestamoDAO.registrarPrestamo(
                copiaId,
                personaId,
                fechaPrestamo,
                fechaVencimiento
        );

        if (!okPrestamo) {
            System.out.println("Error registrando préstamo. Revirtiendo stock...");
            recursoDAO.aumentarStock(recursoId);
            return false;
        }

        // 7. Cambiar estado de copia
        recursoCopiaDAO.actualizarEstadoCopia(copiaId, 2);

        return true;
    }

    // ==========================================================
    //               APROBAR SOLICITUD → PRESTAR RECURSO DIGITAL
    // ==========================================================

    public boolean prestarRecursoDigital(Solicitud solicitud) {

        prestamoDigitalDAO.cerrarPrestamosVencidos();

        int personaId = solicitud.getUsuarioId();
        int recursoId = solicitud.getMaterialId(); 

      
        // 1) Validar usuario
        Usuario usuario = usuarioDAO.obtenerPorId(personaId);
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
            return false;
        }

        // 2) Validar que no tenga penalidad activa
        if (penalidadDAO.tienePenalidadActiva(personaId)) {
            System.out.println("El usuario tiene penalidades activas. No puede realizar préstamos.");
            return false;
        }

        // 3) Validar límite de préstamos digitales activos
        int activos = prestamoDigitalDAO.contarPrestamosActivosPorPersona(personaId);
        if (activos >= MAX_PRESTAMOS_DIGITALES) {
            System.out.println("Ya tiene " + activos + " préstamos digitales activos. "
                    + "Debe devolver alguno para pedir otro.");
            return false;
        }

        // 4) Fechas de préstamo (puedes ajustar los días)
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime venc = ahora.plusDays(7); // por ejemplo, 7 días para recursos digitales

        Timestamp fPrestamo = Timestamp.valueOf(ahora);
        Timestamp fVenc = Timestamp.valueOf(venc);

        // 5) Registrar préstamo digital
        boolean ok = prestamoDigitalDAO.registrarPrestamoDigital(recursoId, personaId, fPrestamo, fVenc);

        if (ok) {
            System.out.println("✔ Préstamo DIGITAL registrado correctamente.");
        }

        return ok;
    }


    // ==========================================================
    //                  DEVOLVER SIN PENALIDADES
    // ==========================================================
    public boolean devolverRecurso(Prestamo prestamo) {

        int prestamoId = prestamo.getPrestamoId();
        int copiaId = prestamo.getCopiaId();

        Timestamp fechaDev = Timestamp.valueOf(LocalDateTime.now());
        String observacion = "Devuelto normal";

        boolean ok = prestamoDAO.registrarDevolucion(prestamoId, fechaDev, observacion);
        if (!ok) return false;

        recursoCopiaDAO.actualizarEstadoCopia(copiaId, 1);

        // Recuperar recursoId desde copia
        int recursoId = recursoCopiaDAO.obtenerRecursoIdDesdeCopia(copiaId);
        recursoDAO.aumentarStock(recursoId);


        recursoCopiaDAO.actualizarEstadoCopia(copiaId, 1); 
        return true;
    }


    // ==========================================================
    //        DEVOLVER CON PENALIDAD (retraso, daño, pérdida)
    // ==========================================================
    public boolean devolverRecursoConPenalidad(
            Prestamo prestamo,
            int opcionDevolucion,
            BigDecimal montoDanioOPerdida
    ) {

        int prestamoId = prestamo.getPrestamoId();
        int personaId  = prestamo.getPersonaId();
        int copiaId    = prestamo.getCopiaId();

        if (prestamo.getFechaDevolucion() != null) {
            System.out.println("⚠ Ya está devuelto.");
            return false;
        }

        String observacion;
        switch (opcionDevolucion) {
            case 1 -> observacion = "Devuelto normal";
            case 2 -> observacion = "Devolución con daño";
            case 3 -> observacion = "No devuelto (pérdida)";
            case 4 -> observacion = "Devuelto con retraso";
            default -> observacion = "Devolución registrada";
        }

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        prestamoDAO.registrarDevolucion(prestamoId, ahora, observacion);

        // Calcular retraso
        long diasRetraso =
                (ahora.getTime() - prestamo.getFechaVencimiento().getTime())
                / (1000L * 60 * 60 * 24);

        if (diasRetraso > 0) {
            penalidadDAO.registrarPenalidadPorRetraso(
                    prestamoId,
                    personaId,
                    diasRetraso,
                    MONTO_RETRASO_POR_DIA
            );
        }

        // Obtener RecursoID
        int recursoId = recursoCopiaDAO.obtenerRecursoIdDesdeCopia(copiaId);

        // -----------------------
        // OPCIONES DE DEVOLUCIÓN
        // -----------------------
        switch (opcionDevolucion) {

            case 1: // Normal
                recursoCopiaDAO.actualizarEstadoCopia(copiaId, 1);
                recursoDAO.aumentarStock(recursoId);
                break;

            case 2: // Daño
                penalidadDAO.registrarPenalidadPorDanio(
                        prestamoId, personaId, montoDanioOPerdida,
                        "Daño reportado"
                );
                recursoCopiaDAO.actualizarEstadoCopia(copiaId, 1);
                recursoDAO.aumentarStock(recursoId);
                break;

            case 3: // Pérdida
                penalidadDAO.registrarPenalidadPorPerdida(
                        prestamoId, personaId, montoDanioOPerdida,
                        "Pérdida del material"
                );
                recursoCopiaDAO.actualizarEstadoCopia(copiaId, 3); 
                break;

            case 4: // Solo retraso
                recursoCopiaDAO.actualizarEstadoCopia(copiaId, 1);
                recursoDAO.aumentarStock(recursoId);
                break;

            default:
                System.out.println("Opción de devolución inválida.");
                return false;
        }

        return true;
    }


    // ==========================================================
    //         REGISTRAR PENALIDAD POSTERIOR A DEVOLUCIÓN
    // ==========================================================
    public boolean registrarPenalidadPosterior(
            Prestamo prestamo,
            int tipoPenalidad,
            BigDecimal monto,
            String observacion
    ) {
        int prestamoId = prestamo.getPrestamoId();
        int personaId  = prestamo.getPersonaId();

        // Solo se permite penalidad posterior si YA fue devuelto
        if (prestamo.getFechaDevolucion() == null) {
            System.out.println("El recurso no ha sido devuelto.");
            return false;
        }

        boolean okPenalidad;
        String textoObsPrestamo;

        switch (tipoPenalidad) {
            case 2: // Daño
                okPenalidad = penalidadDAO.registrarPenalidadPorDanio(
                        prestamoId, personaId, monto, observacion
                );
                // Lo que quieres que quede en la columna Observaciones de Prestamo
                textoObsPrestamo = "Daño detectado en inventario";
                break;

            case 3: // Pérdida
                okPenalidad = penalidadDAO.registrarPenalidadPorPerdida(
                        prestamoId, personaId, monto, observacion
                );
                textoObsPrestamo = "Pérdida detectada en inventario";
                break;

            default:
                System.out.println("Tipo de penalidad inválido.");
                return false;
        }

        if (!okPenalidad) {
            return false;
        }

        
        boolean okObs = prestamoDAO.actualizarObservacion(prestamoId, textoObsPrestamo);

        if (!okObs) {
            
            System.out.println("Penalidad registrada, pero no se pudo actualizar la observación del préstamo.");
        }

        return okObs;
    }
}
