package dao;
import modelo.FacilityResource;
import modelo.Room;
import modelo.CampusSpace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {

    
    public SalaDAO() {
    }

    
    public boolean insertarSala(FacilityResource s) {
        String sql = "INSERT INTO Sala (NombreSala, Ubicacion, Capacidad, Descripcion, TipoRecurso) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, s.getName());
            stmt.setString(2, s.getLocation());
            stmt.setInt(3, s.getCapacity());
            stmt.setString(4, s.getDescription());
            stmt.setString(5, s.getTipoRecurso());   // 'ROOM' o 'CAMPUS'

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error insertarSala: " + e.getMessage());
        }

        return false;
    }

    
    public List<FacilityResource> obtenerTodas() {
        List<FacilityResource> lista = new ArrayList<>();
        String sql = "SELECT SalaID, NombreSala, Ubicacion, Capacidad, Descripcion, TipoRecurso FROM Sala";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirAResource(rs));
            }

        } catch (Exception e) {
            System.out.println("Error obtenerTodas: " + e.getMessage());
        }

        return lista;
    }

   
    public List<FacilityResource> obtenerPorTipo(String tipo) {
        List<FacilityResource> lista = new ArrayList<>();
        String sql = "SELECT SalaID, NombreSala, Ubicacion, Capacidad, Descripcion, TipoRecurso "
                   + "FROM Sala WHERE TipoRecurso = ?";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, tipo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertirAResource(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Error obtenerPorTipo: " + e.getMessage());
        }

        return lista;
    }

    
    public FacilityResource obtenerPorId(int id) {
        String sql = "SELECT SalaID, NombreSala, Ubicacion, Capacidad, Descripcion, TipoRecurso "
                   + "FROM Sala WHERE SalaID = ?";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return convertirAResource(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error obtenerPorId: " + e.getMessage());
        }

        return null;
    }

    
    public boolean actualizarSala(FacilityResource nueva, FacilityResource anterior) {
        String sql = "UPDATE Sala SET NombreSala = ?, Ubicacion = ?, Capacidad = ?, "
                   + "Descripcion = ?, TipoRecurso = ? WHERE SalaID = ?";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, nueva.getName() == null ? anterior.getName() : nueva.getName());
            stmt.setString(2, nueva.getLocation() == null ? anterior.getLocation() : nueva.getLocation());
            stmt.setInt(3, nueva.getCapacity() == 0 ? anterior.getCapacity() : nueva.getCapacity());
            stmt.setString(4, nueva.getDescription() == null ? anterior.getDescription() : nueva.getDescription());
            stmt.setString(5, nueva.getTipoRecurso() == null ? anterior.getTipoRecurso() : nueva.getTipoRecurso());
            stmt.setInt(6, anterior.getId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error actualizarSala: " + e.getMessage());
        }

        return false;
    }

    
    public boolean eliminarSala(int salaId) {
        String sql = "DELETE FROM Sala WHERE SalaID = ?";

        try (Connection connection = ConexionBD.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, salaId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error eliminarSala: " + e.getMessage());
        }

        return false;
    }

    
    private FacilityResource convertirAResource(ResultSet rs) throws SQLException {

        int id = rs.getInt("SalaID");
        String name = rs.getString("NombreSala");
        String location = rs.getString("Ubicacion");
        int capacity = rs.getInt("Capacidad");
        String description = rs.getString("Descripcion");
        String tipo = rs.getString("TipoRecurso");

        if ("ROOM".equalsIgnoreCase(tipo)) {
            return new Room(id, name, location, capacity, description, "GENERAL");
        } else {
            return new CampusSpace(id, name, location, capacity, description, "GENERAL");
        }
    }
}