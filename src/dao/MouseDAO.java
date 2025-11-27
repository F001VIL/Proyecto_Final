package dao;

import modelo.Mouse;

import java.sql.*;

public class MouseDAO {
    public int insert(Mouse mouse) throws SQLException {
        String sql = "INSERT INTO Mouse (Brand, Model, Type, IsWireless) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mouse.getBrand());
            ps.setString(2, mouse.getModel());
            ps.setString(3, mouse.getType());
            ps.setBoolean(4, mouse.isWireless());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserting mouse failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    mouse.setMouseID(id);
                    return id;
                } else {
                    throw new SQLException("Inserting mouse failed, no ID obtained.");
                }
            }
        }
    }

    public boolean update(Mouse mouse) throws SQLException {
        if (mouse.getMouseID() == null) {
            throw new IllegalArgumentException("Mouse ID must not be null for update.");
        }
        if (isMouseInUse(mouse.getMouseID())) {
        }
        String sql = "UPDATE Mouse SET Brand = ?, Model = ?, Type = ?, IsWireless = ? WHERE MouseID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mouse.getBrand());
            ps.setString(2, mouse.getModel());
            ps.setString(3, mouse.getType());
            ps.setBoolean(4, mouse.isWireless());
            ps.setInt(5, mouse.getMouseID());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int mouseId) throws SQLException {
        if (isMouseInUse(mouseId)) {
        }
        String sql = "DELETE FROM Mouse WHERE MouseID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mouseId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private boolean isMouseInUse(int mouseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DesktopPC WHERE MouseID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mouseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
