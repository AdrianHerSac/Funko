package dev.adrian.Funko.validation;

import dev.adrian.Funko.model.Funko;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class FunkoValidator {

    public void validate(Funko funko) {
        if (funko.getNombre() == null || funko.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
        }

        if (funko.getCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La categoría no puede estar vacía");
        }

        if (funko.getPrecio() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El precio no puede ser negativo ni nulo");
        }

        if (funko.getUuid() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El precio no puede ser negativo ni nulo");
        }

        if (funko.getFechaLanzamiento() == null || funko.getFechaLanzamiento()
                .isBefore(LocalDate.of(1900, 1, 1))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La fecha de lanzamiento no puede ser nula ni anterior a 1900-01-01");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}