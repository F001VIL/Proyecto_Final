package dao;

import modelo.AccesoDigital;
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
                    (t.NombreTipo IN ('Tesis','Libro virtual','Multimedia'))
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
                        || tipo.equalsIgnoreCase("Libro virtual")
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
    public int insertarRecurso(String titulo, String descripcion, int tipoRecursoId, String isbn,String edicion,String editorial,Integer anioPublicacion,String idioma) {
        String sql = """
                INSERT INTO Recurso
                (Titulo, Descripcion, TipoRecursoID, ISBN, Edicion, Editorial, AnoPublicacion, Idioma)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, titulo);
            ps.setString(2, descripcion);
            ps.setInt(3, tipoRecursoId);

            
            if (isbn != null && !isbn.isBlank()) {
                ps.setString(4, isbn);
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (edicion != null && !edicion.isBlank()) {
                ps.setString(5, edicion);
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            
            if (editorial != null && !editorial.isBlank()) {
                ps.setString(6, editorial);
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            
            if (anioPublicacion != null) {
                ps.setInt(7, anioPublicacion);
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            
            if (idioma != null && !idioma.isBlank()) {
                ps.setString(8, idioma);
            } else {
                ps.setNull(8, Types.VARCHAR);
            }

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

        String sqlPrestamosActivosFisicos = """
            SELECT COUNT(*) 
            FROM Prestamo p
            JOIN RecursoCopia rc ON p.CopiaID = rc.CopiaID
            WHERE rc.RecursoID = ? AND p.FechaDevolucion IS NULL
            """;

        String sqlPrestamosDigitalesActivos = """
            SELECT COUNT(*) 
            FROM PrestamoDigital
            WHERE RecursoID = ? AND FechaDevolucion IS NULL
            """;

        String sqlDeleteTesisDetalle   = "DELETE FROM TesisDetalle   WHERE RecursoID = ?";
        String sqlDeleteAccesoDigital  = "DELETE FROM AccesoDigital  WHERE RecursoID = ?";
        String sqlDeleteSolicitudes    = "DELETE FROM Solicitud      WHERE MaterialID = ?";
        String sqlDeleteCopias         = "DELETE FROM RecursoCopia   WHERE RecursoID = ?";
        String sqlDeleteRecurso        = "DELETE FROM Recurso        WHERE RecursoID = ?";

        try (Connection con = ConexionBD.getConnection()) {
            con.setAutoCommit(false);

            
            try (PreparedStatement ps = con.prepareStatement(sqlPrestamosActivosFisicos)) {
                ps.setInt(1, recursoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("No se puede eliminar: hay prestamos Fisicos activos de este recurso.");
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlPrestamosDigitalesActivos)) {
                ps.setInt(1, recursoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("No se puede eliminar: hay préstamos DIGITALES activos de este recurso.");
                        con.rollback();
                        return false;
                    }
                }
            }

        
            try (PreparedStatement ps = con.prepareStatement(sqlDeleteTesisDetalle)) {
                ps.setInt(1, recursoId);
                ps.executeUpdate(); 
            }

            
            try (PreparedStatement ps = con.prepareStatement(sqlDeleteAccesoDigital)) {
                ps.setInt(1, recursoId);
                ps.executeUpdate();
            }
        
            try (PreparedStatement ps = con.prepareStatement(sqlDeleteSolicitudes)) {
                ps.setInt(1, recursoId);
                ps.executeUpdate();
            }

            
            try (PreparedStatement ps = con.prepareStatement(sqlDeleteCopias)) {
                ps.setInt(1, recursoId);
                ps.executeUpdate();
            }

            
            int filasRecurso;
            try (PreparedStatement ps = con.prepareStatement(sqlDeleteRecurso)) {
                ps.setInt(1, recursoId);
                filasRecurso = ps.executeUpdate();
            }

            if (filasRecurso == 0) {
                System.out.println("No se encontró el recurso con ese ID.");
                con.rollback();
                return false;
            }

            con.commit();
            return true;

        } catch (Exception e) {
            System.out.println("Error en eliminarRecurso(): " + e.getMessage());
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

    public boolean actualizarDatosBasicos(Recurso r) {
        String sql = """
            UPDATE Recurso
            SET Titulo = ?,
                Descripcion = ?,
                ISBN = ?,
                Edicion = ?,
                Editorial = ?,
                AnoPublicacion = ?,
                Idioma = ?
            WHERE RecursoID = ?
            """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getTitulo());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getIsbn());
            ps.setString(4, r.getEdicion());
            ps.setString(5, r.getEditorial());

            if (r.getAnoPublicacion() != null) {
                ps.setInt(6, r.getAnoPublicacion());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.setString(7, r.getIdioma());
            ps.setInt(8, r.getRecursoId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando recurso: " + e.getMessage());
            return false;
        }
    }

    public Recurso obtenerPorId(int recursoId) {
        String sql = "SELECT * FROM Recurso WHERE RecursoID = ?";

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Recurso r = new Recurso();
                    r.setRecursoId(rs.getInt("RecursoID"));
                    r.setTitulo(rs.getString("Titulo"));
                    r.setDescripcion(rs.getString("Descripcion"));
                    r.setTipoRecursoId(rs.getInt("TipoRecursoID"));

                    // Estos campos pueden ser NULL en BD
                    r.setIsbn(rs.getString("ISBN"));
                    r.setEdicion(rs.getString("Edicion"));
                    r.setEditorial(rs.getString("Editorial"));
                    int anio = rs.getInt("AnoPublicacion");
                    if (!rs.wasNull()) {
                        r.setAnoPublicacion(anio);
                    }
                    r.setIdioma(rs.getString("Idioma"));

                    // Stock aquí es solo lógico; si quieres, puedes dejarlo en 0
                    r.setStock(0);

                    return r;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo recurso por ID: " + e.getMessage());
        }
        return null;
    }

    public AccesoDigital obtenerAccesoDigitalPorRecurso(int recursoId) {
        String sql = """
                SELECT AccesoID, RecursoID, URLAcceso, Licencia, UsuariosConcurrentes
                FROM AccesoDigital
                WHERE RecursoID = ?
                """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AccesoDigital ad = new AccesoDigital();
                    ad.setAccesoId(rs.getInt("AccesoID"));
                    ad.setRecursoId(rs.getInt("RecursoID"));
                    ad.setUrlAcceso(rs.getString("URLAcceso"));
                    ad.setLicencia(rs.getString("Licencia"));
                    ad.setUsuariosConcurrentes(rs.getInt("UsuariosConcurrentes"));
                    return ad;
                }
            }
        } catch (Exception e) {
            System.out.println("Error en obtenerAccesoDigitalPorRecurso(): " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarAccesoDigital(AccesoDigital acceso) {
        String sql = """
                UPDATE AccesoDigital
                SET URLAcceso = ?, Licencia = ?, UsuariosConcurrentes = ?
                WHERE AccesoID = ?
                """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, acceso.getUrlAcceso());
            ps.setString(2, acceso.getLicencia());
            ps.setInt(3, acceso.getUsuariosConcurrentes());
            ps.setInt(4, acceso.getAccesoId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error en actualizarAccesoDigital(): " + e.getMessage());
            return false;
        }
    }

    public List<Recurso> listarTodos() {
        List<Recurso> lista = new ArrayList<>();

        String sql = """
            SELECT RecursoID, Titulo, Descripcion, TipoRecursoID
            FROM Recurso
            ORDER BY RecursoID
            """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Recurso r = new Recurso(
                        rs.getInt("RecursoID"),
                        rs.getString("Titulo"),
                        rs.getString("Descripcion"),
                        rs.getInt("TipoRecursoID")
                );
                
                lista.add(r);
            }

        } catch (SQLException e) {
            System.out.println("Error en listarTodos(): " + e.getMessage());
        }

        return lista;
    }

    


}
