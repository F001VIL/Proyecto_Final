package modelo;

import java.time.LocalDate;

public abstract class Physical extends Material {

    private String format;
    private String sede;
    private int stock;
    private String isbn;
    private String editorial;
    private String edition;

    public Physical(int id, String title, String author, String language ,LocalDate publicationDate, String format, String sede, int stock, String isbn, String editorial, String edition ) {
        super(id, title, author, language, publicationDate);
        this.format = format;
        this.sede = sede;
        this.stock = stock;
        this.isbn = isbn;
        this.editorial = editorial;
        this.edition = edition;
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }


    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getInfo(){
        return String.format("[%s] %s — format:%s, sede:%s, stock:%d", getPublicationDate().getYear(), getTitle(), format, sede, stock);
    }
}
