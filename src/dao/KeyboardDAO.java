package dao;

import modelo.Keyboard;

import java.sql.*;

public class KeyboardDAO {
    public int insert(Keyboard keyboard) throws SQLException {
        String sql = "INSERT INTO Keyboard (Brand, Model, Layout, Type) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, keyboard.getBrand());
            ps.setString(2, keyboard.getModel());
            ps.setString(3, keyboard.getLayout());
            ps.setString(4, keyboard.getType());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserting keyboard failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    keyboard.setKeyboardID(id);
                    return id;
                } else {
                    throw new SQLException("Inserting keyboard failed, no ID obtained.");
                }
            }
        }
    }

    public boolean update(Keyboard keyboard) throws SQLException {
        if (keyboard.getKeyboardID() == null) {
            throw new IllegalArgumentException("Keyboard ID must not be null for update.");
        }
        if (isKeyboardInUse(keyboard.getKeyboardID())) {

        }
        String sql = "UPDATE Keyboard SET Brand = ?, Model = ?, Layout = ?, Type = ? WHERE KeyboardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, keyboard.getBrand());
            ps.setString(2, keyboard.getModel());
            ps.setString(3, keyboard.getLayout());
            ps.setString(4, keyboard.getType());
            ps.setInt(5, keyboard.getKeyboardID());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int keyboardId) throws SQLException {
        if (isKeyboardInUse(keyboardId)) {
        }
        String sql = "DELETE FROM Keyboard WHERE KeyboardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, keyboardId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private boolean isKeyboardInUse(int keyboardId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DesktopPC WHERE KeyboardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, keyboardId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
