package dao;

import modelo.Usuario;
import servicio.PasswordUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ===============================
    // VALIDAR INICIO DE SESIÓN
    // ===============================
    public Usuario validarLogin(String username, String password) {
        String sql = "SELECT * FROM Usuario WHERE Username = ? AND Activo = 1";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String salt = rs.getString("Salt");
                String hash = rs.getString("PasswordHash");

                boolean esPlano = hash.equals(password);

                if (esPlano || PasswordUtil.validarPassword(password, salt, hash)) {
                    Usuario u = new Usuario(
                            rs.getInt("UsuarioID"),
                            rs.getInt("PersonaID"),
                            rs.getString("Username"),
                            hash,
                            salt,
                            rs.getString("Rol"),
                            rs.getBoolean("Activo")
                    );
                    u.setPrimerInicio(rs.getBoolean("PrimerInicio"));
                    return u;
                }
            }

        } catch (SQLException e) {
            System.err.println(" Error al validar usuario: " + e.getMessage());
        }
        return null;
    }

    // CREAR USUARIO
    public boolean crearUsuarioConPersona(String codigo, String nombre, String apellido, String email,
                                          String username, String password, String rol) {

        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        String sqlPersona = "INSERT INTO Persona (CodigoUniversitario, Nombre, Apellido, Email) VALUES (?,?,?,?)";
        String sqlUsuario = "INSERT INTO Usuario (PersonaID, Username, PasswordHash, Salt, Rol, PrimerInicio) VALUES (?,?,?,?,?,1)";

        try (Connection con = ConexionBD.getConnection()) {
            con.setAutoCommit(false);

            int personaId;
            try (PreparedStatement ps = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, codigo);
                ps.setString(2, nombre);
                ps.setString(3, apellido);
                ps.setString(4, email);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("No se generó PersonaID.");
                personaId = rs.getInt(1);
            }

            try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
                ps.setInt(1, personaId);
                ps.setString(2, username);
                ps.setString(3, hash);
                ps.setString(4, salt);
                ps.setString(5, rol);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al crear usuario/persona: " + e.getMessage());
            return false;
        }
    }

    // ===============================
    // CAMBIAR CONTRASEÑA
    // ===============================
    public boolean cambiarPassword(int usuarioId, String nuevaPassword) {
        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.hashPassword(nuevaPassword, salt);
        String sql = "UPDATE Usuario SET PasswordHash=?, Salt=?, PrimerInicio=0 WHERE UsuarioID=?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, salt);
            ps.setInt(3, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }

    // ===============================
    // LISTAR USUARIOS
    // ===============================
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT UsuarioID, PersonaID, Username, Rol, Activo, PrimerInicio FROM Usuario";

        try (Connection con = ConexionBD.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("UsuarioID"),
                        rs.getInt("PersonaID"),
                        rs.getString("Username"),
                        "",
                        "",
                        rs.getString("Rol"),
                        rs.getBoolean("Activo")
                );
                u.setPrimerInicio(rs.getBoolean("PrimerInicio"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println(" Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // ===============================
    // ELIMINAR USUARIO POR ID
    // ===============================
    public boolean eliminarUsuario(int usuarioId) {
        String sql = "DELETE FROM Usuario WHERE UsuarioID = ? DELETE FROM Persona WHERE PersonaID = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setInt(2, usuarioId); // Asumiendo que PersonaID es igual a UsuarioID aquí
            
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }


    public Usuario obtenerPorId(int usuarioId) {
    String sql = """
        SELECT 
            u.UsuarioID,
            u.PersonaID,
            u.Username,
            u.PasswordHash,
            u.Salt,
            u.Rol,
            u.Activo,
            u.PrimerInicio,

            p.CodigoUniversitario,
            p.Nombre,
            p.Apellido,
            p.FechaNacimiento,
            p.Email
        FROM Usuario u
        JOIN Persona p ON u.PersonaID = p.PersonaID
        WHERE u.UsuarioID = ?
    """;

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, usuarioId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String salt = rs.getString("Salt");
            String hash = rs.getString("PasswordHash");

            // Usamos el mismo constructor que en validarLogin
            Usuario u = new Usuario(
                    rs.getInt("UsuarioID"),
                    rs.getInt("PersonaID"),
                    rs.getString("Username"),
                    hash,
                    salt,
                    rs.getString("Rol"),
                    rs.getBoolean("Activo")
            );
            u.setPrimerInicio(rs.getBoolean("PrimerInicio"));

            u.setNombre(rs.getString("Nombre"));
            u.setApellido(rs.getString("Apellido"));
            u.setEmail(rs.getString("Email"));

            Date fechaSql = rs.getDate("FechaNacimiento");
            if (fechaSql != null) {
                u.setFechaNacimiento(fechaSql.toLocalDate());
            }

            u.setCodigoUniversitario(rs.getString("CodigoUniversitario"));

            return u;
        }

    } catch (SQLException e) {
        System.out.println("Error obteniendo usuario por ID: " + e.getMessage());
    }

    return null;
    }


    public boolean actualizarUsuarioConPersona(
            int usuarioId,
            String nuevoUsername,
            String nuevoRol,
            boolean nuevoActivo,
            int personaId,
            String nuevoNombre,
            String nuevoApellido,
            LocalDate nuevaFechaNacimiento,
            String nuevoEmail) {

        String sqlPersona = """
            UPDATE Persona
            SET Nombre = ?,
                Apellido = ?,
                FechaNacimiento = ?,
                Email = ?
            WHERE PersonaID = (
                SELECT PersonaID
                FROM Usuario
                WHERE UsuarioID = ?
            )
            """;

        String sqlUsuario = """
            UPDATE Usuario
            SET Username = ?,
                Rol = ?,
                Activo = ?
            WHERE UsuarioID = ?
            """;

        try (Connection con = ConexionBD.getConnection()) {
            con.setAutoCommit(false);

            
            try (PreparedStatement psPersona = con.prepareStatement(sqlPersona)) {

                psPersona.setString(1, nuevoNombre);
                psPersona.setString(2, nuevoApellido);

                if (nuevaFechaNacimiento != null) {
                    psPersona.setDate(3, java.sql.Date.valueOf(nuevaFechaNacimiento));
                } else {
                    psPersona.setNull(3, java.sql.Types.DATE);
                }

                psPersona.setString(4, nuevoEmail);
                psPersona.setInt(5, usuarioId);  

                int filasPersona = psPersona.executeUpdate();

                
                try (PreparedStatement psUsuario = con.prepareStatement(sqlUsuario)) {
                    psUsuario.setString(1, nuevoUsername);
                    psUsuario.setString(2, nuevoRol);
                    psUsuario.setBoolean(3, nuevoActivo);
                    psUsuario.setInt(4, usuarioId);

                    int filasUsuario = psUsuario.executeUpdate();

                    if (filasPersona > 0 && filasUsuario > 0) {
                        con.commit();
                        return true;
                    } else {
                        con.rollback();
                        return false;
                    }
                }

            } catch (SQLException e) {
                con.rollback();
                System.err.println("Error actualizando usuario/persona: " + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            System.err.println("Error obteniendo conexión: " + ex.getMessage());
            return false;
        }
    }
}
