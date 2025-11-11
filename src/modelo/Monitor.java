package modelo;

public class Monitor {
    private String resolution;
    private int size; // in inches

    public Monitor(String resolution, int size) {
        this.resolution = resolution;
        this.size = size;
    }

    public String getResolution() {
        return resolution;
    }

    public int getSize() {
        return size;
    }
}
