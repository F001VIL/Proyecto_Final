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

    
    public boolean prestarRecurso(Solicitud solicitud) {
        
        int usuarioId = solicitud.getUsuarioId();
        int copiaId   = solicitud.getMaterialId();

        Usuario u = usuarioDAO.obtenerPorId(usuarioId);
        if (u == null) {
            System.out.println("No se encontró el usuario de la solicitud.");
            return false;
        }
        int personaId = u.getPersonaId();

        if (penalidadDAO.tienePenalidadActiva(personaId)) {
            System.out.println("❌ El usuario tiene una penalidad activa y no puede realizar préstamos.");
            return false;
        }

        // Verificar cantidad de préstamos activos
        List<Prestamo> activos = recursoDAO.listarPrestamosActivos(personaId);
        if (activos.size() >= MAX_ITEMS_SIMULTANEOS) {
            System.out.println("El usuario ya alcanzó el máximo de préstamos simultáneos ("
                    + MAX_ITEMS_SIMULTANEOS + ").");
            return false;
        }

        
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime vence = ahora.plusDays(DIAS_MAX_PRESTAMO);

        Timestamp fechaPrestamo    = Timestamp.valueOf(ahora);
        Timestamp fechaVencimiento = Timestamp.valueOf(vence);

        
        boolean okPrestamo = prestamoDAO.registrarPrestamo(
                copiaId,
                personaId,
                fechaPrestamo,
                fechaVencimiento
        );

        // Actualizar estado de la copia a "prestada"
        if (okPrestamo) {
            boolean okEstado = recursoCopiaDAO.actualizarEstadoCopia(copiaId, 2);
            if (!okEstado) {
                System.out.println("Se creó el préstamo, pero no se pudo actualizar el estado de la copia.");
            }
            return okEstado;
        }

        return false;
    }

    
    public boolean devolverRecurso(Prestamo prestamo) {
        int prestamoId = prestamo.getPrestamoId();
        int copiaId = prestamo.getCopiaId();

        LocalDateTime ahora = LocalDateTime.now();
        Timestamp fechaDevolucion = Timestamp.valueOf(ahora);

        
        boolean okPrestamo = prestamoDAO.registrarDevolucion(prestamoId, fechaDevolucion);

        
        if (okPrestamo) {
            boolean okEstado = recursoCopiaDAO.actualizarEstadoCopia(copiaId, 1);
            if (!okEstado) {
                System.out.println("Se marcó el préstamo como devuelto, pero no se pudo actualizar la copia.");
            }
            return okEstado;
        }

        return false;
    }


    public boolean devolverRecursoConPenalidad(Prestamo prestamo,int opcionDevolucion,BigDecimal montoDanioOPerdida) {

        int prestamoId = prestamo.getPrestamoId();        
        int personaId  = prestamo.getPersonaId();
        int copiaId    = prestamo.getCopiaId();

        if (prestamo.getFechaDevolucion() != null) {
            System.out.println("⚠ Este préstamo ya fue devuelto el: " + prestamo.getFechaDevolucion());
            System.out.println("   No se puede registrar una devolución nuevamente.");
            return false;
        }

        //Registrar devolución en la BD (fecha actual)
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        boolean okDevol = prestamoDAO.registrarDevolucion(prestamoId, ahora);
        if (!okDevol) {
            System.out.println("Error registrando la devolución.");
            return false;
        }

        //Calcular retraso (si ahora > fechaFin)
        Timestamp fechaFin = prestamo.getFechaVencimiento();
        long diasRetraso = (ahora.getTime() - fechaFin.getTime()) / (1000L * 60 * 60 * 24);

        //Penalidad por retraso (físico o digital)
        if (diasRetraso > 0) {
            penalidadDAO.registrarPenalidadPorRetraso(
                    prestamoId,
                    personaId,
                    diasRetraso,
                    MONTO_RETRASO_POR_DIA
            );
        }

        
        if (opcionDevolucion == 2) {
            // daño
            penalidadDAO.registrarPenalidadPorDanio(
                    prestamoId,
                    personaId,
                    montoDanioOPerdida,
                    "Daño reportado por bibliotecario"
            );
        } else if (opcionDevolucion == 3) {
            // pérdida
            penalidadDAO.registrarPenalidadPorPerdida(
                    prestamoId,
                    personaId,
                    montoDanioOPerdida,
                    "Pérdida del recurso"
            );
        }

        recursoCopiaDAO.actualizarEstadoCopia(
                copiaId,
                1 
        );

        return true;
    }


    public boolean registrarPenalidadPosterior(Prestamo prestamo,int tipoPenalidad,BigDecimal monto,String observacion) {

        int prestamoId = prestamo.getPrestamoId();
        int personaId  = prestamo.getPersonaId();

        if (prestamo.getFechaDevolucion() == null) {
            System.out.println("⚠ El préstamo aún no se ha devuelto. Use la opción de devolución.");
            return false;
        }

        if (tipoPenalidad == 2) {
            return penalidadDAO.registrarPenalidadPorDanio(
                    prestamoId, personaId, monto, observacion
            );
        } else if (tipoPenalidad == 3) {
            return penalidadDAO.registrarPenalidadPorPerdida(
                    prestamoId, personaId, monto, observacion
            );
        } else {
            System.out.println("Tipo de penalidad no válido.");
            return false;
        }
    }
}
