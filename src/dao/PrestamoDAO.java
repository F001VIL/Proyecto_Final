package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import modelo.Prestamo;

public class PrestamoDAO {
    
    // Registrar un nuevo préstamo
    public boolean registrarPrestamo(int copiaId, int personaId,
                                     Timestamp fechaPrestamo,
                                     Timestamp fechaVencimiento) {

        String sql = "INSERT INTO Prestamo " +
                     "(CopiaID, PersonaID, FechaPrestamo, FechaVencimiento, EstadoPrestamo) " +
                     "VALUES (?,?,?,?, 'Activo')";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, copiaId);
            ps.setInt(2, personaId);
            ps.setTimestamp(3, fechaPrestamo);
            ps.setTimestamp(4, fechaVencimiento);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error registrando préstamo: " + e.getMessage());
            return false;
        }
    }

    // Registrar la devolución de un préstamo
    public boolean registrarDevolucion(int prestamoId,Timestamp fechaDevolucion,String observacion) {
        String sql = "UPDATE Prestamo " +
                    "SET FechaDevolucion = ?, " +
                    "    EstadoPrestamo = 'Devuelto', " +
                    "    Observaciones = ? " +
                    "WHERE PrestamoID = ?";

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, fechaDevolucion);
            ps.setString(2, observacion);
            ps.setInt(3, prestamoId);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error registrando devolución: " + e.getMessage());
            return false;
        }
    }

    public Prestamo obtenerPorId(int prestamoId) {

        String sql = "SELECT PrestamoID, CopiaID, PersonaID, " +
                     "       FechaPrestamo, FechaVencimiento, FechaDevolucion, EstadoPrestamo " +
                     "FROM Prestamo " +
                     "WHERE PrestamoID = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, prestamoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Prestamo(
                            rs.getInt("PrestamoID"),
                            rs.getInt("CopiaID"),
                            rs.getInt("PersonaID"),
                            rs.getTimestamp("FechaPrestamo"),
                            rs.getTimestamp("FechaVencimiento"),
                            rs.getTimestamp("FechaDevolucion"),
                            rs.getString("EstadoPrestamo")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo préstamo por ID: " + e.getMessage());
        }

        return null;
    }

    public List<Prestamo> listarPrestamosActivos() {
        String sql = "SELECT * FROM Prestamo WHERE FechaDevolucion IS NULL";
        List<Prestamo> lista = new ArrayList<>();

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prestamo p = new Prestamo(
                        rs.getInt("PrestamoID"),
                        rs.getInt("CopiaID"),
                        rs.getInt("PersonaID"),
                        rs.getTimestamp("FechaPrestamo"),
                        rs.getTimestamp("FechaVencimiento"),
                        rs.getTimestamp("FechaDevolucion"),
                        rs.getString("EstadoPrestamo")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error listando préstamos activos: " + e.getMessage());
        }

        return lista;
    }

    public List<Prestamo> listarTodos() {
        String sql = "SELECT PrestamoID, CopiaID, PersonaID, " +
                    "       FechaPrestamo, FechaVencimiento, FechaDevolucion, EstadoPrestamo " +
                    "FROM Prestamo " +
                    "ORDER BY FechaPrestamo DESC";

        List<Prestamo> lista = new ArrayList<>();

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prestamo p = new Prestamo(
                    rs.getInt("PrestamoID"),
                    rs.getInt("CopiaID"),
                    rs.getInt("PersonaID"),
                    rs.getTimestamp("FechaPrestamo"),
                    rs.getTimestamp("FechaVencimiento"),
                    rs.getTimestamp("FechaDevolucion"),
                    rs.getString("EstadoPrestamo")   // ← parámetro extra, el objeto lo ignora
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error listando préstamos: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizarObservacion(int prestamoId, String observacion) {
        String sql = "UPDATE Prestamo SET Observaciones = ? WHERE PrestamoID = ?";

        try (Connection con = ConexionBD.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, observacion);
            ps.setInt(2, prestamoId);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando observación de préstamo: " + e.getMessage());
            return false;
        }
    }

}
