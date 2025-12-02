package modelo;

import java.time.LocalDate;

public class Thesis extends Digital{

    private String country;
    private String university;
    private String degree;
    private String issn;


    public Thesis(int id, String title, String author, String language, LocalDate publicationDate, String format, int fileSize, String country, String university, String degree, String issn) {
        super(id, title, author, language ,publicationDate, format, fileSize);
        this.country = country;
        this.university = university;
        this.degree = degree;
        this.issn = issn;

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

    public String getIssn() {
        return issn;
    }
    public void setIssn(String issn) {
        this.issn = issn;
    }

    @Override
    public String getCitacion(){
        return getAuthor() + ". " + getTitle() + ". (" + getPublicationDate().getYear() + "). "
                + getUniversity() + ", " + getDegree() + ".";
    }
}
