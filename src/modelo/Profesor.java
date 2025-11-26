package modelo;

public class Profesor extends Usuario {

    public Profesor(int id, int personaId, String username, String passwordHash, String salt, String rol, boolean activo) {
        super(id, personaId, username, passwordHash, salt, rol, activo);
    }
}
