package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

}
