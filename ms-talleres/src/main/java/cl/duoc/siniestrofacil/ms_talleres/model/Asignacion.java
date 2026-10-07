package cl.duoc.siniestrofacil.ms_talleres.model;

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

import java.time.LocalDateTime;
@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "folio_denuncia", nullable = false, length = 20)
    private String folioDenuncia;

    @Column(name = "patente_vehiculo", nullable = false, length = 6)
    private String patenteVehiculo;

    @Column(name = "descripcion_danos", nullable = false, length = 1000)
    private String descripcionDanos;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name = "observaciones_recepcion", length = 500)
    private String observacionesRecepcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAsignacion estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "taller_id", nullable = false)
    private Taller taller;

    public Asignacion() {
    }

    public Asignacion(String folioDenuncia, String patenteVehiculo, String descripcionDanos,
                      LocalDateTime fechaAsignacion, EstadoAsignacion estado, Taller taller) {
        this.folioDenuncia = folioDenuncia;
        this.patenteVehiculo = patenteVehiculo;
        this.descripcionDanos = descripcionDanos;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
        this.taller = taller;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFolioDenuncia() {
        return folioDenuncia;
    }

    public void setFolioDenuncia(String folioDenuncia) {
        this.folioDenuncia = folioDenuncia;
    }

    public String getPatenteVehiculo() {
        return patenteVehiculo;
    }

    public void setPatenteVehiculo(String patenteVehiculo) {
        this.patenteVehiculo = patenteVehiculo;
    }

    public String getDescripcionDanos() {
        return descripcionDanos;
    }

    public void setDescripcionDanos(String descripcionDanos) {
        this.descripcionDanos = descripcionDanos;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getObservacionesRecepcion() {
        return observacionesRecepcion;
    }

    public void setObservacionesRecepcion(String observacionesRecepcion) {
        this.observacionesRecepcion = observacionesRecepcion;
    }

    public EstadoAsignacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsignacion estado) {
        this.estado = estado;
    }

    public Taller getTaller() {
        return taller;
    }

    public void setTaller(Taller taller) {
        this.taller = taller;
    }
}
