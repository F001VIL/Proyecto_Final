package modelo;

import java.time.LocalDate;

public class Journal extends Physical {

    private int volume;
    

    public Journal(int id, String title, String author, String language, LocalDate publicationDate, String format, String sede, int stock, String isbn, String editorial, String edition, int volume) {
        super(id, title, author, language ,publicationDate, format, sede, stock, isbn, editorial, edition);
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public String getCitacion(){
        return getAuthor() + ". " + getTitle() + ". (" + getPublicationDate().getYear() + ").";
    }
}
