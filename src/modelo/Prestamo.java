package modelo;

import java.sql.Timestamp;

public class Prestamo {

    private int prestamoId;
    private int copiaId;
    private int personaId;
    private Timestamp fechaPrestamo;
    private Timestamp fechaVencimiento;
    private Timestamp fechaDevolucion;
    private String titulo; // agregado para mostrar título del recurso

    // === Constructor usado en RecursoDAO ===
    public Prestamo(int prestamoId, int copiaId, String titulo,
                    Timestamp fechaPrestamo, Timestamp fechaVencimiento) {
        this.prestamoId = prestamoId;
        this.copiaId = copiaId;
        this.titulo = titulo;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
    }

    // === Constructor completo según BD ===
    public Prestamo(int prestamoId, int copiaId, int personaId,
                    Timestamp fechaPrestamo, Timestamp fechaVencimiento,
                    Timestamp fechaDevolucion, String estado) {
        this.prestamoId = prestamoId;
        this.copiaId = copiaId;
        this.personaId = personaId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaDevolucion = fechaDevolucion;
    }

    // Getters y Setters
    public int getPrestamoId() { return prestamoId; }
    public int getCopiaId() { return copiaId; }
    public int getPersonaId() { return personaId; }
    public String getTitulo() { return titulo; }

    @Override
    public String toString() {
        return "Prestamo #" + prestamoId +
               " | Copia: " + copiaId +
               " | Recurso: " + titulo +
               " | Prestado: " + fechaPrestamo +
               " | Vence: " + fechaVencimiento;
    }
}
