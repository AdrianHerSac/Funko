package dev.adrian.Categoria.model;

import dev.adrian.Categoria.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackages = {"dev.adrian"})
@Configuration
public class CategoriaInitializer {

    @Bean
    CommandLineRunner initCategories(CategoriaRepository categoriaRepository) {
        return args -> {
            if (categoriaRepository.findByNombreIgnoreCase("ANIME").isEmpty()) {
                categoriaRepository.save(new Categoria("ANIME"));
            }
            if (categoriaRepository.findByNombreIgnoreCase("PELÍCULA").isEmpty()) {
                categoriaRepository.save(new Categoria("PELÍCULA"));
            }
            if (categoriaRepository.findByNombreIgnoreCase("VIDEOJUEGO").isEmpty()) {
                categoriaRepository.save(new Categoria("VIDEOJUEGO"));
            }
        };
    }
}