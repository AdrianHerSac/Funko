package dev.adrian.Funko.repository;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Funko.model.Funko;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
public class FunkoRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Autowired
    private FunkoJpaRepository funkoRepository;

    @Test
    void testFindByNombreIgnoreCase() {
        Funko f1 = new Funko();
        f1.setUuid(1L);
        f1.setNombre("Goku Ultra Instinto");
        f1.setPrecio(35.5);
        f1.setCategoria(new Categoria(null, "ANIME"));
        f1.setFechaLanzamiento(LocalDate.of(2024, 1, 15));

        funkoRepository.save(f1);

        List<Funko> resultados = funkoRepository.findByNombreIgnoreCase("goku ultra instinto");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados.getFirst().getNombre()).isEqualTo("Goku Ultra Instinto");
    }
}