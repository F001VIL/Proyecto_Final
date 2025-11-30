package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import modelo.PrestamoDigitalResumen;

public class PrestamoDigitalDAO {
    
    public boolean registrarPrestamoDigital(int recursoId, int personaId,
                                            Timestamp fechaPrestamo,
                                            Timestamp fechaVencimiento) {

        String sql = """
            INSERT INTO PrestamoDigital
                (RecursoID, PersonaID, FechaPrestamo, FechaVencimiento)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recursoId);
            ps.setInt(2, personaId);
            ps.setTimestamp(3, fechaPrestamo);
            ps.setTimestamp(4, fechaVencimiento);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error registrando préstamo DIGITAL: " + e.getMessage());
            return false;
        }
    }



    public void cerrarPrestamosVencidos() {
        String sql = """
            UPDATE PrestamoDigital
            SET FechaDevolucion = FechaVencimiento,
                EstadoPrestamo = 'Expirado'
            WHERE FechaVencimiento < SYSDATETIME()
            AND FechaDevolucion IS NULL
            """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error cerrando préstamos digitales vencidos: " + e.getMessage());
        }
    }

    public int contarPrestamosActivosPorPersona(int personaId) {
        String sql = """
            SELECT COUNT(*)
            FROM PrestamoDigital
            WHERE PersonaID = ?
            AND FechaDevolucion IS NULL
            """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error contando préstamos digitales activos: " + e.getMessage());
        }
        return 0;
    }


    public List<PrestamoDigitalResumen> listarPrestamosActivosPorPersona(int personaId) {
        String sql = """
            SELECT pd.PrestamoDigitalID, pd.RecursoID, r.Titulo, pd.FechaPrestamo, pd.FechaVencimiento
            FROM PrestamoDigital pd
            JOIN Recurso r ON pd.RecursoID = r.RecursoID
            WHERE pd.PersonaID = ?
            AND pd.FechaDevolucion IS NULL
            """;

        List<PrestamoDigitalResumen> lista = new ArrayList<>();

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrestamoDigitalResumen p = new PrestamoDigitalResumen(
                            rs.getInt("PrestamoDigitalID"),
                            rs.getInt("RecursoID"),
                            rs.getString("Titulo"),
                            rs.getTimestamp("FechaPrestamo").toLocalDateTime(),
                            rs.getTimestamp("FechaVencimiento").toLocalDateTime()
                    );
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listando préstamos digitales: " + e.getMessage());
        }
        return lista;
    }

    public boolean devolverPrestamoDigital(int prestamoDigitalId) {
        String sql = """
            UPDATE PrestamoDigital
            SET FechaDevolucion = SYSDATETIME(),
                EstadoPrestamo = 'Devuelto'
            WHERE PrestamoDigitalID = ?
            AND FechaDevolucion IS NULL
            """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, prestamoDigitalId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al devolver préstamo digital: " + e.getMessage());
            return false;
        }
    }



    
}
