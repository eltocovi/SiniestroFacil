package cl.duoc.siniestrofacil.ms_talleres.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table (name = "talleres")
public class Taller {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true)
    private Integer rut;
    @Column (name = "digito_verificador", nullable = false, length = 1)
    private String dv;

    @Column (nullable = false, length = 100)
    private String direccion;

    @Column (nullable = false, length = 100)
    private String comuna  ;

    @Column (nullable = false, length = 20)
    private String telefono;

    @Column (nullable = false, length = 100)
    private String email;

    @Column (name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @Column (nullable = false)
    private Boolean disponible;
    
    @OneToMany(mappedBy = "taller")
    private List<Asignacion> asignaciones = new ArrayList<>();


    public Taller() {
    }

    public Taller(String nombre, Integer rut, String dv, String direccion, String comuna,
                  String telefono, String email, Integer capacidadMaxima, Boolean disponible) {
        this.nombre = nombre;
        this.rut = rut;
        this.dv = dv;
        this.direccion = direccion;
        this.comuna = comuna;
        this.telefono = telefono;
        this.email = email;
        this.capacidadMaxima = capacidadMaxima;
        this.disponible = disponible;
    }

    public long contarAsignacionesActivas() {
        return asignaciones.stream().filter(a -> a.getEstado().esActiva()).count();
    }

    public int cuposLibres() {
        return (int) Math.max(0, capacidadMaxima - contarAsignacionesActivas());
    }

    /** Un taller puede recibir una denuncia si está disponible y le quedan cupos. */
    public boolean puedeRecibirAsignacion() {
        return Boolean.TRUE.equals(disponible) && cuposLibres() > 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getRut() {
        return rut;
    }

    public void setRut(Integer rut) {
        this.rut = rut;
    }

    public String getDv() {
        return dv;
    }

    public void setDv(String dv) {
        this.dv = dv;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public List<Asignacion> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<Asignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    

}
