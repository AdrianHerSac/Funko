package dev.adrian.Funko.controller;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Categoria.service.CategoriaService;
import dev.adrian.Funko.dto.CreateFunkoDTO;
import dev.adrian.Funko.dto.FunkoResponseDTO;
import dev.adrian.Funko.dto.PatchFunkoDTO;
import dev.adrian.Funko.dto.UpdateFunkoDTO;
import dev.adrian.Funko.exception.ResourceNotFoundException;
import dev.adrian.Funko.mappers.FunkoMapper;
import dev.adrian.Funko.model.Funko;
import dev.adrian.Funko.service.FunkoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FunkoRestController.class)
public class FunkoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FunkoService funkoService;

    @MockBean
    private CategoriaService CategoriaService;

    @MockBean
    private FunkoMapper funkoMapper;

    private Funko funkoExample;
    private CreateFunkoDTO createFunkoDTO;
    private UpdateFunkoDTO updateFunkoDTO;
    private PatchFunkoDTO patchFunkoDTO;
    private FunkoResponseDTO funkoResponseExample;

    @BeforeEach
    void setUp() {
        funkoExample = new Funko();
        funkoExample.setId(1L);
        funkoExample.setNombre("Vegeta SSJ");
        funkoExample.setPrecio(25.99);
        funkoExample.setCategoria(new Categoria(null, "ANIME"));
        funkoExample.setFechaLanzamiento(LocalDate.of(2023, 10, 20));

        createFunkoDTO = new CreateFunkoDTO();
        createFunkoDTO.setNombre("Goku Ultra Instinto");
        createFunkoDTO.setPrecio(35.50);
        createFunkoDTO.setCategoriaNombre("PELICULA");
        createFunkoDTO.setFechaLanzamiento(LocalDate.of(2024, 1, 15));

        updateFunkoDTO = new UpdateFunkoDTO();
        updateFunkoDTO.setNombre("Goku Adulto");
        updateFunkoDTO.setPrecio(29.99);
        updateFunkoDTO.setCategoria(new Categoria(null, "VIDEOJUEGO"));
        updateFunkoDTO.setFechaLanzamiento(LocalDate.of(2024, 2, 1));

        patchFunkoDTO = new PatchFunkoDTO();
        patchFunkoDTO.setPrecio(32.50);

        funkoResponseExample = FunkoResponseDTO.builder()
                .id(1L)
                .nombre("Goku Adulto")
                .precio(29.99)
                .categoriaNombre("ANIME")
                .fechaLanzamiento(LocalDate.of(2024, 2, 1))
                .build(); // Importante: termina con build()
    }

    @TestConfiguration
    static class CacheConfig {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("funkosAll");
        }
    }

    @Test
    void getAllFunkos() throws Exception {
        when(funkoService.findAll()).thenReturn(List.of(funkoExample));

        mockMvc.perform(MockMvcRequestBuilders.get("/funkos"))
                .andExpect(status().isOk());
    }

    @Test
    void getFunkoById() throws Exception {
        when(funkoService.findById(1L)).thenReturn(Optional.of(funkoExample));
        when(funkoMapper.toResponseDTO(funkoExample)).thenReturn(funkoResponseExample);

        mockMvc.perform(MockMvcRequestBuilders.get("/funkos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getFunkoByIdNoId() throws Exception {
        when(funkoService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/funkos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFunkoValido() throws Exception {
        // Mock del servicio
        when(funkoService.saveFromDTO(any(CreateFunkoDTO.class))).thenReturn(funkoExample);

        // Mock del mapper usando el builder
        when(funkoMapper.toResponseDTO(any(Funko.class))).thenAnswer(invocation -> {
            Funko funko = invocation.getArgument(0);
            return FunkoResponseDTO.builder()
                    .id(funko.getId())
                    .uuid(funko.getUuid())
                    .nombre(funko.getNombre())
                    .precio(funko.getPrecio())
                    .categoriaNombre(funko.getCategoria().getNombre())
                    .fechaLanzamiento(funko.getFechaLanzamiento())
                    .build();
        });

        // MockMvc request
        mockMvc.perform(MockMvcRequestBuilders.post("/funkos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "uuid": 2,
                          "nombre": "Goku Ultra Instinto",
                          "precio": 35.5,
                          "categoriaNombre": "ANIME",
                          "fechaLanzamiento": "2024-01-15"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Vegeta SSJ"));
    }

    @Test
    void updateFunkoValido() throws Exception {
        when(funkoService.updateFromDTO(eq(1L), any(UpdateFunkoDTO.class))).thenReturn(funkoExample);

        mockMvc.perform(MockMvcRequestBuilders.put("/funkos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "uuid": 2,
                      "nombre": "Goku Adulto",
                      "precio": 29.99,
                      "categoria": { "nombre": "ANIME" },
                      "fechaLanzamiento": "2024-02-01"
                    }
                    """))
                .andExpect(status().isOk());
    }

    @Test
    void updateFunkoNoId() throws Exception {
        when(funkoService.updateFromDTO(eq(999L), any(UpdateFunkoDTO.class)))
                .thenThrow(new ResourceNotFoundException("Funko no encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.put("/funkos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "uuid": 2,
                      "nombre": "Goku Adulto",
                      "precio": 29.99,
                      "categoria": { "nombre": "ANIME" },
                      "fechaLanzamiento": "2024-02-01"
                    }
                    """))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchFunkoValido() throws Exception {
        when(funkoService.partialUpdateFromDTO(eq(1L), any(PatchFunkoDTO.class)))
                .thenAnswer(invocation -> {
                    PatchFunkoDTO dto = invocation.getArgument(1);
                    Funko existing = new Funko();
                    existing.setId(funkoExample.getId());
                    existing.setUuid(funkoExample.getUuid());
                    existing.setNombre(funkoExample.getNombre());
                    existing.setPrecio(funkoExample.getPrecio());
                    existing.setCategoria(funkoExample.getCategoria());
                    existing.setFechaLanzamiento(funkoExample.getFechaLanzamiento());

                    if (dto.getUuid() != null) existing.setUuid(dto.getUuid());
                    if (dto.getNombre() != null) existing.setNombre(dto.getNombre());
                    if (dto.getPrecio() != null) existing.setPrecio(dto.getPrecio());
                    if (dto.getCategoria() != null) existing.setCategoria(dto.getCategoria());
                    if (dto.getFechaLanzamiento() != null) existing.setFechaLanzamiento(dto.getFechaLanzamiento());

                    return existing;
                });

        mockMvc.perform(MockMvcRequestBuilders.patch("/funkos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "precio": 32.5
                }
                """))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.precio").value(32.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Vegeta SSJ"));
    }

    @Test
    void patchFunkoNoId() throws Exception {
        when(funkoService.partialUpdateFromDTO(eq(999L), any(PatchFunkoDTO.class)))
                .thenThrow(new RuntimeException("Funko no encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/funkos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "precio": 32.5
                    }
                    """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFunkoId() throws Exception {
        when(funkoService.findById(1L)).thenReturn(Optional.of(funkoExample));

        mockMvc.perform(MockMvcRequestBuilders.delete("/funkos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteFunkoNoId() throws Exception {
        when(funkoService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/funkos/999"))
                .andExpect(status().isNotFound());
    }
}