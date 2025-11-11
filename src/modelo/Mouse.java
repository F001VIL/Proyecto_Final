package modelo;

public class Mouse {
    private String type; // e.g., "Optical", "Mechanical"
    private boolean isWireless;

    public Mouse(String type, boolean isWireless) {
        this.type = type;
        this.isWireless = isWireless;
    }

    public String getType() {
        return type;
    }

    public boolean isWireless() {
        return isWireless;
    }
}
