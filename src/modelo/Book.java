package modelo;

import java.time.LocalDate;

public class Book extends Physical {
    private int pages;

    public Book(int id, String title, String author, String language, LocalDate publicationDate, String format, String sede, int stock, String isbn, String editorial, String edition, int pages) {
        super(id, title, author, language ,publicationDate, format, sede, stock, isbn, editorial, edition);
        this.pages = pages;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String getCitacion(){
        return getAuthor() + ". " + getTitle() + ". (" + getPublicationDate().getYear() + ").";
    }
}