package cl.duoc.siniestrofacil.ms_talleres.exception;

import cl.duoc.siniestrofacil.ms_talleres.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> manejarResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        return construir(ex.getStatusCode(), ex.getReason(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return construir(HttpStatus.BAD_REQUEST, "La petición contiene datos inválidos", request, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es inválido o contiene valores no permitidos", request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String mensaje = "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue();
        return construir(HttpStatus.BAD_REQUEST, mensaje, request, null);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatusCode status, String mensaje,
                                                    HttpServletRequest request, Map<String, String> errores) {
        HttpStatus httpStatus = HttpStatus.valueOf(status.value());
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                errores);
        return ResponseEntity.status(httpStatus).body(body);
    }
}
