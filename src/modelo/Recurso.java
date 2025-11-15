package modelo;

public class Recurso {
    private int recursoId;
    private String titulo;
    private String descripcion;
    private int tipoRecursoId;

    public Recurso(int recursoId, String titulo, String descripcion, int tipoRecursoId) {
        this.recursoId = recursoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoRecursoId = tipoRecursoId;
    }

    public int getRecursoId() { return recursoId; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public int getTipoRecursoId() { return tipoRecursoId; }

    @Override
    public String toString() {
        return recursoId + " - " + titulo;
    }
}
