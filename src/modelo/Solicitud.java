package modelo;

import java.util.Date;

public class Solicitud {

    private int id;
    private int usuarioId;
    private String tipoMaterial;
    private int materialId;
    private String estado;
    private Date fechaSolicitud;

    public Solicitud(int id, int usuarioId, String tipoMaterial,
                     int materialId, String estado, Date fechaSolicitud) {

        this.id = id;
        this.usuarioId = usuarioId;
        this.tipoMaterial = tipoMaterial;
        this.materialId = materialId;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public int getMaterialId() {
        return materialId;
    }

    public String getEstado() {
        return estado;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Solicitud{" +
                "ID=" + id +
                ", Usuario=" + usuarioId +
                ", Tipo='" + tipoMaterial + '\'' +
                ", MaterialID=" + materialId +
                ", Estado='" + estado + '\'' +
                ", Fecha=" + fechaSolicitud +
                '}';
    }
}
