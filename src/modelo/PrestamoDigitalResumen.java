package modelo;

import java.time.LocalDateTime;

public class PrestamoDigitalResumen {
    private int prestamoDigitalId;
    private int recursoId;
    private String titulo;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaVencimiento;

    public PrestamoDigitalResumen(int prestamoDigitalId, int recursoId, String titulo,
                                 LocalDateTime fechaPrestamo, LocalDateTime fechaVencimiento) {
        this.prestamoDigitalId = prestamoDigitalId;
        this.recursoId = recursoId;
        this.titulo = titulo;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getPrestamoDigitalId() {
        return prestamoDigitalId;
    }
    public int getRecursoId() {
        return recursoId;
    }
    public String getTitulo() {
        return titulo;
    }
    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }
    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

}
