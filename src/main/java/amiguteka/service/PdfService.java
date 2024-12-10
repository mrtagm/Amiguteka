package amiguteka.service;

import amiguteka.modelo.FavoritoDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class PdfService {
    public void generarPdf(FavoritoDTO favoritoDTO, HttpServletResponse response) throws DocumentException, IOException {
        // Definir el tipo de contenido como PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename="+ favoritoDTO.getNombrePatron() +".pdf");

        // Crear un nuevo documento PDF
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Definir fuentes para título y texto
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, new BaseColor(148, 0, 211)); // Lila
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD); // Negrita para etiquetas
        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL); // Normal para texto

        // Agregar título centrado
        Paragraph title = new Paragraph(favoritoDTO.getNombrePatron(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Agregar "Estado"
        document.add(new Paragraph("Estado: ", labelFont));
        document.add(new Paragraph(favoritoDTO.getEstado() + "\n", bodyFont));

        // Agregar "Anotaciones"
        document.add(new Paragraph("Anotaciones: ", labelFont));
        document.add(new Paragraph(favoritoDTO.getAnotaciones() + "\n", bodyFont));

        // Agregar "Descripción"
        document.add(new Paragraph("Descripción: ", labelFont));
        document.add(new Paragraph(favoritoDTO.getDescripcion() + "\n", bodyFont));

        // Cerrar el documento PDF
        document.close();
    }
}
