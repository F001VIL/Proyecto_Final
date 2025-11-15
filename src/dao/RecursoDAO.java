package dao;

import modelo.Prestamo;
import modelo.Recurso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    // ---------------------------------------------------------
    // LISTAR RECURSOS DISPONIBLES (EstadoID = 1)
    // ---------------------------------------------------------
    public List<Recurso> listarDisponibles() {
        List<Recurso> lista = new ArrayList<>();

        String sql = """
                SELECT r.RecursoID, r.Titulo, t.NombreTipo, rc.CopiaID
                FROM Recurso r
                JOIN TipoRecurso t ON t.TipoRecursoID = r.TipoRecursoID
                JOIN RecursoCopia rc ON rc.RecursoID = r.RecursoID
                WHERE rc.EstadoID = 1
                """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(
                        new Recurso(
                                rs.getInt("RecursoID"),
                                rs.getString("Titulo"),
                                rs.getString("NombreTipo"),
                                rs.getInt("CopiaID")
                        )
                );
            }

        } catch (Exception e) {
            System.out.println("Error en listarDisponibles(): " + e.getMessage());
        }

        return lista;
    }

    // ---------------------------------------------------------
    // LISTAR RECURSOS DISPONIBLES POR TIPO
    // ---------------------------------------------------------
    public List<Recurso> listarDisponiblesPorTipo(String tipo) {
        List<Recurso> lista = new ArrayList<>();

        String sql = """
                SELECT r.RecursoID, r.Titulo, t.NombreTipo, rc.CopiaID
                FROM Recurso r
                JOIN TipoRecurso t ON t.TipoRecursoID = r.TipoRecursoID
                JOIN RecursoCopia rc ON rc.RecursoID = r.RecursoID
                WHERE rc.EstadoID = 1 AND t.NombreTipo = ?
                """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(
                        new Recurso(
                                rs.getInt("RecursoID"),
                                rs.getString("Titulo"),
                                rs.getString("NombreTipo"),
                                rs.getInt("CopiaID")
                        )
                );
            }

        } catch (Exception e) {
            System.out.println("Error en listarDisponiblesPorTipo(): " + e.getMessage());
        }

        return lista;
    }

    // ---------------------------------------------------------
    // LISTAR PRÉSTAMOS ACTIVOS DE UNA PERSONA
    // ---------------------------------------------------------
    public List<Prestamo> listarPrestamosActivos(int personaId) {
        List<Prestamo> lista = new ArrayList<>();

        String sql = """
                SELECT p.PrestamoID, p.CopiaID, r.Titulo,
                       p.FechaPrestamo, p.FechaVencimiento
                FROM Prestamo p
                JOIN RecursoCopia rc ON rc.CopiaID = p.CopiaID
                JOIN Recurso r ON r.RecursoID = rc.RecursoID
                WHERE p.PersonaID = ? AND p.FechaDevolucion IS NULL
                """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personaId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(
                        new Prestamo(
                                rs.getInt("PrestamoID"),
                                rs.getInt("CopiaID"),
                                rs.getString("Titulo"),
                                rs.getTimestamp("FechaPrestamo"),
                                rs.getTimestamp("FechaVencimiento")
                        )
                );
            }

        } catch (Exception e) {
            System.out.println("Error en listarPrestamosActivos(): " + e.getMessage());
        }

        return lista;
    }
}
