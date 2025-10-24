package dev.adrian.Funko.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public class CreateFunkoDTO {

    @NotNull(message = "El uuid no puede ser nulo")
    private Long uuid;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio no puede ser nulo")
    @PositiveOrZero(message = "El precio no puede ser negativo ni nulo")
    private Double precio;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String categoriaNombre;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    private LocalDate fechaLanzamiento;

    // Getters y Setters
    public Long getUuid() { return uuid; }
    public void setUuid(Long uuid) { this.uuid = uuid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(LocalDate fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }
}
