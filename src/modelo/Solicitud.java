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
