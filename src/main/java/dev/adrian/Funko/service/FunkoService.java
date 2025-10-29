package dev.adrian.Funko.service;

import dev.adrian.Funko.dto.CreateFunkoDTO;
import dev.adrian.Funko.dto.PatchFunkoDTO;
import dev.adrian.Funko.dto.UpdateFunkoDTO;
import dev.adrian.Funko.model.Funko;
import org.springframework.data.domain.Page; // Importante
import org.springframework.data.domain.Pageable; // Importante
import java.util.List;
import java.util.Optional;

public interface FunkoService {

    Optional<Funko> findById(Long id);

    List<Funko> findAll(); // Mantén este si lo usas en otros lugares

    // Nuevo método para paginación y filtrado
    Page<Funko> findAll(Pageable pageable, String nameFilter);

    Funko saveFromDTO(CreateFunkoDTO createFunkoDTO);

    Funko updateFromDTO(Long id, UpdateFunkoDTO updateFunkoDTO);

    Funko partialUpdateFromDTO(Long id, PatchFunkoDTO patchFunkoDTO);

    void deleteById(Long id);
}