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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funkos")
public class FunkoRestController {

    private final FunkoService funkoService;

    private final FunkoMapper funkoMapper;

    @GetMapping(value = {"", "/"})
    public ResponseEntity<List<FunkoResponseDTO>> getAllFunkos() {
        return ResponseEntity.ok(
                funkoService.findAll().stream()
                        .map(funkoMapper::toResponseDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunkoResponseDTO> getFunko(@PathVariable Long id) {
        return funkoService.findById(id)
                .map(funkoMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PostMapping para el post, el Put no funciona ☻
    @PostMapping(value = {"", "/"})
    public ResponseEntity<FunkoResponseDTO> createFunko(@Valid @RequestBody CreateFunkoDTO dto) {
        Funko funko = funkoService.saveFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(funkoMapper.toResponseDTO(funko));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Funko> updateFunko(
            @PathVariable Long id,
            @RequestBody UpdateFunkoDTO dto) {

        Funko updated = funkoService.updateFromDTO(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Funko> patchFunko(
            @PathVariable Long id,
            @Valid @RequestBody PatchFunkoDTO patchFunkoDTO) {

        try {
            Funko patchedFunko = funkoService.partialUpdateFromDTO(id, patchFunkoDTO);
            return ResponseEntity.ok(patchedFunko);
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