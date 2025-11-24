package modelo;

import java.time.LocalDate;

public class Journal extends Physical {

    private int volume;
    private String issn;
    private String language;

    public Journal(int id, String title, String author, LocalDate publicationDate, String format, String sede, int stock, int volume, String issn, String language) {
        super(id, title, author, publicationDate, format, sede, stock);
        this.volume = volume;
        this.issn = issn;
        this.language = language;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getCitacion(){
        return getAuthor() + ". " + getTitle() + ". (" + getPublicationDate().getYear() + ").";
    }
}
