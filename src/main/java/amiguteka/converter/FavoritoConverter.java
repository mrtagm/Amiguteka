package amiguteka.converter;
import amiguteka.modelo.Favorito;
import amiguteka.modelo.FavoritoDTO;
import amiguteka.modelo.Patron;
import amiguteka.modelo.PatronDTO;
import amiguteka.service.PatronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class FavoritoConverter {

    @Autowired
    private PatronService patronService;

    public FavoritoDTO convertToDTO(Favorito favorito) {
        FavoritoDTO dto = new FavoritoDTO();
        dto.setId(favorito.getId());
        dto.setAmigurumiId(favorito.getAmigurumiId());
        dto.setUsuarioId(favorito.getUsuarioId());
        dto.setEstado(favorito.getEstado());
        dto.setAnotaciones(favorito.getAnotaciones());
       

        // Obtener el nombre del patrón usando el servicio PatronService
        PatronDTO patron = patronService.getPatronById(favorito.getAmigurumiId());
        if (patron != null) {
            dto.setNombrePatron(patron.getName());
            dto.setDescripcion(patron.getDescripcion());
            dto.setNombreAutor(patron.getAutorNombre());
        }

        return dto;
    }

    public List<FavoritoDTO> convertToDTO(List<Favorito> favoritoList) {
        List<FavoritoDTO> favoritoDTOs = new ArrayList<>();
        for (Favorito favorito : favoritoList) {
            FavoritoDTO dto = this.convertToDTO(favorito);
            favoritoDTOs.add(dto);
        }
        favoritoDTOs.sort(Comparator.comparing(FavoritoDTO::getNombreAutor, Comparator.nullsLast(String::compareToIgnoreCase)));

        return favoritoDTOs;
    }
}