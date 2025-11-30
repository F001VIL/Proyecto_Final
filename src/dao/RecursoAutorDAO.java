package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RecursoAutorDAO {
    public boolean insertarAutor(int recursoId, String nombreCompleto) {
        String sql = """
            INSERT INTO Autor (RecursoID, NombreCompleto)
            VALUES (?, ?)
            """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ps.setString(2, nombreCompleto);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error en insertarAutor(): " + e.getMessage());
            return false;
        }
    }
}
