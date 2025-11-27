package modelo;

public abstract class Peripheral {
    protected String brand;
    protected String model;

    public Peripheral(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }
}
