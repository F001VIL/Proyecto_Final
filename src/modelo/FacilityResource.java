package modelo;

public abstract class FacilityResource implements Resource {

    protected int id;
    protected String code;
    protected int capacity;
    protected String name;
    protected String location;
    protected String description;
    protected String tipoRecurso; // ROOM o CAMPUS

    public FacilityResource(int id, String name, String location, int capacity,
                            String description, String tipoRecurso) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
        this.tipoRecurso = tipoRecurso;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }

    public String getTipoRecurso() {
        return tipoRecurso;
    }
}