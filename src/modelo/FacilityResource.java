package modelo;

public abstract class FacilityResource implements Resource{
    private String code;
    private int capacity;

    public FacilityResource(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
