package amiguteka.converter;
import amiguteka.modelo.Patron;
import amiguteka.modelo.PatronDTO;
import amiguteka.modelo.Usuario;
import amiguteka.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class PatronConverter {

    @Autowired
    private UsuarioService usuarioService;

    public PatronDTO convertToDTO(Patron patron) {
        PatronDTO dto = new PatronDTO();
        dto.setId(patron.getId());
        dto.setDescripcion(patron.getDescripcion());
        dto.setEnlace(patron.getEnlace());
        dto.setMateriales(patron.getMateriales());
        dto.setName(patron.getName());
        dto.setAutor(patron.getAutor());
        dto.setImagenPortada(patron.getImagenPortada());


        // Obtener el nombre del patrón usando el servicio PatronService
        Usuario usuario = usuarioService.getUsuarioById(patron.getAutor());
        if (usuario != null) {
            dto.setAutorNombre(usuario.getName());
        }

        try {
            if (patron.getImagenPortada() != null){
                byte[] imageBytes  = Files.readAllBytes(Paths.get(patron.getImagenPortada()));
                String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
                dto.setImagenPortada64(encodedImage);
                }
        } catch (IOException e) {
            System.out.println("imagen no encontrada: "+patron.getImagenPortada());
        }

        return dto;
    }

    public List<PatronDTO> convertToDTO(List<Patron> patronList) {
        List<PatronDTO> patronDTOs = new ArrayList<>();
        for (Patron patron : patronList) {
            PatronDTO dto = this.convertToDTO(patron);
            patronDTOs.add(dto);
        }
        return patronDTOs;
    }
}