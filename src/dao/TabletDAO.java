package dao;

import modelo.Tablet;

import java.sql.*;

public class TabletDAO {
    public int insert(Tablet tablet) throws SQLException {
        if (tablet == null) {
            throw new IllegalArgumentException("Tablet must not be null");
        }
        if (codeExists(tablet.getCode(), null)) {
            throw new SQLException("Tablet with Code " + tablet.getCode() + " already exists.");
        }

        String sql = "INSERT INTO Tablet (Brand, Model, Processor, RAM, Storage, BatteryLife, Code) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tablet.getBrand());
            ps.setString(2, tablet.getModel());
            ps.setString(3, tablet.getProcessor());
            ps.setInt(4, tablet.getRam());
            ps.setInt(5, tablet.getStorage());
            ps.setInt(6, tablet.getBatteryLife());
            ps.setString(7, tablet.getCode());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserting tablet failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    tablet.setId(id);
                    return id;
                } else {
                    throw new SQLException("Inserting tablet failed, no ID obtained.");
                }
            }
        }
    }

    public boolean update(Tablet tablet) throws SQLException {
        if (tablet == null || tablet.getId() == null) {
            throw new IllegalArgumentException("Tablet and its ID must not be null for update.");
        }
        if (codeExists(tablet.getCode(), tablet.getId())) {
            throw new SQLException("Tablet with Code " + tablet.getCode() + " already exists.");
        }

        String sql = "UPDATE Tablet SET Brand = ?, Model = ?, Processor = ?, RAM = ?, Storage = ?, BatteryLife = ?, Code = ? WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tablet.getBrand());
            ps.setString(2, tablet.getModel());
            ps.setString(3, tablet.getProcessor());
            ps.setInt(4, tablet.getRam());
            ps.setInt(5, tablet.getStorage());
            ps.setInt(6, tablet.getBatteryLife());
            ps.setString(7, tablet.getCode());
            ps.setInt(8, tablet.getId());

            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int tabletId) throws SQLException {
        String sql = "DELETE FROM Tablet WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tabletId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private boolean codeExists(String code, Integer excludingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Tablet WHERE Code = ?";
        if (excludingId != null) {
            sql += " AND ID <> ?";
        }
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            if (excludingId != null) {
                ps.setInt(2, excludingId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
