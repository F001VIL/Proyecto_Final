package modelo;

public class Recurso {
    private int recursoId;
    private String titulo;
    private String descripcion;
    private int tipoRecursoId;
    private int stock;

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
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
    return "ID: " + recursoId +
           " | Título: " + titulo +
           " | Stock: " + stock;
           }
}
