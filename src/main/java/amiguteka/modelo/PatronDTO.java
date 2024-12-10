package amiguteka.modelo;


public class PatronDTO {

	private String id;
	private String name;
	private String autor;
	private String autorNombre;
	private String descripcion;
	private String materiales;
	private String enlace;
	private String imagenPortada;
	private String imagenPortada64;


	// Constructor, getters y setters
	public PatronDTO() {
	}

	public PatronDTO(String name) {
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

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getDescripcion() {return descripcion;}

	public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

	public String getMateriales() {return materiales;}

	public void setMateriales(String materiales) {this.materiales = materiales;}

	public String getEnlace() { return enlace; }

	public void setEnlace(String enlace) {this.enlace = enlace;
	}

	@Override
	public String toString() {
		return "Patron [id=" + id + ", name=" + name + ", autor=" + autor + "]";
	}


	public String getAutorNombre() {
		return autorNombre;
	}

	public void setAutorNombre(String autorNombre) {
		this.autorNombre = autorNombre;
	}
	public String getImagenPortada() {
		return imagenPortada;
	}

	public void setImagenPortada(String imagenPortada) {
		this.imagenPortada = imagenPortada;
	}

	public String getImagenPortada64() {
		return imagenPortada64;
	}

	public void setImagenPortada64(String imagenPortada64) {
		this.imagenPortada64 = imagenPortada64;
	}
}
