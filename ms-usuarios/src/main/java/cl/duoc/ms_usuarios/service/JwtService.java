package cl.duoc.ms_usuarios.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cl.duoc.ms_usuarios.model.Usuario;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    public String generarToken(Usuario usuario) {
        SecretKey key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Instant ahora = Instant.now();

        String rol = usuario.getRol().getNombre();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("roles", List.of(rol))
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(3600)))
                .signWith(key)
                .compact();
    }
}