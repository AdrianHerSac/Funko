package dev.adrian.Funko.controller;

import dev.adrian.Funko.dto.CreateFunkoDTO;
import dev.adrian.Funko.dto.FunkoResponseDTO;
import dev.adrian.Funko.dto.PatchFunkoDTO;
import dev.adrian.Funko.dto.UpdateFunkoDTO;
import dev.adrian.Funko.mappers.FunkoMapper;
import dev.adrian.Funko.model.Funko;
import dev.adrian.Funko.service.FunkoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Importante
import org.springframework.data.domain.PageRequest; // Importante
import org.springframework.data.domain.Pageable; // Importante
import org.springframework.data.domain.Sort; // Importante
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funkos")
public class FunkoRestController {

    private final FunkoService funkoService;
    private final FunkoMapper funkoMapper;

    // Modificado para paginación, ordenación y filtrado
    @GetMapping(value = {"", "/"})
    public ResponseEntity<Page<FunkoResponseDTO>> getAllFunkos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name // Parámetro de filtrado
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(sortDir)) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Funko> funkoPage = funkoService.findAll(pageable, name);

        Page<FunkoResponseDTO> responsePage = funkoPage.map(funkoMapper::toResponseDTO);

        return ResponseEntity.ok(responsePage);
    }

    // ... Resto de métodos siguen igual ...

    @GetMapping("/{id}")
    public ResponseEntity<FunkoResponseDTO> getFunko(@PathVariable Long id) {
        return funkoService.findById(id)
                .map(funkoMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = {"", "/"})
    public ResponseEntity<FunkoResponseDTO> createFunko(@Valid @RequestBody CreateFunkoDTO dto) {
        Funko funko = funkoService.saveFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(funkoMapper.toResponseDTO(funko));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunkoResponseDTO> updateFunko(
            @PathVariable Long id,
            @RequestBody UpdateFunkoDTO dto) {
        Funko updated = funkoService.updateFromDTO(id, dto);
        return ResponseEntity.ok(funkoMapper.toResponseDTO(updated)); // Mapeamos a DTO
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FunkoResponseDTO> patchFunko(
            @PathVariable Long id,
            @Valid @RequestBody PatchFunkoDTO patchFunkoDTO) {
        try {
            Funko patchedFunko = funkoService.partialUpdateFromDTO(id, patchFunkoDTO);
            return ResponseEntity.ok(funkoMapper.toResponseDTO(patchedFunko)); // Mapeamos a DTO
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunko(@PathVariable Long id) {
        if (funkoService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        funkoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}