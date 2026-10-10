package cl.duoc.siniestrofacil.ms_poliza.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "coberturas")
public class Cobertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoCobertura tipo;

    @Column(name = "monto_maximo", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoMaximo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    public Cobertura() {
    }

    public Cobertura(TipoCobertura tipo, BigDecimal montoMaximo) {
        this.tipo = tipo;
        this.montoMaximo = montoMaximo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCobertura getTipo() {
        return tipo;
    }

    public void setTipo(TipoCobertura tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public void setPoliza(Poliza poliza) {
        this.poliza = poliza;
    }
}
