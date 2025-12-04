package modelo;

import java.time.LocalDate;

public class Usuario {
    private int id;
    private int personaId;
    private String username;
    private String passwordHash;
    private String salt;
    private String rol;
    private boolean activo;
    private boolean primerInicio;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private String codigoUniversitario; 

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
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    public String getCodigoUniversitario() { return codigoUniversitario; }
    public void setCodigoUniversitario(String codigoUniversitario) {
        this.codigoUniversitario = codigoUniversitario;
    }


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
