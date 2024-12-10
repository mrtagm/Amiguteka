package amiguteka.api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/aviso-legal")
    public String avisoLegal() {
        return "aviso-legal"; // Nombre del archivo HTML sin la extensión
    }

    @GetMapping("/politica-cookies")
    public String politicaCookies() {
        return "politica-cookies"; // Nombre del archivo HTML sin la extensión
    }

    @GetMapping("/politica-privacidad")
    public String politicaPrivacidad() {
        return "politica-privacidad"; // Nombre del archivo HTML sin la extensión
    }
}
