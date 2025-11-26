package dao;

import modelo.Solicitud;
import dao.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

    public boolean crearSolicitud(int usuarioId, String tipoMaterial, int materialId) {
        String sql = "INSERT INTO Solicitud (UsuarioID, TipoMaterial, MaterialID, Estado) VALUES (?,?,?, 'Pendiente')";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setString(2, tipoMaterial);
            ps.setInt(3, materialId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error creando solicitud: " + e.getMessage());
            return false;
        }
    }

    public boolean crearSolicitudConFechas(int usuarioId, String tipoMaterial, int materialId,
                                           Timestamp fechaPrestamo, Timestamp fechaDevolucion) {

        String sql = """
                INSERT INTO Solicitud 
                (UsuarioID, TipoMaterial, MaterialID, Fecha_prestamo, Fecha_devolucion, Estado)
                VALUES (?, ?, ?, ?, ?, 'Pendiente')
                """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setString(2, tipoMaterial);
            ps.setInt(3, materialId);
            ps.setTimestamp(4, fechaPrestamo);
            ps.setTimestamp(5, fechaDevolucion);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error creando solicitud con fechas: " + e.getMessage());
            return false;
        }
    }


    public List<Solicitud> verSolicitudesUsuario(int usuarioId) {
        List<Solicitud> lista = new ArrayList<>();

        String sql = "SELECT * FROM Solicitud WHERE UsuarioID = ? ORDER BY FechaSolicitud DESC";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Solicitud(
                        rs.getInt("SolicitudID"),
                        rs.getInt("UsuarioID"),
                        rs.getString("TipoMaterial"),
                        rs.getInt("MaterialID"),
                        rs.getString("Estado"),
                        rs.getTimestamp("FechaSolicitud")
                ));
            }

        } catch (Exception e) {
            System.out.println("Error cargando solicitudes usuario: " + e.getMessage());
        }

        return lista;
    }

    public List<Solicitud> verSolicitudesPendientes() {
        List<Solicitud> lista = new ArrayList<>();

        String sql = "SELECT * FROM Solicitud WHERE Estado = 'Pendiente'";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Solicitud(
                        rs.getInt("SolicitudID"),
                        rs.getInt("UsuarioID"),
                        rs.getString("TipoMaterial"),
                        rs.getInt("MaterialID"),
                        rs.getString("Estado"),
                        rs.getTimestamp("FechaSolicitud")
                ));
            }

        } catch (Exception e) {
            System.out.println("Error cargando solicitudes pendientes: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizarEstadoSolicitud(int solicitudId, String estado) {
        String sql = "UPDATE Solicitud SET Estado = ? WHERE SolicitudID = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, solicitudId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error actualizando solicitud: " + e.getMessage());
            return false;
        }
    }


    public Solicitud obtenerPorId(int solicitudId) {
        String sql = """
                SELECT SolicitudID, UsuarioID, TipoMaterial, MaterialID, Estado, FechaSolicitud
                FROM Solicitud
                WHERE SolicitudID = ?
                """;

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, solicitudId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Solicitud(
                            rs.getInt("SolicitudID"),
                            rs.getInt("UsuarioID"),
                            rs.getString("TipoMaterial"),
                            rs.getInt("MaterialID"),
                            rs.getString("Estado"),
                            rs.getTimestamp("FechaSolicitud")
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo solicitud por ID: " + e.getMessage());
        }

        return null;
    }
}
