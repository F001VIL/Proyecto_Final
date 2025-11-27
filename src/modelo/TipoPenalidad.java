package modelo;

import java.math.BigDecimal;

public class TipoPenalidad {
    
    private int tipoPenalidadId;
    private String nombreTipo;
    private BigDecimal montoBase;

    public TipoPenalidad(int tipoPenalidadId, String nombreTipo, BigDecimal montoBase) {
        this.tipoPenalidadId = tipoPenalidadId;
        this.nombreTipo = nombreTipo;
        this.montoBase = montoBase;
    }

    public int getTipoPenalidadId() {
        return tipoPenalidadId;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public BigDecimal getMontoBase() {
        return montoBase;
    }

    @Override
    public String toString() {
        return nombreTipo + " (base: " + montoBase + ")";
    }
}
