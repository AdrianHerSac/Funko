package dev.adrian.Funko.model;

import dev.adrian.Categoria.model.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "funkos")
public class Funko {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "El uuid no puede ser nulo")
    @Column(name = "uuid", nullable = false, unique = true)
    private long uuid;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(name = "precio", nullable = false)
    private double precio;

    @NotNull(message = "La categoría no puede ser nula")
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @NotNull(message = "La fecha de lanzamiento no puede ser nula")
    @Column(name = "fecha_lanzamiento", nullable = false)
    private LocalDate fechaLanzamiento;
}