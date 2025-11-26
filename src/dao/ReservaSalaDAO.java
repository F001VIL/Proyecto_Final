package dao;

import modelo.ReservaSala;
import modelo.Sala;
import dao.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaSalaDAO {

    public boolean reservarSala(int salaId, int personaId, Timestamp inicio, Timestamp fin) {

        String sql = "INSERT INTO ReservaSala (SalaID, PersonaID, FechaInicio, FechaFin, EstadoReserva) " +
                     "VALUES (?, ?, ?, ?, 'Activa')";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, salaId);
            ps.setInt(2, personaId);
            ps.setTimestamp(3, inicio);
            ps.setTimestamp(4, fin);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error reservando sala: " + e.getMessage());
            return false;
        }
    }

    public List<ReservaSala> listarReservas() {
        List<ReservaSala> lista = new ArrayList<>();

        String sql = "SELECT * FROM ReservaSala ORDER BY FechaInicio DESC";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new ReservaSala(
                        rs.getInt("ReservaID"),
                        rs.getInt("SalaID"),
                        rs.getInt("PersonaID"),
                        rs.getTimestamp("FechaInicio"),
                        rs.getTimestamp("FechaFin"),
                        rs.getString("EstadoReserva")
                ));
            }

        } catch (Exception e) {
            System.out.println("Error listando reservas: " + e.getMessage());
        }

        return lista;
    }
}
