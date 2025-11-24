package modelo;

import java.time.LocalDate;

public class Ebook extends Digital{

    private int pages;
    private String isbn;
    private String language;

    public Ebook(int id, String title, String author, LocalDate publicationDate, String format, int fileSize, int pages, String isbn, String language) {
        super(id, title, author, publicationDate, format, fileSize);
        this.pages = pages;
        this.isbn = isbn;
        this.language = language;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getCitacion() {
        return getAuthor() + ". " + getTitle() + ". (" + getPublicationDate().getYear() + "). "
                + "[" + getFormat() + "] "
                + "ISBN " + getIsbn() + ", " + getPages() + " pp." ;
    }
}
