package modelo;

import java.time.LocalDate;

public class Video extends Digital {
    private int minutes;
    private String resolution;

    public Video(int id, String title, String author, LocalDate publicationDate, String format, int fileSize, int minutes, String resolution) {
        super(id, title, author, publicationDate, format, fileSize);
        this.minutes = minutes;
        this.resolution = resolution;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public String getCitacion() {
        return getAuthor() + ". " + getTitle() + ". (" + getPublicationDate().getYear() + "). "
                + getResolution() + ", " + getMinutes() + " min.";
    }
}
