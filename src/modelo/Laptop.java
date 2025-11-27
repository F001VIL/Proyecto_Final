package modelo;

public class Laptop extends Computer {
    private String brand;
    private String model;
    private double screenSize; // in inches
    private double weight; // in kg
    private int batteryLife; // in hours

    public Laptop(String brand, String model, String processor, int ram, int storage, int disk, double screenSize, double weight, int batteryLife) {
        super(processor, ram, storage, disk);
        this.brand = brand;
        this.model = model;
        this.screenSize = screenSize;
        this.weight = weight;
        this.batteryLife = batteryLife;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }
    
    public double getScreenSize() {
        return screenSize;
    }

    public double getWeight() {
        return weight;
    }

    public int getBatteryLife() {
        return batteryLife;
    }

    @Override
    public String generateCode() {
        // Random number between 1000 and 9999
        int randomNum = 1000 + (int)(Math.random() * 9000);
        return "LAP-" + randomNum;
    }
}
