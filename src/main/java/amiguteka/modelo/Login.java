package amiguteka.modelo;

public class Login {
	private String mail;
	private String pass;

	public Login() {
		super();
	}

	public Login(String name, String pass) {
		super();
		this.mail = name;
		this.pass = pass;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

}
