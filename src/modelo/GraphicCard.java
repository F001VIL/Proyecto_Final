package modelo;

public class GraphicCard extends Peripheral {
    private int GraphicCardID;
    private int memory;
    private String chipset;

    public GraphicCard(String brand, String model, int memory, String chipset) {
        super(brand, model);
        this.memory = memory;
        this.chipset = chipset;
    }

    public Integer getGraphicCardID() {
        return GraphicCardID;
    }

    public int getMemory() {
        return memory;
    }

    public String getChipset() {
        return chipset;
    }

//    Setter for GraphicCardID
    public void setGraphicCardID(int graphicCardID) {
        this.GraphicCardID = graphicCardID;
    }
}
