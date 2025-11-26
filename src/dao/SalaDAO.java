package dao;
import dao.ConexionBD;

import modelo.Sala;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {

    public boolean registrarSala(Sala sala) {
        String sql = "INSERT INTO Sala(nombreSala, ubicacion, capacidad, descripcion) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sala.getNombreSala());
            ps.setString(2, sala.getUbicacion());
            ps.setInt(3, sala.getCapacidad());
            ps.setString(4, sala.getDescripcion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Sala> listarSalas() {
        List<Sala> lista = new ArrayList<>();
        String sql = "SELECT * FROM Sala";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sala s = new Sala(
                        rs.getInt("salaId"),
                        rs.getString("nombreSala"),
                        rs.getString("ubicacion"),
                        rs.getInt("capacidad"),
                        rs.getString("descripcion")
                );
                lista.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
