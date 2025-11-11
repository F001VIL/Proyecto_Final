package modelo;

public class Tablet extends TechnicalResource{
    private String processor;
    private int ram; // in GB
    private int storage; // in GB
    private int batteryLife; // in hours

    public Tablet(String processor, int ram, int storage, int batteryLife) {
        this.processor = processor;
        this.ram = ram;
        this.storage = storage;
        this.batteryLife = batteryLife;
    }

    public String getProcessor() {
        return processor;
    }

    public int getRam() {
        return ram;
    }

    public int getStorage() {
        return storage;
    }

    public int getBatteryLife() {
        return batteryLife;
    }

    @Override
    public String generateCode() {
        // Random number between 1000 and 9999
        int randomNum = 1000 + (int)(Math.random() * 9000);
        return "TAB-" + randomNum;
    }
}
