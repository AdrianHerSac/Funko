package dev.adrian.Funko.service;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Categoria.repository.CategoriaRepository;
import dev.adrian.Funko.dto.CreateFunkoDTO;
import dev.adrian.Funko.dto.PatchFunkoDTO;
import dev.adrian.Funko.dto.UpdateFunkoDTO;
import dev.adrian.Funko.exception.ResourceNotFoundException;
import dev.adrian.Funko.mappers.FunkoMapper;
import dev.adrian.Funko.model.Funko;
import dev.adrian.Funko.repository.FunkoJpaRepository;
import dev.adrian.Funko.validation.FunkoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoServiceImpl implements FunkoService {

    private final FunkoJpaRepository funkoRepository;
    private final FunkoMapper funkoMapper;
    private final FunkoValidator funkoValidator;
    private final CategoriaRepository categoriaRepository;

    public FunkoServiceImpl(FunkoJpaRepository funkoRepository,
                            CategoriaRepository categoriaRepository,
                            FunkoMapper funkoMapper,
                            FunkoValidator funkoValidator) {
        this.funkoRepository = funkoRepository;
        this.categoriaRepository = categoriaRepository;
        this.funkoMapper = funkoMapper;
        this.funkoValidator = funkoValidator;
    }

    @Override
    public Optional<Funko> findById(Long id) {
        log.info("findById - Buscando Funko con ID: {}", id);

        Funko funko = funkoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("findById - Funko no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Funko no encontrado con ID: " + id);
                });

        log.info("findById - Funko encontrado: {}", funko.getNombre());
        return Optional.of(funko);
    }

    @Override
    public List<Funko> findAll() {
        log.info("findAll - Recuperando todos los Funkos...");
        List<Funko> funkos = funkoRepository.findAll();
        log.info("findAll - Total de Funkos encontrados: {}", funkos.size());
        return funkos;
    }

    @Override
    public Funko saveFromDTO(CreateFunkoDTO dto) {
        log.info("saveFromDTO - Creando Funko desde DTO: {}", dto.getNombre());

        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(dto.getCategoriaNombre())
                .orElseThrow(() -> {
                    log.warn("saveFromDTO - Categoría no encontrada: {}", dto.getCategoriaNombre());
                    return new ResourceNotFoundException("Categoría no encontrada con nombre: " + dto.getCategoriaNombre());
                });

        log.info("saveFromDTO - Categoría encontrada: {}", categoria.getNombre());

        Funko funko = funkoMapper.fromCreateDTO(dto, categoria);

        funkoValidator.validate(funko);
        log.info("saveFromDTO - Validación completada para Funko: {}", funko.getNombre());

        Funko saved = funkoRepository.save(funko);
        log.info("saveFromDTO - Funko guardado con ID: {}", saved.getId());

        return saved;
    }

    @Override
    public Funko updateFromDTO(Long id, UpdateFunkoDTO dto) {
        log.info("updateFromDTO - Actualizando Funko con ID: {}", id);

        Funko existing = funkoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("updateFromDTO - Funko no encontrado: {}", id);
                    return new ResourceNotFoundException("Funko con ID " + id + " no encontrado");
                });

        existing.setNombre(dto.getNombre());
        existing.setPrecio(dto.getPrecio());
        existing.setFechaLanzamiento(dto.getFechaLanzamiento());

        if (dto.getCategoria() != null && dto.getCategoria().getNombre() != null) {
            existing.getCategoria().setNombre(dto.getCategoria().getNombre());
            log.info("updateFromDTO - Categoría actualizada a: {}", dto.getCategoria().getNombre());
        }

        funkoValidator.validate(existing);
        log.info("updateFromDTO - Validación completada para Funko actualizado");

        Funko updated = funkoRepository.save(existing);
        log.info("updateFromDTO - Funko guardado correctamente con ID: {}", updated.getId());

        return updated;
    }

    @Override
    public Funko partialUpdateFromDTO(Long id, PatchFunkoDTO patchFunkoDTO) {
        log.info("partialUpdateFromDTO - Actualización parcial del Funko con ID: {}", id);

        Funko existingFunko = funkoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("partialUpdateFromDTO - Funko no encontrado con ID: {}", id);
                    return new RuntimeException("Funko con ID " + id + " no encontrado para actualización parcial.");
                });

        funkoMapper.fromPatchDTO(patchFunkoDTO, existingFunko);
        log.info("partialUpdateFromDTO - Campos aplicados desde Patch DTO");

        Funko updated = funkoRepository.save(existingFunko);
        log.info("partialUpdateFromDTO - Funko parcialmente actualizado con ID: {}", updated.getId());

        return updated;
    }

    @Override
    public void deleteById(Long id) {
        log.info("deleteById - Intentando eliminar Funko con ID: {}", id);

        Optional<Funko> funkoOpt = funkoRepository.findById(id);
        if (funkoOpt.isPresent()) {
            funkoRepository.deleteById(id);
            log.info("deleteById - Funko eliminado con éxito: {}", id);
        } else {
            log.warn("deleteById - No se encontró Funko con ID: {}", id);
        }
    }
}