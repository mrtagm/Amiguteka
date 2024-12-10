package amiguteka.api;

import java.util.List;
import java.util.concurrent.ExecutionException;

import amiguteka.modelo.*;
import amiguteka.service.EmailService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import amiguteka.service.PatronService;
import amiguteka.service.FavoritoService;
import amiguteka.service.UsuarioService;

@Controller
@RequestMapping("/api/usuario")
public class UsuarioController {


	private final String HTML_USUARIO_CONFIRMAR = "confirmarUsuario";
	private final String HTML_USUARIO_CREA = "crearUsuario";
	private final String HTML_USUARIO_LIST = "listUsuarios";
	private final String HTML_USUARIO_MODIFICA = "modificaUsuario";
	private final String API_USUARIO_CREA = "redirect:/api/usuario/crear";
	private final String API_USUARIO_MODIFICA = "redirect:/api/usuario/modifica";
	private final String API_LOGIN = "redirect:/api/login";
	private final String HTML_PATRON_LIST = "patronesPropiosList";
	private final String HTML_CAMBIO_CONTRASENA = "cambiarContrasena";
	private final String API_CAMBIO_CONTRASENA = "redirect:/api/usuario/cambiarContrasena";
	private final String HTML_ADMIN_MODIFICA = "modificaAdmin";
	private final String HTML_ADMIN_MODIFICA_PATRON="modificaPatronAdmin";

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private FavoritoService favoritoService;
	

	@Autowired
	private PatronService patronService;
    @Autowired
    private EmailService emailService;

	/**
	 *
	 * @param usuario
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@PostMapping("/crear")
	public String crearUsuario(@ModelAttribute Usuario usuario,  RedirectAttributes redirectAttributes)  throws ExecutionException, InterruptedException {

		if (usuarioService.getUsuarioByName(usuario.getName())== null && usuarioService.getUsuarioByEmail(usuario.getEmail())==null){

			usuarioService.nuevoUsuario(usuario);
			emailService.enviarCorreoVerificacionCrearMime(usuario.getEmail(),usuario.getId());
			redirectAttributes.addFlashAttribute("messageInfo", "Registro realizado con éxito. Para finalizar valida el email.");
			return API_LOGIN;
		}else{
			redirectAttributes.addFlashAttribute("message", "Usuario ya existe. Pruebe otro nombre o email.");
			return API_USUARIO_CREA;
		}
	}

	/**
	 *
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@GetMapping("/crear/confirmar/{usuarioId}")
	public String confirmarUsuario(@PathVariable String usuarioId,  RedirectAttributes redirectAttributes)  throws ExecutionException, InterruptedException {
		Usuario  usuario = usuarioService.getUsuarioById(usuarioId);
		if (usuario == null){
			return API_USUARIO_CREA;
		}else{
			usuarioService.confirmarUsuario(usuarioId);
			redirectAttributes.addFlashAttribute("messageInfo", "¡Validación completada, ya puedes iniciar sesión!");
			return API_LOGIN;
		}
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/crear")
	public String crearUsuario(Model model) {
		// Se pasa un objeto vacío de Usuario al formulario
		model.addAttribute("usuario", new Usuario());
		return HTML_USUARIO_CREA; // Nombre de la plantilla de Thymeleaf
	}

	@GetMapping("/list")
	public String listUsuarios(Model model, HttpSession session) throws ExecutionException, InterruptedException {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		List<Usuario> usuarioList = usuarioService.getAllUsuarios();
		model.addAttribute("usuarioList", usuarioList); // Pasa la lista al modelo
		return HTML_USUARIO_LIST; // Nombre de la plantilla Thymeleaf
	}

	// Método para borrar un usuario por su ID
	@GetMapping("/delete/{id}")
	public String deleteUsuario(@PathVariable("id") String id, RedirectAttributes redirectAttributes, HttpSession session) {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		for (Patron patron :patronService.getPatronByAutor(id)) {
			favoritoService.borrarFavoritoPatron(patron.getId());
			patronService.borrarPatron(patron.getId());
		}
		for (FavoritoDTO favorito :favoritoService.getFavoritoByIdUsuario(id))
			favoritoService.borrarFavorito(favorito.getId());

		usuarioService.borrarUsuario(id); // Llama al servicio para borrar el usuario
		redirectAttributes.addFlashAttribute("message", "Usuario eliminado con éxito");

		Usuario loggedInUser = (Usuario) session.getAttribute("loggedInUser");
		if (loggedInUser.isAdmin())
			return API_USUARIO_MODIFICA;
		return API_LOGIN;
	}

	/**
	 * 
	 * @param usuario
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@PostMapping("/modifica")
	public String modificaUsuario(@ModelAttribute Usuario usuario) throws ExecutionException, InterruptedException {
		// Aquí puedes hacer la lógica para guardar el usuario
		usuarioService.editarUsuario(usuario);
		return API_USUARIO_MODIFICA; // Redirige a la página de nuevo usuario
	}

	/**
	 * 
	 * @param model
	 * @param session
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@GetMapping("/modifica")
	public String modificaUsuario( Model model, HttpSession session)
			throws ExecutionException, InterruptedException {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		Usuario loggedInUser = (Usuario) session.getAttribute("loggedInUser");
		Usuario usuario = usuarioService.getUsuarioById(loggedInUser.getId()); 
		List<FavoritoDTO> favoritoList = favoritoService.getFavoritoByIdUsuario(loggedInUser.getId());
		List<Patron> amigurumiList = patronService.getPatronByAutor(loggedInUser.getId());

		model.addAttribute("amigurumiList", amigurumiList);
		model.addAttribute("favoritoList", favoritoList);
		model.addAttribute("usuario", usuario);
		model.addAttribute("numeroFavoritos", favoritoList.size());
		model.addAttribute("numeroPatrones", amigurumiList.size());
		model.addAttribute("admin", usuario.isAdmin());
		if (usuario.isAdmin()) {
			List<Usuario> usuarioList = usuarioService.getAllUsuarios();
			model.addAttribute("usuarioList", usuarioList);
			return HTML_ADMIN_MODIFICA;

		}


		return HTML_USUARIO_MODIFICA; // Nombre de la plantilla de Thymeleaf
	}

	@GetMapping("/patrones-propios")
	public String verPatronesPropios(Model model, HttpSession session) throws ExecutionException, InterruptedException {
		if(session.getAttribute("loggedInUser") == null)
			return API_LOGIN;
		Usuario loggedInUser = (Usuario) session.getAttribute("loggedInUser");
		Usuario usuario = usuarioService.getUsuarioById(loggedInUser.getId());
		List<Patron> amigurumiList = patronService.getPatronByAutor(loggedInUser.getId());

		model.addAttribute("amigurumiList", amigurumiList);
		if (usuario.isAdmin()) {
			List<PatronDTO> allAmigurumiList =patronService.getAllPatrones();
			model.addAttribute("allAmigurumiList", allAmigurumiList);
			return HTML_ADMIN_MODIFICA_PATRON;
		}
		return HTML_PATRON_LIST; // Nombre de la nueva plantilla Thymeleaf
	}

	@GetMapping("/cambiar-contrasena/{id}")
	public String mostrarCambiarContrasena(@PathVariable("id") String id,Model model,HttpSession session) {
		session.setAttribute("loggedInUser", usuarioService.getUsuarioById(id));
		model.addAttribute("cambiarContrasenaDTO", new CambiarContrasenaDTO());
		return HTML_CAMBIO_CONTRASENA;
	}
	@GetMapping("/cambiar-contrasena")
	public String mostrarCambiarContrasenaInterno(Model model) {
		model.addAttribute("cambiarContrasenaDTO", new CambiarContrasenaDTO());
		return HTML_CAMBIO_CONTRASENA;
	}

	// Procesar el cambio de contraseña
	@PostMapping("/cambiar-contrasena")
	public String cambiarContrasena(CambiarContrasenaDTO cambiarContrasenaDTO,
									RedirectAttributes redirectAttributes, HttpSession session)
			throws ExecutionException, InterruptedException {

		Usuario loggedInUser = (Usuario) session.getAttribute("loggedInUser");

		if (!cambiarContrasenaDTO.getNuevaContrasena().equals(cambiarContrasenaDTO.getConfirmacionContrasena())) {
			redirectAttributes.addFlashAttribute("message", "Las contraseñas no coinciden.");
			return API_CAMBIO_CONTRASENA;
		}

		if (usuarioService.actualizarContrasena(loggedInUser.getId(), cambiarContrasenaDTO.getNuevaContrasena())) {
			redirectAttributes.addFlashAttribute("messageInfo", "Contraseña actualizada con éxito.");
			return API_LOGIN;
		} else {
			redirectAttributes.addFlashAttribute("message", "Error al actualizar la contraseña.");
			return API_CAMBIO_CONTRASENA;
		}
	}
	

}
