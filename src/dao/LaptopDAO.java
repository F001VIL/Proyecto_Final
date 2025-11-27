package dao;

import modelo.Laptop;

import java.sql.*;

public class LaptopDAO {
    public int insert(Laptop laptop) throws SQLException {
        if (laptop == null) {
            throw new IllegalArgumentException("Laptop must not be null");
        }
        if (codeExists(laptop.getCode(), null)) {
            throw new SQLException("Laptop with Code " + laptop.getCode() + " already exists.");
        }

        String sql = "INSERT INTO Laptop (Brand, Model, Processor, RAM, Storage, Disk, ScreenSize, Weight, BatteryLife, Code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, laptop.getBrand());
            ps.setString(2, laptop.getModel());
            ps.setString(3, laptop.getProcessor());
            ps.setInt(4, laptop.getRam());
            ps.setInt(5, laptop.getStorage());
            ps.setInt(6, laptop.getDisk());
            ps.setDouble(7, laptop.getScreenSize());
            ps.setDouble(8, laptop.getWeight());
            ps.setInt(9, laptop.getBatteryLife());
            ps.setString(10, laptop.getCode());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserting laptop failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    laptop.setId(id);
                    return id;
                } else {
                    throw new SQLException("Inserting laptop failed, no ID obtained.");
                }
            }
        }
    }

    public boolean update(Laptop laptop) throws SQLException {
        if (laptop == null || laptop.getId() == null) {
            throw new IllegalArgumentException("Laptop and its ID must not be null for update.");
        }
        if (codeExists(laptop.getCode(), laptop.getId())) {
            throw new SQLException("Laptop with Code " + laptop.getCode() + " already exists.");
        }

        String sql = "UPDATE Laptop SET Brand = ?, Model = ?, Processor = ?, RAM = ?, Storage = ?, Disk = ?, ScreenSize = ?, Weight = ?, BatteryLife = ?, Code = ? WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, laptop.getBrand());
            ps.setString(2, laptop.getModel());
            ps.setString(3, laptop.getProcessor());
            ps.setInt(4, laptop.getRam());
            ps.setInt(5, laptop.getStorage());
            ps.setInt(6, laptop.getDisk());
            ps.setDouble(7, laptop.getScreenSize());
            ps.setDouble(8, laptop.getWeight());
            ps.setInt(9, laptop.getBatteryLife());
            ps.setString(10, laptop.getCode());
            ps.setInt(11, laptop.getId());

            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int laptopId) throws SQLException {
        String sql = "DELETE FROM Laptop WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, laptopId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private boolean codeExists(String code, Integer excludingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Laptop WHERE Code = ?";
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
