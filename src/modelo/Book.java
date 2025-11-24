package modelo;

import java.time.LocalDate;

public class Book extends Physical {
    private int pages;

    public Book(int id, String title, String author, LocalDate publicationDate, String format, String sede, int stock, int pages) {
        super(id, title, author, publicationDate, format, sede, stock);
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