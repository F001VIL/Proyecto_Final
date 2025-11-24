package servicio;


import java.time.LocalDateTime;
import java.util.List;
import java.sql.Timestamp;

import modelo.Prestamo;
import modelo.Solicitud;
import modelo.Usuario;
import dao.UsuarioDAO;
import dao.PrestamoDAO;
import dao.RecursoCopiaDAO;
import dao.RecursoDAO;

public class PrestamoService {
    
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final RecursoCopiaDAO recursoCopiaDAO = new RecursoCopiaDAO();
    private final RecursoDAO recursoDAO = new RecursoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private static final int MAX_ITEMS_SIMULTANEOS = 5;
    private static final int DIAS_MAX_PRESTAMO = 14;

    // Prestar un recurso a un usuario
    public boolean prestarRecurso(Solicitud solicitud) {
        // 1) Obtener el usuario y su personaId
        int usuarioId = solicitud.getUsuarioId();
        int copiaId   = solicitud.getMaterialId(); // aquí estás guardando el CopiaID

        Usuario u = usuarioDAO.obtenerPorId(usuarioId);
        if (u == null) {
            System.out.println("No se encontró el usuario de la solicitud.");
            return false;
        }
        int personaId = u.getPersonaId();

        // 2) Verificar máximo de préstamos activos para esa persona
        List<Prestamo> activos = recursoDAO.listarPrestamosActivos(personaId);
        if (activos.size() >= MAX_ITEMS_SIMULTANEOS) {
            System.out.println("El usuario ya alcanzó el máximo de préstamos simultáneos ("
                    + MAX_ITEMS_SIMULTANEOS + ").");
            return false;
        }

        // 3) Calcular fechas de préstamo y vencimiento
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime vence = ahora.plusDays(DIAS_MAX_PRESTAMO);

        Timestamp fechaPrestamo    = Timestamp.valueOf(ahora);
        Timestamp fechaVencimiento = Timestamp.valueOf(vence);

        // 4) Insertar en tabla Prestamo
        boolean okPrestamo = prestamoDAO.registrarPrestamo(
                copiaId,
                personaId,
                fechaPrestamo,
                fechaVencimiento
        );

        // 5) Si se creó el préstamo, marcar la copia como PRESTADA (EstadoID = 2)
        if (okPrestamo) {
            boolean okEstado = recursoCopiaDAO.actualizarEstadoCopia(copiaId, 2);
            if (!okEstado) {
                System.out.println("Se creó el préstamo, pero no se pudo actualizar el estado de la copia.");
            }
            return okEstado;
        }

        return false;
    }

    // Devolver un recurso prestado
    public boolean devolverRecurso(Prestamo prestamo) {
        int prestamoId = prestamo.getPrestamoId();
        int copiaId = prestamo.getCopiaId();

        LocalDateTime ahora = LocalDateTime.now();
        Timestamp fechaDevolucion = Timestamp.valueOf(ahora);

        // 1) Marcar préstamo como devuelto
        boolean okPrestamo = prestamoDAO.registrarDevolucion(prestamoId, fechaDevolucion);

        // 2) Volver a poner la copia como Disponible (EstadoID = 1)
        if (okPrestamo) {
            boolean okEstado = recursoCopiaDAO.actualizarEstadoCopia(copiaId, 1);
            if (!okEstado) {
                System.out.println("Se marcó el préstamo como devuelto, pero no se pudo actualizar la copia.");
            }
            return okEstado;
        }

        return false;
    }
}
