package dev.adrian.Categoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Pattern(
            regexp = "ANIME|PELÍCULA|VIDEOJUEGO",
            message = "La categoría solo puede ser ANIME, PELÍCULA o VIDEOJUEGO"
    )
    @Column(nullable = false, unique = true)
    private String nombre;

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

}