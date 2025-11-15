package modelo;

public class Bibliotecario extends Usuario {

    public Bibliotecario(int id, int personaId, String username, String passwordHash, String salt, String rol, boolean activo) {
        super(id, personaId, username, passwordHash, salt, rol, activo);
    }
}
