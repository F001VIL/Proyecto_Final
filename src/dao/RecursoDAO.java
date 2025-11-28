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
                    (t.NombreTipo IN ('Libro','Revista','Tablet','PC','DVD') 
                        AND rc.CopiaID IS NOT NULL)
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
    // LISTAR RECURSOS DISPONIBLES POR TIPO (Libro, Tesis, etc.)
    // ---------------------------------------------------------
    public List<Recurso> listarDisponiblesPorTipo(String tipo) {
        List<Recurso> lista = new ArrayList<>();

        boolean esDigital = tipo.equalsIgnoreCase("Tesis")
                        || tipo.equalsIgnoreCase("Audiolibro")
                        || tipo.equalsIgnoreCase("Multimedia");

        String sql;

        if (esDigital) {
            sql = """
                SELECT r.RecursoID, r.Titulo, r.Descripcion, r.TipoRecursoID
                FROM Recurso r
                JOIN TipoRecurso t ON t.TipoRecursoID = r.TipoRecursoID
                WHERE t.NombreTipo = ?
                """;
        } else {
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

    // ---------------------------------------------------------
    // INSERTAR NUEVO RECURSO
    // ---------------------------------------------------------
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
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            System.out.println("Error en insertarRecurso(): " + e.getMessage());
        }
        return -1;
    }

    // ---------------------------------------------------------
    // INSERTAR DATOS DIGITALES (PDF, MP3, URL, etc.)
    // ---------------------------------------------------------
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

    // ---------------------------------------------------------
    // NUEVO: VERIFICAR SI EL RECURSO EXISTE
    // ---------------------------------------------------------
    public boolean existeRecurso(int recursoId) {
        String sql = "SELECT COUNT(*) FROM Recurso WHERE RecursoID = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (Exception e) {
            System.out.println("Error en existeRecurso(): " + e.getMessage());
        }

        return false;
    }

    // ---------------------------------------------------------
    // NUEVO: ELIMINAR RECURSO
    // ---------------------------------------------------------
    public boolean eliminarRecurso(int recursoId) {

    String sqlRecurso = "DELETE FROM Recurso WHERE RecursoID = ?";

    try (Connection con = ConexionBD.getConnection()) {

        con.setAutoCommit(false); // TRANSACTION

        // 1. Eliminar copias
        try (PreparedStatement ps1 = con.prepareStatement(
                "DELETE FROM RecursoCopia WHERE RecursoID = ?")) {
            ps1.setInt(1, recursoId);
            ps1.executeUpdate();
        }

        // 2. Eliminar accesos digitales
        try (PreparedStatement ps2 = con.prepareStatement(
                "DELETE FROM AccesoDigital WHERE RecursoID = ?")) {
            ps2.setInt(1, recursoId);
            ps2.executeUpdate();
        }

        // 3. Eliminar recurso
        try (PreparedStatement ps3 = con.prepareStatement(sqlRecurso)) {
            ps3.setInt(1, recursoId);

            int filas = ps3.executeUpdate();

            if (filas == 0) {
                con.rollback();
                return false;
            }
        }

        con.commit();
        return true;

    } catch (Exception e) {
        System.out.println("Error eliminando recurso: " + e.getMessage());
        return false;
    }
}


    // ---------------------------------------------------------
    // NUEVO: OBTENER STOCK
    // ---------------------------------------------------------
    public int obtenerStock(int recursoId) {
        String sql = "SELECT Stock FROM Recurso WHERE RecursoID = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("Stock");

        } catch (Exception e) {
            System.out.println("Error en obtenerStock(): " + e.getMessage());
        }

        return -1;
    }

    // ---------------------------------------------------------
    // NUEVO: DISMINUIR STOCK AL PRESTAR
    // ---------------------------------------------------------
    public boolean disminuirStock(int recursoId) {
        String sql = "UPDATE Recurso SET Stock = Stock - 1 WHERE RecursoID = ? AND Stock > 0";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error en disminuirStock(): " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------
    // NUEVO: AUMENTAR STOCK AL DEVOLVER
    // ---------------------------------------------------------
    public boolean aumentarStock(int recursoId) {
        String sql = "UPDATE Recurso SET Stock = Stock + 1 WHERE RecursoID = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error en aumentarStock(): " + e.getMessage());
            return false;
        }
    }
}
