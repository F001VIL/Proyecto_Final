package modelo;

import java.time.LocalDate;

public abstract class Physical extends Material {

    private String format;
    private String sede;
    private int stock;

    public Physical(int id, String title, String author, LocalDate publicationDate, String format, String sede, int stock) {
        super(id, title, author, publicationDate);
        this.format = format;
        this.sede = sede;
        this.stock = stock;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getInfo(){
        return String.format("[%s] %s — format:%s, sede:%s, stock:%d", getPublicationDate().getYear(), getTitle(), format, sede, stock);
    }
}
