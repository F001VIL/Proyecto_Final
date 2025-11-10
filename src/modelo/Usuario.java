package modelo;

public class Usuario {
    private int id;
    private int personaId;
    private String username;
    private String passwordHash;
    private String salt;
    private String rol;
    private boolean activo;
    private boolean primerInicio; 

    // ==============================
    // CONSTRUCTOR PRINCIPAL
    // ==============================
    public Usuario(int id, int personaId, String username, String passwordHash, String salt, String rol, boolean activo) {
        this.id = id;
        this.personaId = personaId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.rol = rol;
        this.activo = activo;
        this.primerInicio = false; 
    }


    public int getId() { return id; }
    public int getPersonaId() { return personaId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }

    // ==============================
    // NUEVOS MÉTODOS: PRIMER INICIO
    // ==============================
    public boolean isPrimerInicio() {
        return primerInicio;
    }

    public void setPrimerInicio(boolean primerInicio) {
        this.primerInicio = primerInicio;
    }

    // ==============================
    // OPCIONAL: para imprimir usuario
    // ==============================
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", personaId=" + personaId +
                ", username='" + username + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                ", primerInicio=" + primerInicio +
                '}';
    }
}
