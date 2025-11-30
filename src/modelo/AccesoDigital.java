package modelo;

public class AccesoDigital {
    
    private int accesoId;
    private int recursoId;
    private String urlAcceso;
    private String licencia;
    private int usuariosConcurrentes;

    public int getAccesoId() { return accesoId; }
    public void setAccesoId(int accesoId) { this.accesoId = accesoId; }

    public int getRecursoId() { return recursoId; }
    public void setRecursoId(int recursoId) { this.recursoId = recursoId; }

    public String getUrlAcceso() { return urlAcceso; }
    public void setUrlAcceso(String urlAcceso) { this.urlAcceso = urlAcceso; }

    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }

    public int getUsuariosConcurrentes() { return usuariosConcurrentes; }
    public void setUsuariosConcurrentes(int usuariosConcurrentes) {
        this.usuariosConcurrentes = usuariosConcurrentes;
    }

}
