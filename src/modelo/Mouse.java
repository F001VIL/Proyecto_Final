package modelo;

public class Mouse extends Peripheral {
    private int MouseID;
    private String type;
    private boolean isWireless;

    public Mouse(String brand, String model, String type, boolean isWireless) {
        super(brand, model);
        this.type = type;
        this.isWireless = isWireless;
    }

    public Integer getMouseID() {
        return MouseID;
    }

    public String getType() {
        return type;
    }

    public boolean isWireless() {
        return isWireless;
    }

//    Setter for MouseID
    public void setMouseID(int mouseID) {
        this.MouseID = mouseID;
    }
}
