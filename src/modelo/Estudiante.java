package modelo;

public class Estudiante extends Usuario {

    public Estudiante(int id, int personaId, String username, String passwordHash, String salt, String rol, boolean activo) {
        super(id, personaId, username, passwordHash, salt, rol, activo);
    }
}
