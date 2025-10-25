package dev.adrian.Funko.mappers;

import dev.adrian.Categoria.model.Categoria;
import dev.adrian.Categoria.repository.CategoriaRepository;
import dev.adrian.Funko.dto.CreateFunkoDTO;
import dev.adrian.Funko.dto.UpdateFunkoDTO;
import dev.adrian.Funko.dto.PatchFunkoDTO;
import dev.adrian.Funko.model.Funko;
import dev.adrian.Funko.dto.FunkoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FunkoMapper {

    private final CategoriaRepository categoriaRepository;

    public FunkoMapper(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public FunkoResponseDTO toResponseDTO(Funko funko) {
        if (funko == null) return null;
        return FunkoResponseDTO.builder()
                .id(funko.getId())
                .uuid(funko.getUuid())
                .nombre(funko.getNombre())
                .precio(funko.getPrecio())
                .categoriaNombre(funko.getCategoria() != null ? funko.getCategoria().getNombre() : null)
                .fechaLanzamiento(funko.getFechaLanzamiento())
                .build();
    }

    public Funko fromCreateDTO(CreateFunkoDTO dto) {
        Funko funko = new Funko();
        funko.setUuid(dto.getUuid());
        funko.setNombre(dto.getNombre());
        funko.setPrecio(dto.getPrecio());
        funko.setFechaLanzamiento(dto.getFechaLanzamiento());

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getCategoriaNombre());
        funko.setCategoria(categoria);

        return funko;
    }

    public Funko fromUpdateDTO(UpdateFunkoDTO dto, Funko funko) {
        funko.setNombre(dto.getNombre());
        funko.setPrecio(dto.getPrecio());
        funko.setCategoria(dto.getCategoria());
        funko.setFechaLanzamiento(dto.getFechaLanzamiento());
        return funko;
    }

    public Funko fromPatchDTO(PatchFunkoDTO dto, Funko funko) {
        if (dto.getNombre() != null) funko.setNombre(dto.getNombre());
        if (dto.getPrecio() != null) funko.setPrecio(dto.getPrecio());
        if (dto.getCategoria() != null) funko.setCategoria(dto.getCategoria());
        if (dto.getFechaLanzamiento() != null) funko.setFechaLanzamiento(dto.getFechaLanzamiento());
        return funko;
    }
}