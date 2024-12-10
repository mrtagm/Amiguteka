package amiguteka.modelo;

import com.google.cloud.firestore.annotation.DocumentId;

public class Usuario {
	@DocumentId
	private String id;
	private String name;
	private String email;
	private String pass;
	private boolean confirmado;
	private String codigoAutentificacion;
	private boolean admin;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	// Constructor, getters y setters
	public Usuario() {
	}

	public Usuario(String name, String mail, String pass) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String mail) {
		this.email = mail;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}

	public String getCodigoAutentificacion() {
		return codigoAutentificacion;
	}

	public void setCodigoAutentificacion(String codigoAutentificacion) {
		this.codigoAutentificacion = codigoAutentificacion;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", name=" + name + ", mail=" + email + ", pass=" + pass + "]";
	}
	
}
