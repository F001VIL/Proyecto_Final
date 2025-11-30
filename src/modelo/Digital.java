package modelo;

import java.time.LocalDate;

public abstract class Digital extends Material {
    private String format;
    private int fileSize;
    private String urlAcceso;
    private String licencia;
    private int usuariosConcurrentes;

    public Digital(int id, String title, String author, String language, LocalDate publicationDate, String format, int fileSize) {
        super(id, title, author, language ,publicationDate);
        this.format = format;
        this.fileSize = fileSize;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getUrlAcceso() {
    return urlAcceso;
    }

    public void setUrlAcceso(String urlAcceso) {
        this.urlAcceso = urlAcceso;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public int getUsuariosConcurrentes() {
        return usuariosConcurrentes;
    }

    public void setUsuariosConcurrentes(int usuariosConcurrentes) {
        this.usuariosConcurrentes = usuariosConcurrentes;
    }

    public String getInfo(){
        return getTitle() + " [" + format + "] - (" + fileSize + " MB)";
    }
}
