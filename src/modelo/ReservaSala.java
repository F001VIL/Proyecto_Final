package modelo;

import java.sql.Timestamp;

public class ReservaSala {

    private int reservaId;
    private int salaId;
    private int personaId;
    private Timestamp fechaInicio;
    private Timestamp fechaFin;
    private String estadoReserva;
    private int numeroPersonas; // opcional según BD

    // === Constructor usado en ReservaSalaDAO (6 parámetros) ===
    public ReservaSala(int reservaId, int salaId, int personaId,
                       Timestamp fechaInicio, Timestamp fechaFin,
                       String estadoReserva) {

        this.reservaId = reservaId;
        this.salaId = salaId;
        this.personaId = personaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estadoReserva = estadoReserva;
    }

    // === Constructor completo de la BD (7 parámetros) ===
    public ReservaSala(int reservaId, int salaId, int personaId,
                       Timestamp fechaInicio, Timestamp fechaFin,
                       String estadoReserva, int numeroPersonas) {

        this.reservaId = reservaId;
        this.salaId = salaId;
        this.personaId = personaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estadoReserva = estadoReserva;
        this.numeroPersonas = numeroPersonas;
    }

    // Getters
    public int getReservaId() { return reservaId; }
    public int getSalaId() { return salaId; }
    public int getPersonaId() { return personaId; }

    @Override
    public String toString() {
        return "ReservaSala #" + reservaId +
               " | Sala: " + salaId +
               " | Usuario: " + personaId +
               " | Inicio: " + fechaInicio +
               " | Fin: " + fechaFin +
               " | Estado: " + estadoReserva;
    }
}
