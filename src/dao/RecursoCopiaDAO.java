package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoCopiaDAO {

    // Obtener copias disponibles por recurso
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
}
