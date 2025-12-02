package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TesisDAO {
    
    public boolean insertarTesisDetalle(int recursoId, String pais, String universidad, String grado) {
        String sql = """
            INSERT INTO TesisDetalle (RecursoID, Pais, Universidad, Grado)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ps.setString(2, pais);
            ps.setString(3, universidad);
            ps.setString(4, grado);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error en insertarTesisDetalle(): " + e.getMessage());
            return false;
        }
    }
}
