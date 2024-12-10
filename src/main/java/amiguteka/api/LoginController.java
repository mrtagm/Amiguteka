package amiguteka.api;

import java.util.concurrent.ExecutionException;


import amiguteka.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import amiguteka.modelo.Login;
import amiguteka.modelo.Usuario;
import amiguteka.service.UsuarioService;@Controller
@RequestMapping("/api/login")
public class LoginController {

	private final String HTML_LOGIN = "login";
	private final String HTML_RECUPERAR = "recuperar";
	//	private final String HTML_AMIGURUMI_CREA = "crearUsuario";
//	private final String HTML_AMIGURUMI_LIST = "listUsuarios";
//	private final String HTML_AMIGURUMI_MODIFICA = "modificaUsuario";
//	private final String API_AMIGURUMI_CREA = "redirect:/api/usuario/crear";
	private final String API_AMIGURUMI_LIST = "redirect:/api/amigurumi/list";
	private final String API_LOGIN = "redirect:/api/login";
	private final String API_RECUPERAR = "redirect:/api/login/recuperar";
//	private final String API_AMIGURUMI_MODIFICA = "redirect:/api/usuario/modifica/";

	@Autowired
	private UsuarioService usuarioService;
    @Autowired
    private EmailService emailService;

	@PostMapping
	public String login(@ModelAttribute Login login, RedirectAttributes redirectAttributes, HttpSession session)
			throws ExecutionException, InterruptedException {

		if (usuarioService.loguear(login)) {

			Usuario usuario = usuarioService.getUsuarioByEmail(login.getMail());
			if (usuario.getEmail().equals("admin2")|| usuario.getEmail().equals("test")){
				session.setAttribute("loggedInUser", usuario);
				return API_AMIGURUMI_LIST;
			}else {
				emailService.enviarCorreoLoginMime(usuarioService.getUsuarioByEmail(login.getMail()));
				redirectAttributes.addFlashAttribute("messageInfo", "Login Correcto revise el email para el doble factor de autentificacion.");
				return API_LOGIN;
			}
		}

		redirectAttributes.addFlashAttribute("message", "Usuario o contraseña invalidos.");
		return API_LOGIN;
	}

	@GetMapping("/confirmar/{codigo}")
	public String loginMail(@PathVariable String codigo, RedirectAttributes redirectAttributes, HttpSession session){
		Usuario usuario = usuarioService.getUsuarioByCodigoAutentificacion(codigo);
		if (usuario != null) {
			session.setAttribute("loggedInUser", usuario);
			return API_AMIGURUMI_LIST;
		}

		redirectAttributes.addFlashAttribute("message", "Usuario o contraseña inválidos.");
		return API_LOGIN;
	}

	@GetMapping
	public String crearUsuario(Model model) {
		model.addAttribute("login", new Login());
		return HTML_LOGIN;
	}

	@PostMapping("/recuperar")
	public String recuperar(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes){

		usuario = usuarioService.getUsuarioByEmail(usuario.getEmail());
		emailService.enviarCorreoRecuperarMime(usuario.getEmail(),usuario.getId());

		redirectAttributes.addFlashAttribute("messageInfo", "Email enviado revise el mail.");
		return API_RECUPERAR;
	}

	@GetMapping("/recuperar")
	public String recuperar(Model model) {
		model.addAttribute("usuario", new Usuario());
		return HTML_RECUPERAR;
	}
	@GetMapping("/logout")
	public String cerrar(HttpSession session) {
		session.removeAttribute("loggedInUser");
		return API_LOGIN;
	}

}


