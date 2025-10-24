package dev.adrian.Funko.service;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Funko.dto.CreateFunkoDTO;
import dev.adrian.Funko.dto.PatchFunkoDTO;
import dev.adrian.Funko.dto.UpdateFunkoDTO;
import dev.adrian.Funko.mappers.FunkoMapper;
import dev.adrian.Funko.model.Funko;
import dev.adrian.Funko.repository.FunkoJpaRepository;
import dev.adrian.Funko.service.FunkoServiceImpl;
import dev.adrian.Funko.validation.FunkoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {

    @Mock
    private FunkoJpaRepository funkoRepository;

    @Mock
    private FunkoMapper funkoMapper;

    @Mock
    private FunkoValidator funkoValidator;

    @InjectMocks
    private FunkoServiceImpl funkoService;

    private Funko funkoExample;
    private CreateFunkoDTO createFunkoDTO;
    private UpdateFunkoDTO updateFunkoDTO;
    private PatchFunkoDTO patchFunkoDTO;

    @BeforeEach
    void setUp() {
        // Inicializa objetos de prueba
        funkoExample = new Funko();
        funkoExample.setId(1L);
        funkoExample.setUuid(1L);
        funkoExample.setNombre("Vegeta SSJ");
        funkoExample.setPrecio(25.99);
        funkoExample.setCategoria(new Categoria(null, "ANIME"));
        funkoExample.setFechaLanzamiento(LocalDate.of(2023, 10, 20));

        createFunkoDTO = new CreateFunkoDTO();
        createFunkoDTO.setUuid(1L);
        createFunkoDTO.setNombre("Goku Ultra Instinto");
        createFunkoDTO.setPrecio(35.50);
        createFunkoDTO.setCategoria(new Categoria(null, "ANIME"));
        createFunkoDTO.setFechaLanzamiento(LocalDate.of(2024, 1, 15));

        updateFunkoDTO = new UpdateFunkoDTO();
        updateFunkoDTO.setUuid(1L);
        updateFunkoDTO.setNombre("Goku Adulto");
        updateFunkoDTO.setPrecio(29.99);
        updateFunkoDTO.setCategoria(new Categoria(null, "ANIME"));
        updateFunkoDTO.setFechaLanzamiento(LocalDate.of(2024, 2, 1));

        patchFunkoDTO = new PatchFunkoDTO();
        patchFunkoDTO.setPrecio(32.50);
    }

    @Test
    void findById_ExistId() {
        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoExample));

        Optional<Funko> result = funkoService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Vegeta SSJ", result.get().getNombre());
    }

    @Test
    void findById_NoId_Empty() {
        when(funkoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Funko> result = funkoService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ReturnListOfFunkos() {
        List<Funko> funkosList = List.of(funkoExample);
        when(funkoRepository.findAll()).thenReturn(funkosList);

        List<Funko> result = funkoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Vegeta SSJ", result.getFirst().getNombre());  // GetFrist y no Get?
    }

    @Test
    void saveFromDTO_CreateFunko() {
        // Mock del mapper
        when(funkoMapper.fromCreateDTO(any(CreateFunkoDTO.class))).thenAnswer(invocation -> {
            CreateFunkoDTO dto = invocation.getArgument(0);
            Funko f = new Funko();
            f.setUuid(dto.getUuid());
            f.setNombre(dto.getNombre());
            f.setPrecio(dto.getPrecio());
            f.setCategoria(dto.getCategoria());
            f.setFechaLanzamiento(dto.getFechaLanzamiento());
            return f;
        });

        // Mock del validator
        doNothing().when(funkoValidator).validate(any(Funko.class));

        // Mock del repositorio (simulamos que asigna un ID)
        when(funkoRepository.save(any(Funko.class))).thenAnswer(invocation -> {
            Funko savedFunko = invocation.getArgument(0);
            savedFunko.setId(1L);
            return savedFunko;
        });

        Funko savedFunko = funkoService.saveFromDTO(createFunkoDTO);

        assertNotNull(savedFunko.getId());
        assertEquals("Goku Ultra Instinto", savedFunko.getNombre());
        assertEquals(35.50, savedFunko.getPrecio());
        verify(funkoRepository, times(1)).save(any(Funko.class));
    }

    @Test
    void updateFromDTO_Id() {
        Categoria categoria = new Categoria(null, "ANIME");

        when(funkoMapper.fromUpdateDTO(any(UpdateFunkoDTO.class), any(Funko.class)))
                .thenAnswer(invocation -> {
                    UpdateFunkoDTO dto = invocation.getArgument(0);
                    Funko funko = invocation.getArgument(1);
                    if (dto.getNombre() != null) funko.setNombre(dto.getNombre());
                    if (dto.getPrecio() != null) funko.setPrecio(dto.getPrecio());
                    if (dto.getCategoria() != null) funko.setCategoria(dto.getCategoria());
                    if (dto.getFechaLanzamiento() != null) funko.setFechaLanzamiento(dto.getFechaLanzamiento());
                    return null; // mapper es void
                });

        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoExample));
        when(funkoRepository.save(any(Funko.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        updateFunkoDTO.setCategoria(categoria);

        Funko updatedFunko = funkoService.updateFromDTO(1L, updateFunkoDTO);

        assertEquals("Goku Adulto", updatedFunko.getNombre());
        assertEquals(29.99, updatedFunko.getPrecio());
        assertNotNull(updatedFunko.getCategoria());
        assertEquals(new Categoria(null, "ANIME"), updatedFunko.getCategoria());
    }

    @Test
    void updateFromDTO_NoId() {
        when(funkoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> funkoService.updateFromDTO(999L, updateFunkoDTO));
    }

    @Test
    void partialUpdateFromDTO_Id() {
        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoExample));

        // Simulamos el comportamiento del mapper
        when(funkoMapper.fromPatchDTO(any(PatchFunkoDTO.class), any(Funko.class)))
                .thenAnswer(invocation -> {
                    PatchFunkoDTO dto = invocation.getArgument(0);
                    Funko funko = invocation.getArgument(1);
                    if (dto.getNombre() != null) funko.setNombre(dto.getNombre());
                    if (dto.getPrecio() != null) funko.setPrecio(dto.getPrecio());
                    if (dto.getCategoria() != null) funko.setCategoria(dto.getCategoria());
                    if (dto.getFechaLanzamiento() != null) funko.setFechaLanzamiento(dto.getFechaLanzamiento());
                    return funko;
                });

        when(funkoRepository.save(any(Funko.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Funko patchedFunko = funkoService.partialUpdateFromDTO(1L, patchFunkoDTO);

        assertEquals(32.50, patchedFunko.getPrecio());          // Precio actualizado
        assertEquals("Vegeta SSJ", patchedFunko.getNombre());   // Nombre sin cambios
        assertNotNull(patchedFunko.getCategoria());            // Categoria no nula
        assertEquals("ANIME", patchedFunko.getCategoria().getNombre()); // Igual que la original
    }


    @Test
    void partialUpdateFromDTO_NoId() {
        // Given
        when(funkoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> funkoService.partialUpdateFromDTO(999L, patchFunkoDTO));
    }

    @Test
    void deleteById_ExistingId_ShouldDelete() {
        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoExample)); // <-- Este stubbing SÍ es necesario si tu deleteById verifica antes de borrar

        funkoService.deleteById(1L);
        verify(funkoRepository, times(1)).deleteById(1L); // Esto es lo que quieres probar: que se llamó a deleteById
    }

    @Test
    void deleteById_NonExistingId_ShouldNotThrowButDoNothing() {
        when(funkoRepository.findById(999L)).thenReturn(Optional.empty());

        funkoService.deleteById(999L);

        verify(funkoRepository, times(1)).findById(999L);
        verify(funkoRepository, never()).deleteById(999L);
    }
}