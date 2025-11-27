package dao;

import modelo.GraphicCard;

import java.sql.*;

public class GraphicCardDAO {

    public int insert(GraphicCard gc) throws SQLException {
        String sql = "INSERT INTO GraphicCard (Brand, Model, Memory, Chipset) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gc.getBrand());
            ps.setString(2, gc.getModel());
            ps.setInt(3, gc.getMemory());
            ps.setString(4, gc.getChipset());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserting graphic card failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    gc.setGraphicCardID(id);
                    return id;
                } else {
                    throw new SQLException("Inserting graphic card failed, no ID obtained.");
                }
            }
        }
    }

    public boolean update(GraphicCard gc) throws SQLException {
        if (gc.getGraphicCardID() == null) {
            throw new IllegalArgumentException("GraphicCard ID must not be null for update.");
        }
        if (isGraphicCardInUse(gc.getGraphicCardID())) {
        }
        String sql = "UPDATE GraphicCard SET Brand = ?, Model = ?, Memory = ?, Chipset = ? WHERE GraphicCardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gc.getBrand());
            ps.setString(2, gc.getModel());
            ps.setInt(3, gc.getMemory());
            ps.setString(4, gc.getChipset());
            ps.setInt(5, gc.getGraphicCardID());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int graphicCardId) throws SQLException {
        if (isGraphicCardInUse(graphicCardId)) {
        }
        String sql = "DELETE FROM GraphicCard WHERE GraphicCardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, graphicCardId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private boolean isGraphicCardInUse(int graphicCardId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DesktopPC WHERE GraphicCardID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, graphicCardId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}