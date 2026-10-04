package cl.duoc.ms_usuarios.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import cl.duoc.ms_usuarios.model.Rol;
import cl.duoc.ms_usuarios.repository.RolRepository;

@Service 
public class RolService {
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository){
        this.rolRepository = rolRepository;
    }

    public List<Rol> obtenerRoles() {
        return rolRepository.findAll();
    }

    public Optional<Rol> obtenerPorId(Long idRol) {
        return rolRepository.findById(idRol);
    }

    public Rol crearRol(Rol rol) {
        return rolRepository.save(rol);
    }


    public Rol modificarRol(Rol rol, Long idRol) {
        Rol rolExistente = rolRepository.findById(idRol)
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        rolExistente.setNombre(rol.getNombre());

        return rolRepository.save(rolExistente);
    }

    public void eliminarRol(Long idRol){
        this.rolRepository.deleteById(idRol);
    }
    
}