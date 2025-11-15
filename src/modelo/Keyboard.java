package modelo;

public class Keyboard {
    private String layout;
    private String type; // e.g., mechanical, membrane

    public Keyboard(String layout, String type) {
        this.layout = layout;
        this.type = type;
    }

    public String getLayout() {
        return layout;
    }

    public String getType() {
        return type;
    }
}
