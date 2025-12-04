package modelo;

import java.sql.Timestamp;

public class ReservaSala {

    
    private int reservaId;
    private int salaId;
    private int personaID;
    private Timestamp fechaInicio;
    private Timestamp fechaFin;
    private String estadoReserva;
    private int numeroPersonas;
    private Timestamp fechaRegistro;
    private FacilityResource recurso;


    
    public ReservaSala(int reservaId, int salaId, int personaID, Timestamp fechaInicio,
                       Timestamp fechaFin, String estadoReserva, int numeroPersonas, Timestamp fechaRegistro) {

        this.reservaId = reservaId;
        this.salaId = salaId;
        this.personaID = personaID;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estadoReserva = estadoReserva;
        this.numeroPersonas = numeroPersonas;
        this.fechaRegistro = fechaRegistro;
    }


    
    public ReservaSala(FacilityResource recurso, int personaID, Timestamp fechaInicio,
                       Timestamp fechaFin, int numeroPersonas) {

        this.recurso = recurso;
        this.salaId = recurso.getId(); 
        this.personaID = personaID;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroPersonas = numeroPersonas;

        this.estadoReserva = "Activo";
        this.fechaRegistro = new Timestamp(System.currentTimeMillis());
    }


    
    public int getReservaId() {
        return reservaId;
    }

    public int getSalaId() {
        return salaId;
    }

    public void setSalaId(int salaId) {
        this.salaId = salaId;
    }

    public int getPersonaID() {
        return personaID;
    }

    public void setPersonalId(int personaID) {
        this.personaID = personaID;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Timestamp fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Timestamp getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Timestamp fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public void setNumeroPersonas(int numeroPersonas) {
        this.numeroPersonas = numeroPersonas;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public FacilityResource getRecurso() {
        return recurso;
    }

    public void setRecurso(FacilityResource recurso) {
        this.recurso = recurso;
    }


    @Override
    public String toString() {
        return "ReservaSala { " +
                "reservaId=" + reservaId +
                ", salaId=" + salaId +
                ", personalId=" + personaID +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estadoReserva='" + estadoReserva + '\'' +
                ", numeroPersonas=" + numeroPersonas +
                ", fechaRegistro=" + fechaRegistro +
                ", recurso=" + (recurso != null ? recurso.getName() : "null") +
                " }";
    }
}
