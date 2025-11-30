package modelo;

public class Recurso {
    private int recursoId;
    private String titulo;
    private String descripcion;
    private int tipoRecursoId;
    private int stock;

    private String isbn;
    private String edicion;
    private String editorial;
    private Integer anoPublicacion;
    private String idioma;

    public Recurso() {
    }

    public Recurso(int recursoId, String titulo, String descripcion, int tipoRecursoId) {
        this.recursoId = recursoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoRecursoId = tipoRecursoId;
    }

    public int getRecursoId() { return recursoId; }
    public void setRecursoId(int recursoId) { this.recursoId = recursoId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getTipoRecursoId() { return tipoRecursoId; }
    public void setTipoRecursoId(int tipoRecursoId) { this.tipoRecursoId = tipoRecursoId; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getEdicion() { return edicion; }
    public void setEdicion(String edicion) { this.edicion = edicion; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public Integer getAnoPublicacion() { return anoPublicacion; }
    public void setAnoPublicacion(Integer anoPublicacion) { this.anoPublicacion = anoPublicacion; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    @Override
    public String toString() {
    return "ID: " + recursoId +
           " | Título: " + titulo +
           " | Stock: " + stock;
           }
}
