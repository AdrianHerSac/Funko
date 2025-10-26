package dev.adrian.Funko.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoServiceImpl implements FunkoService {

    private final FunkoJpaRepository funkoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FunkoMapper funkoMapper;
    private final FunkoValidator funkoValidator;
    private final WebSocketService webSocketService;
    private final ObjectMapper mapper = new ObjectMapper();

    public FunkoServiceImpl(
            FunkoJpaRepository funkoRepository,
            CategoriaRepository categoriaRepository,
            FunkoMapper funkoMapper,
            FunkoValidator funkoValidator,
            WebSocketService webSocketService
    ) {
        this.funkoRepository = funkoRepository;
        this.categoriaRepository = categoriaRepository;
        this.funkoMapper = funkoMapper;
        this.funkoValidator = funkoValidator;
        this.webSocketService = webSocketService;
    }

    @Override
    public Optional<Funko> findById(Long id) {
        log.info("findById - Buscando Funko con ID: {}", id);
        Funko funko = funkoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funko no encontrado con ID: " + id));
        return Optional.of(funko);
    }

    @Override
    public List<Funko> findAll() {
        log.info("findAll - Recuperando todos los Funkos...");
        return funkoRepository.findAll();
    }

    @Override
    public Funko saveFromDTO(CreateFunkoDTO dto) {
        log.info("saveFromDTO - Creando Funko desde DTO: {}", dto.getNombre());

        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(dto.getCategoriaNombre())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + dto.getCategoriaNombre()));

        Funko funko = funkoMapper.fromCreateDTO(dto);
        funko.setCategoria(categoria);
        funkoValidator.validate(funko);

        Funko saved = funkoRepository.save(funko);
        log.info("saveFromDTO - Funko guardado con ID: {}", saved.getId());

        onChange("CREATE", saved);
        return saved;
    }

    @Override
    public Funko updateFromDTO(Long id, UpdateFunkoDTO dto) {
        log.info("updateFromDTO - Actualizando Funko con ID: {}", id);
        Funko existing = funkoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funko con ID " + id + " no encontrado"));

        existing.setNombre(dto.getNombre());
        existing.setPrecio(dto.getPrecio());
        existing.setFechaLanzamiento(dto.getFechaLanzamiento());

        if (dto.getCategoria() != null && dto.getCategoria().getNombre() != null) {
            existing.getCategoria().setNombre(dto.getCategoria().getNombre());
        }

        funkoValidator.validate(existing);
        Funko updated = funkoRepository.save(existing);
        log.info("updateFromDTO - Funko actualizado correctamente con ID: {}", updated.getId());

        onChange("UPDATE", updated);
        return updated;
    }

    @Override
    public Funko partialUpdateFromDTO(Long id, PatchFunkoDTO patchFunkoDTO) {
        log.info("partialUpdateFromDTO - Actualización parcial del Funko con ID: {}", id);
        Funko existing = funkoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funko con ID " + id + " no encontrado"));
        funkoMapper.fromPatchDTO(patchFunkoDTO, existing);
        Funko updated = funkoRepository.save(existing);
        onChange("UPDATE_PARTIAL", updated);
        return updated;
    }

    @Override
    public void deleteById(Long id) {
        log.info("deleteById - Intentando eliminar Funko con ID: {}", id);
        Optional<Funko> funkoOpt = funkoRepository.findById(id);
        if (funkoOpt.isPresent()) {
            funkoRepository.deleteById(id);
            onChange("DELETE", funkoOpt.get());
            log.info("deleteById - Funko eliminado con éxito: {}", id);
        } else {
            throw new ResourceNotFoundException("Funko no encontrado con ID: " + id);
        }
    }

    private void onChange(String tipo, Funko funko) {
        log.debug("WebSocket - Notificación tipo: {} para Funko: {}", tipo, funko.getNombre());
        try {
            var notificacion = new Notificacion<>(
                    "FUNKOS",
                    tipo,
                    funkoMapper.toResponseDTO(funko),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar mensaje por WebSocket", e);
                }
            }).start();

        } catch (JsonProcessingException e) {
            log.error("Error al convertir notificación a JSON", e);
        }
    }
}