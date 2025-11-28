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
import dao.RecursoCopiaDAO;
import dao.RecursoDAO;

public class PrestamoService {
    
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final RecursoCopiaDAO recursoCopiaDAO = new RecursoCopiaDAO();
    private final RecursoDAO recursoDAO = new RecursoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PenalidadDAO penalidadDAO = new PenalidadDAO();

    private static final int MAX_ITEMS_SIMULTANEOS = 5;
    private static final int DIAS_MAX_PRESTAMO = 14;
    private static final BigDecimal MONTO_RETRASO_POR_DIA = new BigDecimal("1.00");


    // ==========================================================
    //               APROBAR SOLICITUD → PRESTAR RECURSO
    // ==========================================================
    public boolean prestarRecurso(Solicitud solicitud) {
        
        int usuarioId = solicitud.getUsuarioId();
        int copiaId   = solicitud.getMaterialId();

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

        // 3. Obtener el ID del recurso para actualizar stock
        int recursoId = recursoCopiaDAO.obtenerRecursoIdDesdeCopia(copiaId);
        if (recursoId == -1) {
            System.out.println("No se pudo obtener el recurso asociado.");
            return false;
        }

        // 4. Disminuir stock antes del préstamo
        if (!recursoDAO.disminuirStock(recursoId)) {
            System.out.println("❌ No hay stock disponible.");
            return false;
        }

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
    //                  DEVOLVER SIN PENALIDADES
    // ==========================================================
    public boolean devolverRecurso(Prestamo prestamo) {

        int prestamoId = prestamo.getPrestamoId();
        int copiaId = prestamo.getCopiaId();

        Timestamp fechaDev = Timestamp.valueOf(LocalDateTime.now());

        boolean ok = prestamoDAO.registrarDevolucion(prestamoId, fechaDev);
        if (!ok) return false;

        recursoCopiaDAO.actualizarEstadoCopia(copiaId, 1);

        // Recuperar recursoId desde copia
        int recursoId = recursoCopiaDAO.obtenerRecursoIdDesdeCopia(copiaId);
        recursoDAO.aumentarStock(recursoId);

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

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        prestamoDAO.registrarDevolucion(prestamoId, ahora);

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
                recursoCopiaDAO.actualizarEstadoCopia(copiaId, 3); // perdido
                // ❌ NO aumentar stock
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
        int personaId = prestamo.getPersonaId();

        if (prestamo.getFechaDevolucion() == null) {
            System.out.println("El recurso no ha sido devuelto.");
            return false;
        }

        return switch (tipoPenalidad) {
            case 2 -> penalidadDAO.registrarPenalidadPorDanio(
                    prestamoId, personaId, monto, observacion
            );
            case 3 -> penalidadDAO.registrarPenalidadPorPerdida(
                    prestamoId, personaId, monto, observacion
            );
            default -> {
                System.out.println("Tipo de penalidad inválido.");
                yield false;
            }
        };
    }
}
