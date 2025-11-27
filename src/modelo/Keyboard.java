package modelo;

public class Keyboard extends Peripheral {
    private int KeyboardID;
    private String layout;
    private String type; // e.g., mechanical, membrane

    public Keyboard(String brand, String model, String layout, String type) {
        super(brand, model);
        this.layout = layout;
        this.type = type;
    }

    public Integer getKeyboardID() { return KeyboardID; }

    public String getLayout() {
        return layout;
    }

    public String getType() {
        return type;
    }

//    Setter for KeyboardID
    public void setKeyboardID(int keyboardID) {
        this.KeyboardID = keyboardID;
    }
}
