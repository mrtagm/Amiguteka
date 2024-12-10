package amiguteka.service;
import amiguteka.modelo.Usuario;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UsuarioService usuarioService;

    @Async
    public void enviarCorreoVerificacion(String destinatario, String codigoVerificacion) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Código de Verificación - 2FA");
        mensaje.setText("Tu código de verificación es: " + codigoVerificacion);
        mailSender.send(mensaje);
    }

    @Async
    public void enviarCorreoVerificacionCrear(String destinatario, String usuarioId) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Código de Activación cuenta de Amiguteka.");
        mensaje.setText("El enlace para activar la cuenta de amiguteka es: https:\\192.168.66.129:8443\\api\\usuario\\crear\\confirmar\\" + usuarioId );
        mailSender.send(mensaje);
    }
    @Async
    public void enviarCorreoRecuperar(String destinatario, String usuarioId) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Mail recuperacion de contraseña de Amiguteka.");
        mensaje.setText("El enlace para cambiar la contraseña de amiguteka es: https:\\192.168.66.129:8443\\api\\usuario\\cambiar-contrasena\\" +usuarioId );
        mailSender.send(mensaje);

    }

    @Async
    public void enviarCorreoLogin(Usuario usuario) {

        String codigoAutentificacion = codigo();
        usuarioService.crearCodigoAutentificacion(usuario.getId(), usuario.getId()+codigoAutentificacion);

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(usuario.getEmail());
        mensaje.setSubject("Email de autentificación de Amiguteka.");
        mensaje.setText("Para acceder use este enlace: https:\\192.168.66.129:8443\\api\\login\\confirmar\\" +usuario.getId()+codigoAutentificacion );
        mailSender.send(mensaje);

    }

    public String codigo() {
        LocalDateTime fecha = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return fecha.format(formatter);
    }




    @Async
    public void enviarCorreoVerificacionCrearMime(String destinatario, String usuarioId) {
        try {
            // Crear un MimeMessage
            MimeMessage mensaje = mailSender.createMimeMessage();

            // Usar MimeMessageHelper para construir el correo
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true); // 'true' para habilitar contenido HTML
            helper.setTo(destinatario);
            helper.setSubject("Código de Activación cuenta de Amiguteka.");

            // Construir el contenido del mensaje HTML
            String enlace = "https://192.168.66.129:8443/api/usuario/crear/confirmar/" + usuarioId;
            String contenido = """
                <html>
                <body>
                    <p>Gracias por registrarte en Amiguteka.</p>
                    <p>Por favor, haz clic en el enlace de abajo para activar tu cuenta:</p>
                    <p><a href="%s" target="_blank">Activar cuenta</a></p>
                    <p>o cópielo: https://192.168.66.129:8443/api/usuario/crear/confirmar/%s</p>
                    <p>o cópielo: https://amiguteka:8443/api/usuario/crear/confirmar/%s</p>
                </body>
                </html>
                """.formatted(enlace,usuarioId, usuarioId);

            helper.setText(contenido, true); // 'true' para indicar que el contenido es HTML

            // Enviar el correo
            mailSender.send(mensaje);
        } catch (MessagingException e) {
            // Manejar excepciones
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }


    }



    @Async
    public void enviarCorreoLoginMime(Usuario usuario) {

        String codigoAutentificacion = codigo();
        usuarioService.crearCodigoAutentificacion(usuario.getId(), usuario.getId()+codigoAutentificacion);


        try {
            // Crear un MimeMessage
            MimeMessage mensaje = mailSender.createMimeMessage();

            // Usar MimeMessageHelper para construir el correo
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true); // 'true' para habilitar contenido HTML
            helper.setTo(usuario.getEmail());
            helper.setSubject("Email de autentificación de Amiguteka.");

            // Construir el contenido del mensaje HTML
            String enlace = "https://192.168.66.129:8443/api/login/confirmar/" + usuario.getId()+codigoAutentificacion;
            String contenido = """
                <html>
                <body>
                    <p>Por favor, para acceder a amiguteka use este enlace como doble factor:</p>
                    <p><a href="%s" target="_blank"> acceder a amiguteka</a></p>
                    <p>o cópielo: https://192.168.66.129:8443/api/login/confirmar/%s</p>
                    <p>o cópielo: https://amiguteka:8443/api/login/confirmar/%s</p>
                </body>
                </html>
                """.formatted(enlace, usuario.getId()+codigoAutentificacion,usuario.getId()+codigoAutentificacion);

            helper.setText(contenido, true); // 'true' para indicar que el contenido es HTML

            // Enviar el correo
            mailSender.send(mensaje);
        } catch (MessagingException e) {
            // Manejar excepciones
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }




    @Async
    public void enviarCorreoRecuperarMime(String destinatario, String usuarioId) {

        try {
            // Crear un MimeMessage
            MimeMessage mensaje = mailSender.createMimeMessage();

            // Usar MimeMessageHelper para construir el correo
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
            helper.setTo(destinatario);
            helper.setSubject("Mail recuperación de contraseña de Amiguteka.");

            // Construir el contenido del mensaje HTML
            String enlace = "https://192.168.66.129:8443/api/usuario/cambiar-contrasena/" + usuarioId;
            String contenido = """
                <html>
                <body>
                    <p>Por favor, El enlace para cambiar la contraseña de amiguteka es::</p>
                    <p><a href="%s" target="_blank"> acceder a amiguteka</a></p>
                    <p>o cópielo: https://192.168.66.129:8443/api/usuario/cambiar-contrasena/%s</p>
                </body>
                </html>
                """.formatted(enlace, usuarioId);

            helper.setText(contenido, true); // 'true' para indicar que el contenido es HTML

            // Enviar el correo
            mailSender.send(mensaje);
        } catch (MessagingException e) {
            // Manejar excepciones
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
