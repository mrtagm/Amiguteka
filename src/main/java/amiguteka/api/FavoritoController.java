package amiguteka.api;

import java.util.List;
import java.util.concurrent.ExecutionException;

import amiguteka.modelo.FavoritoDTO;
import amiguteka.service.PatronService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import amiguteka.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import com.itextpdf.text.DocumentException;

import amiguteka.modelo.Patron;
import amiguteka.modelo.Favorito;
import amiguteka.modelo.Usuario;
import amiguteka.service.FavoritoService;
import amiguteka.converter.FavoritoConverter;

@Controller
@RequestMapping("/api/favorito")
public class FavoritoController {

	private final String HTML_FAVORITO_CREA = "crearFavorito";
	private final String HTML_FAVORITO_LIST = "listFavoritos";
	private final String HTML_FAVORITO_MODIFICA = "modificaFavorito";
	private final String API_AMIGURUMI_VER = "redirect:/api/amigurumi/ver/";
	private final String API_FAVORITO_CREA = "redirect:/api/favorito/crear";
	private final String API_FAVORITO_LIST = "redirect:/api/favorito/list";
	private final String API_FAVORITO_MODIFICA = "redirect:/api/favorito/modifica/";
	private final String API_USUARIO_MODIFICA = "redirect:/api/usuario/modifica";
	private final String API_LOGIN = "redirect:/api/login";

	@Autowired
	private PdfService pdfService;

	@Autowired
	private FavoritoService favoritoService;

	/**
	 * 
	 * @param amigurumi
	 * @param session
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@PostMapping("/crear")
	public String crearFavorito(@ModelAttribute Patron amigurumi, HttpSession session) throws ExecutionException, InterruptedException {
		// Aquí puedes hacer la lógica para guardar el favorito
		Usuario loggedInUser = (Usuario) session.getAttribute("loggedInUser");
		FavoritoDTO favorito = favoritoService.getFavoritoByIdUsuarioAndAmigurumi(loggedInUser.getId(), amigurumi.getId());
		if (favorito == null)
			favoritoService.nuevoFavorito(amigurumi.getId(), loggedInUser.getId());
		return API_AMIGURUMI_VER+amigurumi.getId(); // Redirige a la página de nuevo favorito
	}

//	@GetMapping("/crear")
//	public String crearFavorito(Model model) {
//		// Se pasa un objeto vacío de Favorito al formulario
//		model.addAttribute("favorito", new Favorito());
//		return HTML_FAVORITO_CREA; // Nombre de la plantilla de Thymeleaf
//	}

	
	/**
	 * 
	 * @param id
	 * @param redirectAttributes
	 * @return
	 * */
	@GetMapping("/delete/{id}")
	public String deleteFavorito(@PathVariable("id") String id, RedirectAttributes redirectAttributes, HttpSession session) {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		favoritoService.borrarFavorito(id); // Llama al servicio para borrar el favorito
		redirectAttributes.addFlashAttribute("message", "Favorito eliminado con éxito");
		return API_USUARIO_MODIFICA; // Redirige a la lista de favoritos
	}
	 /**
	 *
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */

	@PostMapping ("/delete")
	public String deleteFavorito2(@ModelAttribute("id") String id, RedirectAttributes redirectAttributes) {
		String amigurumiId = favoritoService.getFavoritoById(id).getAmigurumiId();
		favoritoService.borrarFavorito(id); // Llama al servicio para borrar el favorito
		redirectAttributes.addFlashAttribute("message", "Favorito eliminado con éxito");
		return API_AMIGURUMI_VER+amigurumiId; // Redirige a la página de nuevo favorito
	}

	/**
	 * 
	 * @param favorito
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@PostMapping("/modifica")
	public String modificaFavorito(@ModelAttribute Favorito favorito) throws ExecutionException, InterruptedException {
		// Aquí puedes hacer la lógica para guardar el favorito
		favoritoService.editarFavorito(favorito);
		return API_FAVORITO_LIST; // Redirige a la página de nuevo favorito
	}

	@GetMapping("/modifica/{id}")
	public String modificaFavorito(@PathVariable("id") String id, Model model, HttpSession session){
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		FavoritoDTO favoritoDTO = favoritoService.getFavoritoById(id);
		model.addAttribute("favorito", favoritoDTO);
		return HTML_FAVORITO_MODIFICA;
	}

	@GetMapping("/list")
	public String listFavoritosByUsuario(Model model, HttpSession session)  {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		Usuario usuario = (Usuario) session.getAttribute("loggedInUser");
		List<FavoritoDTO> favoritoList = favoritoService.getFavoritoByIdUsuario(usuario.getId());
		model.addAttribute("favoritoList", favoritoList);
		return HTML_FAVORITO_LIST;
	}

	@GetMapping("/list/all")
	public String listFavoritos(Model model, HttpSession session)
			throws ExecutionException, InterruptedException {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		List<Favorito> favoritoList = favoritoService.getFavoritos();
		model.addAttribute("favoritoList", favoritoList);
		return HTML_FAVORITO_LIST;
	}

	@GetMapping("/descargar/{id}")
	public void descargarFavoritoPdf(@PathVariable("id") String id, HttpServletResponse response) throws IOException, DocumentException {

		// Obtener el favorito por ID
		FavoritoDTO favoritoDTO = favoritoService.getFavoritoById(id); // Este método debe devolver un FavoritoDTO
		if (favoritoDTO != null) {
			// Llamar al servicio para generar el PDF
			pdfService.generarPdf(favoritoDTO, response);
		} else {
			// Si el patrón no se encuentra, podemos manejarlo (por ejemplo, redirigiendo a otra página)
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Favorito no encontrado");
		}
	}

	






}
