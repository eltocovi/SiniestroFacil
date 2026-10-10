package cl.duoc.siniestrofacil.ms_poliza.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_poliza", nullable = false, unique = true, length = 20)
    private String numeroPoliza;

    @Column(name = "patente_vehiculo", nullable = false, length = 6)
    private String patenteVehiculo;

    @Column(name = "marca_vehiculo", nullable = false, length = 50)
    private String marcaVehiculo;

    @Column(name = "modelo_vehiculo", nullable = false, length = 50)
    private String modeloVehiculo;

    @Column(name = "anio_vehiculo", nullable = false)
    private Integer anioVehiculo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_termino", nullable = false)
    private LocalDate fechaTermino;

    @Column(name = "prima_mensual", nullable = false, precision = 12, scale = 2)
    private BigDecimal primaMensual;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deducible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPoliza estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asegurado_id", nullable = false)
    private Asegurado asegurado;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cobertura> coberturas = new ArrayList<>();

    public Poliza() {
    }

    public void agregarCobertura(Cobertura cobertura) {
        cobertura.setPoliza(this);
        coberturas.add(cobertura);
    }

    public EstadoPoliza estadoA(LocalDate fecha) {
        if (estado == EstadoPoliza.ACTIVA && fecha.isAfter(fechaTermino)) {
            return EstadoPoliza.VENCIDA;
        }
        return estado;
    }

    public boolean estaVigenteEn(LocalDate fecha) {
        return estado == EstadoPoliza.ACTIVA
                && !fecha.isBefore(fechaInicio)
                && !fecha.isAfter(fechaTermino);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPoliza() {
        return numeroPoliza;
    }

    public void setNumeroPoliza(String numeroPoliza) {
        this.numeroPoliza = numeroPoliza;
    }

    public String getPatenteVehiculo() {
        return patenteVehiculo;
    }

    public void setPatenteVehiculo(String patenteVehiculo) {
        this.patenteVehiculo = patenteVehiculo;
    }

    public String getMarcaVehiculo() {
        return marcaVehiculo;
    }

    public void setMarcaVehiculo(String marcaVehiculo) {
        this.marcaVehiculo = marcaVehiculo;
    }

    public String getModeloVehiculo() {
        return modeloVehiculo;
    }

    public void setModeloVehiculo(String modeloVehiculo) {
        this.modeloVehiculo = modeloVehiculo;
    }

    public Integer getAnioVehiculo() {
        return anioVehiculo;
    }

    public void setAnioVehiculo(Integer anioVehiculo) {
        this.anioVehiculo = anioVehiculo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(LocalDate fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public BigDecimal getPrimaMensual() {
        return primaMensual;
    }

    public void setPrimaMensual(BigDecimal primaMensual) {
        this.primaMensual = primaMensual;
    }

    public BigDecimal getDeducible() {
        return deducible;
    }

    public void setDeducible(BigDecimal deducible) {
        this.deducible = deducible;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public void setEstado(EstadoPoliza estado) {
        this.estado = estado;
    }

    public Asegurado getAsegurado() {
        return asegurado;
    }

    public void setAsegurado(Asegurado asegurado) {
        this.asegurado = asegurado;
    }

    public List<Cobertura> getCoberturas() {
        return coberturas;
    }

    public void setCoberturas(List<Cobertura> coberturas) {
        this.coberturas = coberturas;
    }
}
