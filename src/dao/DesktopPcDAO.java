package dao;

import modelo.DesktopPc;

import java.sql.*;

public class DesktopPcDAO {
    public int insert(DesktopPc pc) throws SQLException {
        // validate peripherals and code
        int gcId = extractGraphicCardId(pc);
        int monitorId = extractMonitorId(pc);
        int keyboardId = extractKeyboardId(pc);
        int mouseId = extractMouseId(pc);

        checkPeripheralNotUsed("GraphicCardID", gcId, null);
        checkPeripheralNotUsed("MonitorID", monitorId, null);
        checkPeripheralNotUsed("KeyboardID", keyboardId, null);
        checkPeripheralNotUsed("MouseID", mouseId, null);

        if (codeExists(pc.getCode(), null)) {
        }

        String sql = "INSERT INTO DesktopPC (Processor, RAM, Storage, Disk, GraphicCardID, MonitorID, KeyboardID, MouseID, Code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pc.getProcessor());
            ps.setInt(2, pc.getRam());
            ps.setInt(3, pc.getStorage());
            ps.setInt(4, pc.getDisk());
            ps.setInt(5, gcId);
            ps.setInt(6, monitorId);
            ps.setInt(7, keyboardId);
            ps.setInt(8, mouseId);
            ps.setString(9, pc.getCode());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserting DesktopPC failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    pc.setId(id);
                    return id;
                } else {
                    throw new SQLException("Inserting DesktopPC failed, no ID obtained.");
                }
            }
        }
    }

    public boolean update(DesktopPc pc) throws SQLException {
        if (pc.getId() == null) {
            throw new IllegalArgumentException("DesktopPC ID must not be null for update.");
        }

        int gcId = extractGraphicCardId(pc);
        int monitorId = extractMonitorId(pc);
        int keyboardId = extractKeyboardId(pc);
        int mouseId = extractMouseId(pc);

        checkPeripheralNotUsed("GraphicCardID", gcId, pc.getId());
        checkPeripheralNotUsed("MonitorID", monitorId, pc.getId());
        checkPeripheralNotUsed("KeyboardID", keyboardId, pc.getId());
        checkPeripheralNotUsed("MouseID", mouseId, pc.getId());

        if (codeExists(pc.getCode(), pc.getId())) {
            throw new SQLException("DesktopPC with Code " + pc.getCode() + " already exists.");
        }

        String sql = "UPDATE DesktopPC SET Processor = ?, RAM = ?, Storage = ?, Disk = ?, GraphicCardID = ?, MonitorID = ?, KeyboardID = ?, MouseID = ?, Code = ? WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pc.getProcessor());
            ps.setInt(2, pc.getRam());
            ps.setInt(3, pc.getStorage());
            ps.setInt(4, pc.getDisk());
            ps.setInt(5, gcId);
            ps.setInt(6, monitorId);
            ps.setInt(7, keyboardId);
            ps.setInt(8, mouseId);
            ps.setString(9, pc.getCode());
            ps.setInt(10, pc.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int desktopId) throws SQLException {
        String sql = "DELETE FROM DesktopPC WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, desktopId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    // --- helpers ---

    private void checkPeripheralNotUsed(String column, int peripheralId, Integer excludingDesktopId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DesktopPC WHERE " + column + " = ?";
        if (excludingDesktopId != null) {
            sql += " AND ID <> ?";
        }
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, peripheralId);
            if (excludingDesktopId != null) {
                ps.setInt(2, excludingDesktopId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                }
            }
        }
    }

    private boolean codeExists(String code, Integer excludingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DesktopPC WHERE Code = ?";
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

    // Extractors from model objects (throw clear exceptions if missing)
    private int extractGraphicCardId(DesktopPc pc) {
        if (pc == null || pc.getGraphicsCard() == null || pc.getGraphicsCard().getGraphicCardID() == null) {
            throw new IllegalStateException("GraphicCard or its ID is null for DesktopPc.");
        }
        return pc.getGraphicsCard().getGraphicCardID();
    }

    private int extractMonitorId(DesktopPc pc) {
        if (pc == null || pc.getMonitor() == null || pc.getMonitor().getMonitorID() == null) {
            throw new IllegalStateException("Monitor or its ID is null for DesktopPc.");
        }
        return pc.getMonitor().getMonitorID();
    }

    private int extractKeyboardId(DesktopPc pc) {
        if (pc == null || pc.getKeyboard() == null || pc.getKeyboard().getKeyboardID() == null) {
            throw new IllegalStateException("Keyboard or its ID is null for DesktopPc.");
        }
        return pc.getKeyboard().getKeyboardID();
    }

    private int extractMouseId(DesktopPc pc) {
        if (pc == null || pc.getMouse() == null || pc.getMouse().getMouseID() == null) {
            throw new IllegalStateException("Mouse or its ID is null for DesktopPc.");
        }
        return pc.getMouse().getMouseID();
    }
}
