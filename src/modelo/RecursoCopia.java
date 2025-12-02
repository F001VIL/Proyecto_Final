package modelo;

public class RecursoCopia {
    private int copiaId;
    private int recursoId;
    private String codigoCopia;
    private int estadoId;
    private String ubicacion;

    public RecursoCopia() {
    }

    public RecursoCopia(int copiaId, int recursoId, String codigoCopia, int estadoId) {
        this.copiaId = copiaId;
        this.recursoId = recursoId;
        this.codigoCopia = codigoCopia;
        this.estadoId = estadoId;
    }

    public int getCopiaId() { return copiaId; }
    public void setCopiaId(int copiaId) { this.copiaId = copiaId; }
    public void setRecursoId(int recursoId) { this.recursoId = recursoId; }
    public void setCodigoCopia(String codigoCopia) { this.codigoCopia = codigoCopia; }
    public void setEstadoId(int estadoId) { this.estadoId = estadoId; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion ) { this.ubicacion = ubicacion; }
    public int getRecursoId() { return recursoId; }
    public String getCodigoCopia() { return codigoCopia; }
    public int getEstadoId() { return estadoId; }

    @Override
    public String toString() {
        return codigoCopia + " (Estado=" + estadoId + ")";
    }
}
