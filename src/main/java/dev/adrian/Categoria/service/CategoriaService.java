package dev.adrian.Categoria.service;

import dev.adrian.Categoria.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {

    Optional<Categoria> findById(Long id);
    List<Categoria> findAll();
    Categoria save(Categoria categoria);

    Categoria findByNombre(String nombre);
}