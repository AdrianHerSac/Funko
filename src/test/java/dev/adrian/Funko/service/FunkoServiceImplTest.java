package dev.adrian.Funko.service;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Categoria.repository.CategoriaRepository; // ✅ Import necesario
import dev.adrian.Funko.dto.CreateFunkoDTO;
import dev.adrian.Funko.dto.PatchFunkoDTO;
import dev.adrian.Funko.dto.UpdateFunkoDTO;
import dev.adrian.Funko.exception.ResourceNotFoundException;
import dev.adrian.Funko.mappers.FunkoMapper;
import dev.adrian.Funko.model.Funko;
import dev.adrian.Funko.repository.FunkoJpaRepository;
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

/**
 * ✅ Test unitario del servicio FunkoServiceImpl
 * Usa Mockito para simular el repositorio, mapper y validador sin tocar la base de datos real.
 */
@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {

    // --- Dependencias simuladas (Mocks) ---
    @Mock private FunkoJpaRepository funkoRepository;
    @Mock private FunkoMapper funkoMapper;
    @Mock private FunkoValidator funkoValidator;
    @Mock private CategoriaRepository categoriaRepository;

    // --- Clase que se prueba ---
    @InjectMocks private FunkoServiceImpl funkoService;

    // --- Datos de prueba ---
    private Funko funkoExample;
    private CreateFunkoDTO createFunkoDTO;
    private UpdateFunkoDTO updateFunkoDTO;
    private PatchFunkoDTO patchFunkoDTO;

    @BeforeEach
    void setUp() {
        // Preparamos un Funko de ejemplo
        funkoExample = new Funko();
        funkoExample.setId(1L);
        funkoExample.setUuid(1L);
        funkoExample.setNombre("Vegeta SSJ");
        funkoExample.setPrecio(25.99);
        funkoExample.setCategoria(new Categoria(null, "ANIME"));
        funkoExample.setFechaLanzamiento(LocalDate.of(2023, 10, 20));

        // DTO para crear un Funko nuevo
        createFunkoDTO = new CreateFunkoDTO();
        createFunkoDTO.setUuid(1L);
        createFunkoDTO.setNombre("Goku Ultra Instinto");
        createFunkoDTO.setPrecio(35.50);
        createFunkoDTO.setCategoriaNombre("ANIME");
        createFunkoDTO.setFechaLanzamiento(LocalDate.of(2024, 1, 15));

        // DTO para actualización completa
        updateFunkoDTO = new UpdateFunkoDTO();
        updateFunkoDTO.setUuid(1L);
        updateFunkoDTO.setNombre("Goku Adulto");
        updateFunkoDTO.setPrecio(29.99);
        updateFunkoDTO.setCategoria(new Categoria(null, "ANIME"));
        updateFunkoDTO.setFechaLanzamiento(LocalDate.of(2024, 2, 1));

        // DTO para actualización parcial
        patchFunkoDTO = new PatchFunkoDTO();
        patchFunkoDTO.setPrecio(32.50);
    }

    // --- TESTS INDIVIDUALES ---

    @Test
    void findById_ExistId() {
        // Simulamos que el repositorio devuelve un Funko cuando se busca por ID
        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoExample));

        Optional<Funko> result = funkoService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Vegeta SSJ", result.get().getNombre());
    }

    @Test
    void findById_NoId() {
        // Simulamos que no existe un Funko con ese ID
        when(funkoRepository.findById(999L)).thenReturn(Optional.empty());

        // Esperamos que el servicio lance una excepción ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> funkoService.findById(999L));

        // Verificamos que el repositorio fue consultado
        verify(funkoRepository, times(1)).findById(999L);
    }


    @Test
    void findAll_ReturnListOfFunkos() {
        when(funkoRepository.findAll()).thenReturn(List.of(funkoExample));

        List<Funko> result = funkoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Vegeta SSJ", result.get(0).getNombre());
    }

    @Test
    void saveFromDTO_CreateFunko() {
        // ✅ Simulamos la categoría existente
        Categoria categoria = new Categoria(1L, "ANIME");
        when(categoriaRepository.findByNombreIgnoreCase("ANIME"))
                .thenReturn(Optional.of(categoria));

        // ✅ Simulamos el mapper
        when(funkoMapper.fromCreateDTO(any(CreateFunkoDTO.class))).thenAnswer(invocation -> {
            CreateFunkoDTO dto = invocation.getArgument(0);
            Funko f = new Funko();
            f.setUuid(dto.getUuid());
            f.setNombre(dto.getNombre());
            f.setPrecio(dto.getPrecio());
            f.setCategoria(categoria);
            f.setFechaLanzamiento(dto.getFechaLanzamiento());
            return f;
        });

        // ✅ El validador no hace nada (evitamos lógica real)
        doNothing().when(funkoValidator).validate(any(Funko.class));

        // ✅ Simulamos que la BD asigna un ID al guardar
        when(funkoRepository.save(any(Funko.class))).thenAnswer(invocation -> {
            Funko savedFunko = invocation.getArgument(0);
            savedFunko.setId(1L);
            return savedFunko;
        });

        // ✅ Ejecutamos el método real
        Funko savedFunko = funkoService.saveFromDTO(createFunkoDTO);

        // ✅ Verificaciones
        assertNotNull(savedFunko.getId());
        assertEquals("Goku Ultra Instinto", savedFunko.getNombre());
        assertEquals(35.50, savedFunko.getPrecio());
        assertEquals("ANIME", savedFunko.getCategoria().getNombre());

        verify(funkoRepository, times(1)).save(any(Funko.class));
    }

    @Test
    void updateFromDTO_Id() {
        Categoria categoria = new Categoria(null, "ANIME");

        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoExample));
        when(funkoRepository.save(any(Funko.class))).thenAnswer(invocation -> invocation.getArgument(0));

        updateFunkoDTO.setCategoria(categoria);

        Funko updatedFunko = funkoService.updateFromDTO(1L, updateFunkoDTO);

        assertEquals("Goku Adulto", updatedFunko.getNombre());
        assertEquals(29.99, updatedFunko.getPrecio());
        assertEquals("ANIME", updatedFunko.getCategoria().getNombre());
    }

    @Test
    void partialUpdateFromDTO_Id() {
        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoExample));

        when(funkoMapper.fromPatchDTO(any(PatchFunkoDTO.class), any(Funko.class)))
                .thenAnswer(invocation -> {
                    PatchFunkoDTO dto = invocation.getArgument(0);
                    Funko funko = invocation.getArgument(1);
                    if (dto.getPrecio() != null) funko.setPrecio(dto.getPrecio());
                    return funko;
                });

        when(funkoRepository.save(any(Funko.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Funko patchedFunko = funkoService.partialUpdateFromDTO(1L, patchFunkoDTO);

        assertEquals(32.50, patchedFunko.getPrecio());
        assertEquals("Vegeta SSJ", patchedFunko.getNombre());
    }

    @Test
    void deleteById_ExistingId_ShouldDelete() {
        when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoExample));

        funkoService.deleteById(1L);

        verify(funkoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_NonExistingId_ShouldNotThrowButDoNothing() {
        when(funkoRepository.findById(999L)).thenReturn(Optional.empty());

        funkoService.deleteById(999L);

        verify(funkoRepository, times(1)).findById(999L);
        verify(funkoRepository, never()).deleteById(999L);
    }
}