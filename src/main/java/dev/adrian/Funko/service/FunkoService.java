package dev.adrian.Funko.service;

import dev.adrian.Funko.dto.CreateFunkoDTO;
import dev.adrian.Funko.dto.PatchFunkoDTO;
import dev.adrian.Funko.dto.UpdateFunkoDTO;
import dev.adrian.Funko.model.Funko;
import java.util.List;
import java.util.Optional;

public interface FunkoService {

    Optional<Funko> findById(Long id);

    List<Funko> findAll();

    Funko saveFromDTO(CreateFunkoDTO createFunkoDTO); // Cambiado

    Funko updateFromDTO(Long id, UpdateFunkoDTO updateFunkoDTO); // Cambiado

    Funko partialUpdateFromDTO(Long id, PatchFunkoDTO patchFunkoDTO); // Cambiado

    void deleteById(Long id);
}