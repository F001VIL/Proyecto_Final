package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelo.RecursoCopia;

public class RecursoCopiaDAO {

    // ==========================================================
    //      OBTENER COPIAS DISPONIBLES POR RECURSO
    // ==========================================================
    public List<Integer> obtenerCopiasDisponibles(int recursoId) {
        List<Integer> copias = new ArrayList<>();

        String sql = "SELECT CopiaID FROM RecursoCopia WHERE RecursoID = ? AND EstadoID = 1";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                copias.add(rs.getInt("CopiaID"));
            }

        } catch (Exception e) {
            System.out.println("Error buscando copias: " + e.getMessage());
        }

        return copias;
    }


    // ==========================================================
    //      ACTUALIZAR ESTADO DE UNA COPIA
    // ==========================================================
    public boolean actualizarEstadoCopia(int copiaId, int nuevoEstadoId) {
        String sql = "UPDATE RecursoCopia SET EstadoID = ? WHERE CopiaID = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoEstadoId);
            ps.setInt(2, copiaId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando estado de copia: " + e.getMessage());
            return false;
        }
    }


    // ==========================================================
    //      INSERTAR NUEVA COPIA FÍSICA
    // ==========================================================
    public boolean insertarCopiaFisica(int recursoId, String codigoCopia, String ubicacion) {
        String sql = """
                INSERT INTO RecursoCopia
                (RecursoID, CodigoCopia, Ubicacion, EstadoID, FechaAdquisicion)
                VALUES (?, ?, ?, 1, GETDATE())
                """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ps.setString(2, codigoCopia);
            ps.setString(3, ubicacion);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error en insertarCopiaFisica(): " + e.getMessage());
            return false;
        }
    }


    // ==========================================================
    //      OBTENER RECURSO ID DESDE COPIA → NECESARIO PARA STOCK
    // ==========================================================
    public int obtenerRecursoIdDesdeCopia(int copiaId) {
        String sql = "SELECT RecursoID FROM RecursoCopia WHERE CopiaID = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, copiaId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("RecursoID");
            }

        } catch (Exception e) {
            System.out.println("Error obtenerRecursoIdDesdeCopia(): " + e.getMessage());
        }

        return -1; // No encontrado
    }


    // ==========================================================
    //      ELIMINAR TODAS LAS COPIAS DE UN RECURSO
    // ==========================================================
    public boolean eliminarCopia(String codigoCopia) {
    String sql = "DELETE FROM RecursoCopia WHERE CodigoCopia = ?";

    try (Connection con = ConexionBD.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, codigoCopia);
        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println("Error al eliminar copia: " + e.getMessage());
        return false;
    }
    }

    public List<RecursoCopia> listarCopiasPorRecurso(int recursoId) {
        List<RecursoCopia> lista = new ArrayList<>();

        String sql = "SELECT CopiaID, RecursoID, CodigoCopia, Ubicacion, EstadoID " +
                    "FROM RecursoCopia WHERE RecursoID = ?";

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecursoCopia c = new RecursoCopia();
                    c.setCopiaId(rs.getInt("CopiaID"));
                    c.setRecursoId(rs.getInt("RecursoID"));
                    c.setCodigoCopia(rs.getString("CodigoCopia"));
                    c.setUbicacion(rs.getString("Ubicacion"));
                    c.setEstadoId(rs.getInt("EstadoID"));
                    lista.add(c);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error listando copias por recurso: " + e.getMessage());
        }
        return lista;
    }

    public RecursoCopia obtenerPorId(int copiaId) {
        String sql = "SELECT CopiaID, RecursoID, CodigoCopia, Ubicacion, EstadoID " +
                    "FROM RecursoCopia WHERE CopiaID = ?";

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, copiaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RecursoCopia c = new RecursoCopia();
                    c.setCopiaId(rs.getInt("CopiaID"));
                    c.setRecursoId(rs.getInt("RecursoID"));
                    c.setCodigoCopia(rs.getString("CodigoCopia"));
                    c.setUbicacion(rs.getString("Ubicacion"));
                    c.setEstadoId(rs.getInt("EstadoID"));
                    return c;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo copia por ID: " + e.getMessage());
        }
        return null;
    }
    public boolean actualizarCodigoUbicacion(int copiaId,String nuevoCodigo,String nuevaUbicacion) {
        String sql = """
            UPDATE RecursoCopia
            SET CodigoCopia = ?,
                Ubicacion = ?
            WHERE CopiaID = ?
            """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoCodigo);
            ps.setString(2, nuevaUbicacion);
            ps.setInt(3, copiaId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando copia (código/ubicación): " + e.getMessage());
            return false;
        }
    }

    public int contarCopiasFisicas(int recursoId) {
        String sql = "SELECT COUNT(*) FROM RecursoCopia WHERE RecursoID = ?";
        
        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error contando copias físicas: " + e.getMessage());
        }
        return 0;
    }


    public int contarCopiasDisponibles(int recursoId) {
        String sql = """
            SELECT COUNT(*)
            FROM RecursoCopia
            WHERE RecursoID = ?
            AND EstadoID = 1   -- Disponible
        """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error contando copias disponibles: " + e.getMessage());
        }
        return 0;
    }

}
