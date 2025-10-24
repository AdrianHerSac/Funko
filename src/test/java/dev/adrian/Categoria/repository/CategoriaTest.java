package dev.adrian.Categoria.repository;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Funko.model.Funko;
import dev.adrian.Funko.repository.FunkoJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoriaTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private FunkoJpaRepository funkoRepository;

    @Test
    @DisplayName("Debe guardar y recuperar un Funko asociado a una categoría")
    void saveFunkoWithCategoria() {
        Categoria anime = new Categoria(null, "ANIME");
        categoriaRepository.save(anime);

        Funko funko = new Funko();
        funko.setUuid(1L);
        funko.setNombre("Goku Ultra Instinto");
        funko.setPrecio(39.99);
        funko.setCategoria(anime);
        funko.setFechaLanzamiento(LocalDate.of(2024, 5, 1));

        funkoRepository.save(funko);

        List<Funko> resultados = funkoRepository.findAll();

        assertThat(resultados).hasSize(1);
        Funko f = resultados.getFirst();

        assertThat(f.getNombre()).isEqualTo("Goku Ultra Instinto");
        assertThat(f.getCategoria()).isNotNull();
        assertThat(f.getCategoria().getNombre()).isEqualTo("ANIME");
    }

    @Test
    @DisplayName("Debe lanzar error si la categoría no es válida")
    void invalidCategoriaShouldFail() {
        Categoria invalida = new Categoria(null, "MÚSICA"); // ❌ No permitida

        // No se puede guardar porque viola el patrón de validación
        org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> categoriaRepository.saveAndFlush(invalida)
        );
    }
}