package modelo;

public class RecursoCopia {
    private int copiaId;
    private int recursoId;
    private String codigoCopia;
    private int estadoId;

    public RecursoCopia(int copiaId, int recursoId, String codigoCopia, int estadoId) {
        this.copiaId = copiaId;
        this.recursoId = recursoId;
        this.codigoCopia = codigoCopia;
        this.estadoId = estadoId;
    }

    public int getCopiaId() { return copiaId; }
    public int getRecursoId() { return recursoId; }
    public String getCodigoCopia() { return codigoCopia; }
    public int getEstadoId() { return estadoId; }

    @Override
    public String toString() {
        return codigoCopia + " (Estado=" + estadoId + ")";
    }
}
