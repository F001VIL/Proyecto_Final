package dao;

import modelo.Monitor;

import java.sql.*;

public class MonitorDAO {

    public int insert(modelo.Monitor monitor) throws SQLException {
        String sql = "INSERT INTO Monitor (Brand, Model, Resolution, Size) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, monitor.getBrand());
            ps.setString(2, monitor.getModel());
            ps.setString(3, monitor.getResolution());
            ps.setInt(4, monitor.getSize());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserting monitor failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    monitor.setMonitorID(id);
                    return id;
                } else {
                    throw new SQLException("Inserting monitor failed, no ID obtained.");
                }
            }
        }
    }

    public boolean update(Monitor monitor) throws SQLException {
        if (monitor.getMonitorID() == null) {
            throw new IllegalArgumentException("Monitor ID must not be null for update.");
        }
        if (isMonitorInUse(monitor.getMonitorID())) {

        }
        String sql = "UPDATE Monitor SET Brand = ?, Model = ?, Resolution = ?, Size = ? WHERE MonitorID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, monitor.getBrand());
            ps.setString(2, monitor.getModel());
            ps.setString(3, monitor.getResolution());
            ps.setInt(4, monitor.getSize());
            ps.setInt(5, monitor.getMonitorID());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int monitorId) throws SQLException {
        if (isMonitorInUse(monitorId)) {

        }
        String sql = "DELETE FROM Monitor WHERE MonitorID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, monitorId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private boolean isMonitorInUse(int monitorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DesktopPC WHERE MonitorID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, monitorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
}