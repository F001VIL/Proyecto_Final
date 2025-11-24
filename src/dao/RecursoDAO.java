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
                SELECT DISTINCT r.RecursoID, r.Titulo, r.Descripcion, r.TipoRecursoID
                FROM Recurso r
                JOIN TipoRecurso t ON t.TipoRecursoID = r.TipoRecursoID
                LEFT JOIN RecursoCopia rc
                    ON rc.RecursoID = r.RecursoID AND rc.EstadoID = 1
                WHERE
                    -- FÍSICOS: necesitan copia disponible
                    (t.NombreTipo IN ('Libro','Revista','Tablet','PC','DVD') AND rc.CopiaID IS NOT NULL)
                    -- DIGITALES: siempre disponibles si existen
                    OR
                    (t.NombreTipo IN ('Tesis','Audiolibro','Multimedia'))
                """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(
                        new Recurso(
                                rs.getInt("RecursoID"),
                                rs.getString("Titulo"),
                                rs.getString("Descripcion"),
                                rs.getInt("TipoRecursoID")
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

        // tipos que trataremos como DIGITALES
        boolean esDigital = tipo.equalsIgnoreCase("Tesis")
                        || tipo.equalsIgnoreCase("Audiolibro")
                        || tipo.equalsIgnoreCase("Multimedia");

        String sql;

        if (esDigital) {
            // Digitales: basta que el recurso exista
            sql = """
                SELECT r.RecursoID, r.Titulo, r.Descripcion, r.TipoRecursoID
                FROM Recurso r
                JOIN TipoRecurso t ON t.TipoRecursoID = r.TipoRecursoID
                WHERE t.NombreTipo = ?
                """;
        } else {
            // Físicos: deben tener al menos una copia disponible (EstadoID = 1)
            sql = """
                SELECT DISTINCT r.RecursoID, r.Titulo, r.Descripcion, r.TipoRecursoID
                FROM Recurso r
                JOIN TipoRecurso t ON t.TipoRecursoID = r.TipoRecursoID
                JOIN RecursoCopia rc ON rc.RecursoID = r.RecursoID
                WHERE rc.EstadoID = 1 AND t.NombreTipo = ?
                """;
        }

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(
                            new Recurso(
                                    rs.getInt("RecursoID"),
                                    rs.getString("Titulo"),
                                    rs.getString("Descripcion"),
                                    rs.getInt("TipoRecursoID")
                            )
                    );
                }
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

    public int insertarRecurso(String titulo, String descripcion, int tipoRecursoId) {
        String sql = """
                INSERT INTO Recurso (Titulo, Descripcion, TipoRecursoID)
                VALUES (?, ?, ?)
                """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, titulo);
            ps.setString(2, descripcion);
            ps.setInt(3, tipoRecursoId);

            int filas = ps.executeUpdate();
            if (filas == 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // RecursoID generado
                }
            }

        } catch (Exception e) {
            System.out.println("Error en insertarRecurso(): " + e.getMessage());
        }
        return -1;
    }

    public boolean insertarAccesoDigital(int recursoId, String url, String licencia, int usuariosConc) {
        String sql = """
                INSERT INTO AccesoDigital (RecursoID, URLAcceso, Licencia, UsuariosConcurrentes)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ps.setString(2, url);
            ps.setString(3, licencia);
            ps.setInt(4, usuariosConc);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error en insertarAccesoDigital(): " + e.getMessage());
            return false;
        }
    }
}
