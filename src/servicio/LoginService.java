package servicio;

import dao.ConexionBD;
import java.security.MessageDigest;
import java.sql.*;

public class LoginService {

    public static boolean validarCredenciales(String username, String password) {
        String sql = "SELECT PasswordHash, Salt, Activo FROM Usuario WHERE Username = ?";
        
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean activo = rs.getBoolean("Activo");
                if (!activo) {
                    System.out.println("El usuario está inactivo.");
                    return false;
                }

                byte[] storedHash = rs.getBytes("PasswordHash");
                String salt = rs.getString("Salt");

                // Generar hash SHA-1 del password ingresado + salt
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] computedHash = md.digest((salt + password).getBytes());

                // Comparar hashes byte a byte
                if (MessageDigest.isEqual(storedHash, computedHash)) {
                    System.out.println("Inicio de sesión exitoso.");
                    return true;
                } else {
                    System.out.println("Credenciales incorrectas.");
                    return false;
                }
            } else {
                System.out.println("Usuario no encontrado.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
            return false;
        }
    }
}
