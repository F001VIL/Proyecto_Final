package dao;

import modelo.ReservaSala;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaSalaDAO {

    private SalaDAO salaDAO;

    
    public ReservaSalaDAO() {
        this.salaDAO = new SalaDAO();
    }

    
    public boolean crearReserva(ReservaSala r) {

        String sql = "INSERT INTO ReservaSala "
                   + "(SalaID, PersonaID, FechaInicio, FechaFin, EstadoReserva, NumeroPersonas, FechaRegistro) "
                   + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, r.getSalaId());
            stmt.setInt(2, r.getPersonaID());
            stmt.setTimestamp(3, r.getFechaInicio());
            stmt.setTimestamp(4, r.getFechaFin());
            stmt.setString(5, r.getEstadoReserva());
            stmt.setInt(6, r.getNumeroPersonas());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error crearReserva: " + e.getMessage());
        }

        return false;
    }

    
    public List<ReservaSala> obtenerReservas() {
        List<ReservaSala> lista = new ArrayList<>();

        String sql = "SELECT * FROM ReservaSala";

        try (Connection connection = ConexionBD.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            Timestamp ahora = new Timestamp(System.currentTimeMillis());

            while (rs.next()) {
                ReservaSala r = convertirAReserva(rs);

                // Actualizar a FINALIZADO si corresponde
                if ("Activo".equalsIgnoreCase(r.getEstadoReserva())
                        && r.getFechaFin().before(ahora)) {

                    actualizarEstado(r.getReservaId(), "Finalizado");
                    r.setEstadoReserva("Finalizado");
                }

                r.setRecurso(salaDAO.obtenerPorId(r.getSalaId()));
                lista.add(r);
            }

        } catch (Exception e) {
            System.out.println("Error obtenerReservas: " + e.getMessage());
        }

        return lista;
    }

    
    public List<ReservaSala> obtenerPorSala(int salaId) {
        List<ReservaSala> lista = new ArrayList<>();

        String sql = "SELECT * FROM ReservaSala WHERE SalaID = ?";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, salaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReservaSala r = convertirAReserva(rs);
                    r.setRecurso(salaDAO.obtenerPorId(salaId));
                    lista.add(r);
                }
            }

        } catch (Exception e) {
            System.out.println("Error obtenerPorSala: " + e.getMessage());
        }

        return lista;
    }

    
    public boolean eliminarReserva(int reservaId) {
        String sql = "DELETE FROM ReservaSala WHERE ReservaID = ?";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, reservaId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error eliminarReserva: " + e.getMessage());
        }

        return false;
    }

    
    public boolean actualizarEstado(int reservaId, String nuevoEstado) {
        String sql = "UPDATE ReservaSala SET EstadoReserva = ? WHERE ReservaID = ?";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, reservaId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error actualizarEstado: " + e.getMessage());
        }

        return false;
    }

    
    private ReservaSala convertirAReserva(ResultSet rs) throws SQLException {
        return new ReservaSala(
                rs.getInt("ReservaID"),
                rs.getInt("SalaID"),
                rs.getInt("PersonaID"),
                rs.getTimestamp("FechaInicio"),
                rs.getTimestamp("FechaFin"),
                rs.getString("EstadoReserva"),
                rs.getInt("NumeroPersonas"),
                rs.getTimestamp("FechaRegistro")
        );
    }

    public boolean actualizarNumeroPersonas(int reservaId, int nuevoNumero) {
        String sql = "UPDATE ReservaSala SET NumeroPersonas = ? WHERE ReservaID = ?";

        try (Connection connection = ConexionBD.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, nuevoNumero);
            stmt.setInt(2, reservaId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error actualizarNumeroPersonas: " + e.getMessage());
        }

        return false;
    }

    public List<ReservaSala> obtenerPorUsuario(int personaID) {
        List<ReservaSala> lista = new ArrayList<>();

        String sql = "SELECT * FROM ReservaSala WHERE PersonaID = ?";

        try (Connection connection = ConexionBD.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, personaID);

            try (ResultSet rs = stmt.executeQuery()) {

                Timestamp ahora = new Timestamp(System.currentTimeMillis());

                while (rs.next()) {
                    ReservaSala r = convertirAReserva(rs);

                    if ("Activo".equalsIgnoreCase(r.getEstadoReserva())
                            && r.getFechaFin().before(ahora)) {

                        actualizarEstado(r.getReservaId(), "Finalizado");
                        r.setEstadoReserva("Finalizado");
                    }

                    r.setRecurso(salaDAO.obtenerPorId(r.getSalaId()));
                    lista.add(r);
                }
            }

        } catch (Exception e) {
            System.out.println("Error obtenerPorUsuario: " + e.getMessage());
        }

        return lista;
    }

    public boolean cancelarReservaUsuario(int reservaId, int personaID) {

        String sql = "UPDATE ReservaSala SET EstadoReserva = 'Cancelada' " +
                    "WHERE ReservaID = ? AND PersonaID = ? AND EstadoReserva <> 'Cancelada'";

        try (Connection connection = ConexionBD.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, reservaId);
            stmt.setInt(2, personaID);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error cancelarReservaUsuario: " + e.getMessage());
        }

        return false;
    }


    public boolean actualizarReservaUsuario(int reservaId, int personaID,Timestamp nuevaInicio, Timestamp nuevaFin,int nuevoNumeroPersonas) {

        String sql = "UPDATE ReservaSala SET FechaInicio = ?, FechaFin = ?, NumeroPersonas = ? " +
                    "WHERE ReservaID = ? AND PersonaID = ? AND EstadoReserva <> 'Cancelada'";

        try (Connection connection = ConexionBD.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setTimestamp(1, nuevaInicio);
            stmt.setTimestamp(2, nuevaFin);
            stmt.setInt(3, nuevoNumeroPersonas);
            stmt.setInt(4, reservaId);
            stmt.setInt(5, personaID);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error actualizarReservaUsuario: " + e.getMessage());
        }

        return false;
    }

    public boolean existenReservasPorSala(int salaID) {
        String sql = "SELECT COUNT(*) FROM ReservaSala WHERE SalaID = ?";

        try (Connection connection = ConexionBD.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, salaID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int cantidad = rs.getInt(1);
                    return cantidad > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("Error existenReservasPorSala: " + e.getMessage());
        }

        return false;
    }
}
