package servicio;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    // Genera una sal aleatoria
    public static String generarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Genera un hash SHA-256 a partir de la contraseña + sal
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt)); // añadir la sal antes de hashear
            byte[] hashed = md.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar hash de contraseña", e);
        }
    }

    // Valida una contraseña comparando su hash con el almacenado
    public static boolean validarPassword(String passwordIngresada, String salt, String hashAlmacenado) {
        String hashIntento = hashPassword(passwordIngresada, salt);
        return hashIntento.equals(hashAlmacenado);
    }
}
