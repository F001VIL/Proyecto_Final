package modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Penalidad {

    private int penalidadId;      
    private Integer prestamoId;   
    private int personaId;
    private int tipoPenalidadId;
    private BigDecimal monto;
    private Timestamp fechaAplicacion;
    private boolean pagada;
    private String observacion;


    public Penalidad(int penalidadId, Integer prestamoId, int personaId,int tipoPenalidadId, BigDecimal monto,Timestamp fechaAplicacion, boolean pagada,String observacion) {
        this.penalidadId = penalidadId;
        this.prestamoId = prestamoId;
        this.personaId = personaId;
        this.tipoPenalidadId = tipoPenalidadId;
        this.monto = monto;
        this.fechaAplicacion = fechaAplicacion;
        this.pagada = pagada;
        this.observacion = observacion;
    }

    public int getPenalidadId() {
        return penalidadId;
    }

    public Integer getPrestamoId() {
        return prestamoId;
    }

    public int getPersonaId() {
        return personaId;
    }

    public int getTipoPenalidadId() {
        return tipoPenalidadId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public Timestamp getFechaAplicacion() {
        return fechaAplicacion;
    }

    public boolean isPagada() {
        return pagada;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setPagada(boolean pagada) {
        this.pagada = pagada;
    }

    @Override
    public String toString() {
        return "Penalidad #" + penalidadId +
               " PersonaID=" + personaId +
               " Tipo=" + tipoPenalidadId +
               " Monto=" + monto +
               " Pagada=" + pagada;
    }
}
