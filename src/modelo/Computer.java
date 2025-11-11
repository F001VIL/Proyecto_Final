package modelo;

public abstract class Computer extends TechnicalResource {
    private String processor;
    private int ram; // in GB
    private int storage; // in GB
    private int disk;

    public Computer(String processor, int ram, int storage, int disk) {
        this.processor = processor;
        this.ram = ram;
        this.storage = storage;
        this.disk = disk;
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

    public int getDisk() {
        return disk;
    }
}
