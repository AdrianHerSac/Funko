package dev.adrian.Funko.dto;

import dev.adrian.Categoria.model.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter @Getter
public class UpdateFunkoDTO {

    private Long uuid;

    @NotBlank(message = "El nombre no puede estar vacío si se proporciona")
    private String nombre;

    @PositiveOrZero(message = "El precio no puede ser negativo si se proporciona")
    private Double precio;
    private Categoria categoria;
    private LocalDate fechaLanzamiento;

    public UpdateFunkoDTO() {}

}