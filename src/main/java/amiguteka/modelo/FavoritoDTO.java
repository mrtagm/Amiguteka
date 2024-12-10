package amiguteka.modelo;

import com.google.cloud.firestore.annotation.DocumentId;

public class FavoritoDTO {
    private String id;
    private String amigurumiId;
    private String usuarioId;
    private String estado;
	private String nombrePatron;
	private String nombreAutor;
	private String descripcion; // Descripción del patrón (no editable)
	private String anotaciones; // Anotaciones del usuario sobre el patrón (editable)
    
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAmigurumiId() {
		return amigurumiId;
	}
	public void setAmigurumiId(String amigurumiId) {
		this.amigurumiId = amigurumiId;
	}
	public String getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNombrePatron() {return nombrePatron;}
	public void setNombrePatron(String nombrePatron) {this.nombrePatron = nombrePatron;}

	public String getDescripcion() {return descripcion;}
	public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

	public String getNombreAutor() {
		return nombreAutor;
	}

	public void setNombreAutor(String nombreAutor) {
		this.nombreAutor = nombreAutor;
	}

	public String getAnotaciones() {return anotaciones;}
	public void setAnotaciones(String anotaciones) {this.anotaciones = anotaciones;}
}
