package modelo;

import java.time.LocalDate;

public class Ebook extends Digital{

    private int pages;
    private String isbn;
    private String editorial;
    private String edition;
    

    public Ebook(int id, String title, String author, String language, LocalDate publicationDate, String format, int fileSize, int pages, String isbn, String editorial, String edition) {
        super(id, title, author, language ,publicationDate, format, fileSize);
        this.pages = pages;
        this.isbn = isbn;
        this.editorial = editorial;
        this.edition = edition;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
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

    @Override
    public String getCitacion() {
        return getAuthor() + ". " + getTitle() + ". (" + getPublicationDate().getYear() + "). "
                + "[" + getFormat() + "] "
                + "ISBN " + getIsbn() + ", " + getPages() + " pp." ;
    }
}
