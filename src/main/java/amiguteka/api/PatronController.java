package amiguteka.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import amiguteka.modelo.PatronDTO;
import amiguteka.service.FavoritoService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import amiguteka.modelo.Patron;
import amiguteka.modelo.Usuario;
import amiguteka.service.PatronService;
import amiguteka.modelo.FavoritoDTO;

@Controller
@RequestMapping("/api/amigurumi")
public class PatronController {

	private final String HTML_PATRONES_PROPIOS_LIST ="patronesPropiosList";
	private final String HTML_AMIGURUMI_CREA = "crearAmigurumi";
	private final String HTML_AMIGURUMI_LIST = "listAmigurumis";
	private final String HTML_AMIGURUMI_MODIFICA = "modificaAmigurumi";
	private final String HTML_AMIGURUMI_VER = "verAmigurumi";
	private final String API_AMIGURUMI_CREA = "redirect:/api/amigurumi/crear";
	private final String API_AMIGURUMI_LIST = "redirect:/api/amigurumi/list";
	private final String API_AMIGURUMI_MODIFICA = "redirect:/api/amigurumi/modifica/";
	private final String API_USUARIO_MODIFICA = "redirect:/api/usuario/modifica";
	private final String API_LOGIN = "redirect:/api/login";
	private final String API_PATRONES_PROPIOS_LIST = "redirect:/api/usuario/patrones-propios";


    @Autowired
    private PatronService patronService;

	@Autowired
	private FavoritoService favoritoService;

	/**
	 * 
	 * @param amigurumi
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@PostMapping("/crear")
	public String crearAmigurumi(@ModelAttribute Patron amigurumi, @RequestParam("image") MultipartFile file, HttpSession session) throws ExecutionException, InterruptedException, IOException {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		// Aquí puedes hacer la lógica para guardar el amigurumi
		Usuario loggedInUser = (Usuario) session.getAttribute("loggedInUser");
		amigurumi.setAutor(loggedInUser.getId());

		patronService.nuevoPatron(amigurumi);
		amigurumi.setImagenPortada(uploadImage(file, amigurumi.getId()));
		patronService.editarPatron(amigurumi);

		return API_PATRONES_PROPIOS_LIST; // Redirige a la página de nuevo amigurumi
	}

	public String uploadImage(MultipartFile file,String patronID) throws IOException {

		byte[] bytes = file.getBytes();
		Path path = Paths.get("\\home\\martagm\\Imágenes\\amiguteka\\"+  patronID + "." +obtenerExtension(file.getOriginalFilename()));
		Files.write(path, bytes);


		System.out.println(path.toString());
		return path.toString();
	}
	public String obtenerExtension(String nombreArchivo) {
		if (nombreArchivo == null || !nombreArchivo.contains(".")) {
			return ""; // O lanza una excepción si prefieres
		}
		return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
	}
	/**
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/crear")
	public String crearAmigurumi(Model model , HttpSession session) {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		// Se pasa un objeto vacío de Amigurumi al formulario
		model.addAttribute("amigurumi", new Patron());
		return HTML_AMIGURUMI_CREA; // Nombre de la plantilla de Thymeleaf
	}

	/**
	 * 
	 * @param model
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@GetMapping("/list")
	public String listAmigurumis(Model model,HttpSession session) throws ExecutionException, InterruptedException {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;

		List<PatronDTO> amigurumiList = patronService.getAllPatrones();
		model.addAttribute("amigurumiList", amigurumiList); // Pasa la lista al modelo
		return HTML_AMIGURUMI_LIST; // Nombre de la plantilla Thymeleaf
	}

	// Método para borrar un amigurumi por su ID
	@GetMapping("/delete/{id}")
	public String deleteAmigurumi(@PathVariable("id") String id, RedirectAttributes redirectAttributes, HttpSession session) {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;

		favoritoService.borrarFavoritoPatron(id);
		patronService.borrarPatron(id); // Llama al servicio para borrar el amigurumi
		redirectAttributes.addFlashAttribute("message", "Amigurumi eliminado con éxito");
		Usuario usuario = (Usuario) session.getAttribute("loggedInUser");
		if(usuario.isAdmin())
			return API_AMIGURUMI_LIST;
		return API_PATRONES_PROPIOS_LIST;
	}

	
	/**
	 * 
	 * @param patron
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@PostMapping("/modifica")
	public String modificaPatron(@ModelAttribute Patron patron)
			throws ExecutionException, InterruptedException {
		// Aquí puedes hacer la lógica para guardar el amigurumi
		patronService.editarPatron(patron);
		return API_PATRONES_PROPIOS_LIST; // Redirige a la página de nuevo amigurumi
	}

	/**
	 * 
	 * @param id
	 * @param model
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@GetMapping("/modifica/{id}")
	public String modificaAmigurumi(@PathVariable("id") String id, Model model, HttpSession session)
			throws ExecutionException, InterruptedException {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;

		PatronDTO amigurumi = patronService.getPatronById(id); // Obtiene el amigurumi por ID
		model.addAttribute("amigurumi", amigurumi);
		return HTML_AMIGURUMI_MODIFICA; // Nombre de la plantilla de Thymeleaf
	}

	
	/**
	 * 
	 * @param model
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@GetMapping("/ver/{id}")
	public String verAmigurumi(@PathVariable("id") String id, Model model,HttpSession session)  {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		//
		PatronDTO amigurumi = patronService.getPatronById(id);
		model.addAttribute("amigurumi", amigurumi);
		//
		Usuario loggedInUser = (Usuario) session.getAttribute("loggedInUser");
		FavoritoDTO favorito = favoritoService.getFavoritoByIdUsuarioAndAmigurumi(loggedInUser.getId(),amigurumi.getId());
		if (favorito != null) {
			model.addAttribute("favorito", favorito);
		}
		long contadorFavoritos = favoritoService.contarFavoritos(amigurumi.getId());
		model.addAttribute("contadorFavoritos", contadorFavoritos);

		model.addAttribute("admin", ((Usuario) session.getAttribute("loggedInUser")) .isAdmin());
		//
		return HTML_AMIGURUMI_VER; 
	}

}
