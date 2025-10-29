package dev.adrian.Funko.repository;

import dev.adrian.Funko.model.Funko;
import org.springframework.data.domain.Page; // Importante
import org.springframework.data.domain.Pageable; // Importante
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunkoJpaRepository extends JpaRepository<Funko, Long> {

    List<Funko> findByNombreIgnoreCase(String nombre);

    List<Funko> findByPrecioLessThanEqual(Double precioMax);

    List<Funko> findByCategoriaId(Long id);

    Page<Funko> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
