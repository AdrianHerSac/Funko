package dev.adrian.Funko.repository;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Categoria.repository.CategoriaRepository;
import dev.adrian.Funko.model.Funko;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
class FunkoRepositoryPriceTest {

    @Autowired
    private FunkoJpaRepository funkoRepository;

    @Autowired
    private CategoriaRepository CategoriaRepository;

    @Test
    void findAllFunkosWithPriceLessThan() {

        Categoria categoria = new Categoria("ANIME");
        categoria = CategoriaRepository.save(categoria);

        Funko f1 = new Funko();
        f1.setUuid(1L);
        f1.setNombre("Goku Ultra Instinto");
        f1.setPrecio(35.5);
        f1.setCategoria(categoria);
        f1.setFechaLanzamiento(LocalDate.of(2024, 1, 15));

        Funko f2 = new Funko();
        f2.setUuid(2L);
        f2.setNombre("Vegeta SSJ");
        f2.setPrecio(25.0);
        f2.setCategoria(categoria);
        f2.setFechaLanzamiento(LocalDate.of(2023, 10, 20));

        funkoRepository.save(f1);
        funkoRepository.save(f2);

        List<Funko> baratos = funkoRepository.findByPrecioLessThanEqual(30.0);

        assertThat(baratos).hasSize(1);
        Funko fBarato = baratos.get(0); // getFirst() no existe en List
        assertThat(fBarato.getNombre()).isEqualTo("Vegeta SSJ");
        assertThat(fBarato.getCategoria()).isNotNull();
        assertThat(fBarato.getCategoria().getNombre()).isEqualTo("ANIME");
    }
}