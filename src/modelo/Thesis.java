package modelo;

import java.time.LocalDate;

public class Thesis extends Digital{

    private String country;
    private String university;
    private String degree;

    public Thesis(int id, String title, String author, LocalDate publicationDate, String format, int fileSize, String country, String university, String degree) {
        super(id, title, author, publicationDate, format, fileSize);
        this.country = country;
        this.university = university;
        this.degree = degree;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    @Override
    public String getCitacion(){
        return getAuthor() + ". " + getTitle() + ". (" + getPublicationDate().getYear() + "). "
                + getUniversity() + ", " + getDegree() + ".";
    }
}
