package dev.adrian.Funko.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class FunkoResponseDTO {
    Long id;
    Long uuid;
    String nombre;
    Double precio;
    String categoriaNombre;
    LocalDate fechaLanzamiento;
}