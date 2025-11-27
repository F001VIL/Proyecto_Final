package modelo;

public class Monitor extends Peripheral{
    private int MonitorID;
    private String resolution;
    private int size; // in inches

    public Monitor(String brand, String model, String resolution, int size) {
        super(brand, model);
        this.resolution = resolution;
        this.size = size;
    }

    public String getResolution() {
        return resolution;
    }

    public int getSize() {
        return size;
    }

    public Integer getMonitorID() {
        return MonitorID;
    }

//    Setter for MonitorID
    public void setMonitorID(int monitorID) {
        this.MonitorID = monitorID;
    }
}
