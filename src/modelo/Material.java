package modelo;

import java.time.LocalDate;

public abstract class Material {

    private int id;
    private String title;
    private String author;
    private String language;
    private LocalDate publicationDate;

    public Material(int id, String title, String author, String language ,LocalDate publicationDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.language = language;
        this.publicationDate = publicationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public abstract String getCitacion();
}
