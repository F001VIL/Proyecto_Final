package dao;

import modelo.Penalidad;


import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class PenalidadDAO {

    public boolean tienePenalidadActiva(int personaId) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM Penalidad
            WHERE PersonaID = ? AND Pagada = 0
        """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personaId);
            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt("total") > 0;

        } catch (SQLException e) {
            System.out.println("Error verificando penalidades: " + e.getMessage());
        }
        return false;
    }


    public int contarRetrasos(int personaId) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM Penalidad
            WHERE PersonaID = ? AND TipoPenalidadID = 1 AND Pagada = 0
        """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personaId);
            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt("total");

        } catch (SQLException e) {
            System.out.println("Error contando retrasos: " + e.getMessage());
        }
        return 0;
    }


    public boolean registrarPenalidad(int prestamoId, int personaId,int tipoPenalidadId, BigDecimal monto,String observacion) {

        String sql = """
            INSERT INTO Penalidad
            (PrestamoID, PersonaID, TipoPenalidadID, Monto, Observacion)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, prestamoId);
            ps.setInt(2, personaId);
            ps.setInt(3, tipoPenalidadId);
            ps.setBigDecimal(4, monto);
            ps.setString(5, observacion);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error registrando penalidad: " + e.getMessage());
        }
        return false;
    }


    public boolean registrarPenalidadPorRetraso(int prestamoId,int personaId,long diasRetraso,BigDecimal montoBasePorDia) {

        BigDecimal monto = montoBasePorDia.multiply(
                BigDecimal.valueOf(diasRetraso)
        );

        String obser = "Retraso de " + diasRetraso + " días.";

        return registrarPenalidad(
                prestamoId,
                personaId,
                1,   // 1 = Retraso
                monto,
                obser
        );
    }

    public boolean registrarPenalidadPorDanio(int prestamoId,int personaId,BigDecimal monto,String observacion) {

        return registrarPenalidad(
                prestamoId,
                personaId,
                2,   // 2 = Daño
                monto,
                observacion
        );
    }

    public boolean registrarPenalidadPorPerdida(int prestamoId,int personaId,BigDecimal monto,String observacion) {

        return registrarPenalidad(
                prestamoId,
                personaId,
                3,   // 3 = Perdida
                monto,
                observacion
        );
    }

    public boolean registrarBloqueoPorReincidencia(int personaId) {

        String observacion = "Bloqueo por reincidencia (2 retrasos en préstamos)";
        BigDecimal monto = BigDecimal.ZERO;

        String sql = """
            INSERT INTO Penalidad
            (PrestamoID, PersonaID, TipoPenalidadID, Monto, Observacion)
            VALUES (NULL, ?, 1, ?, ?)
        """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personaId);
            ps.setBigDecimal(2, monto);
            ps.setString(3, observacion);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error registrando bloqueo: " + e.getMessage());
        }
        return false;
    }


    public List<Penalidad> listarPenalidadesActivasPorPersona(int personaId) {
        List<Penalidad> lista = new ArrayList<>();

        String sql = "SELECT * FROM Penalidad WHERE PersonaID = ? AND Pagada = 0";

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Penalidad p = new Penalidad(
                        rs.getInt("PenalidadID"),
                        rs.getInt("PrestamoID"),
                        rs.getInt("PersonaID"),
                        rs.getInt("TipoPenalidadID"),
                        rs.getBigDecimal("Monto"),
                        rs.getTimestamp("FechaAplicacion"),
                        rs.getBoolean("Pagada"),
                        rs.getString("Observacion")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error listando penalidades: " + e.getMessage());
        }

        return lista;
    }

    public boolean marcarPenalidadPagada(int penalidadId) {
        String sql = "UPDATE Penalidad SET Pagada = 1 WHERE PenalidadID = ?";

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, penalidadId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error marcando penalidad pagada: " + e.getMessage());
            return false;
        }
    }
    
}
