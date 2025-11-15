package modelo;

public class Sala {
    private int salaId;
    private String nombreSala;
    private String ubicacion;
    private int capacidad;
    private String descripcion;

    public Sala(int salaId, String nombreSala, String ubicacion, int capacidad, String descripcion) {
        this.salaId = salaId;
        this.nombreSala = nombreSala;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.descripcion = descripcion;
    }

    public int getSalaId() { return salaId; }
    public String getNombreSala() { return nombreSala; }
    public String getUbicacion() { return ubicacion; }
    public int getCapacidad() { return capacidad; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return salaId + " - " + nombreSala + " (" + capacidad + " personas)";
    }
}
