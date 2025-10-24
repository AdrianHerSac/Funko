package dev.adrian.Funko.repository;

import dev.adrian.Funko.model.Funko;
import java.util.List;
import java.util.Optional;

public interface FunkoRepository {
    Optional<Funko> findById(Long id);
    List<Funko> findAll();
    Funko save(Funko funko);
    Funko update(Funko funko);
    void deleteById(Long id);
    boolean existsById(Long id);
}