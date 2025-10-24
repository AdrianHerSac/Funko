package dev.adrian.Categoria.service;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Categoria.repository.CategoriaRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"funkos"})
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;

    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return Optional.empty();
    }

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria findByNombre(String nombre) {
        return findByNombre(nombre);
    }
}
