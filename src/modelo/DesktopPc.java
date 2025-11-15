package modelo;

public class DesktopPc extends Computer {
    private String graphicsCard;
    private Monitor monitor;
    private Keyboard keyboard;
    private Mouse mouse;

    public DesktopPc(String processor, int ram, int storage, int disk, String graphicsCard, Monitor monitor, Keyboard keyboard, Mouse mouse) {
        super(processor, ram, storage, disk);
        this.graphicsCard = graphicsCard;
        this.monitor = monitor;
        this.keyboard = keyboard;
        this.mouse = mouse;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Mouse getMouse() {
        return mouse;
    }

    @Override
    public String generateCode() {
//        Random number between 1000 and 9999
         int randomNum = 1000 + (int)(Math.random() * 9000);
        return "DPC-" + randomNum;
    }
}
