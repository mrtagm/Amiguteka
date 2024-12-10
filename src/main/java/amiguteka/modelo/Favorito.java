package amiguteka.modelo;

import com.google.cloud.firestore.annotation.DocumentId;

public class Favorito {
    @DocumentId
    private String id;
    private String amigurumiId;
    private String usuarioId;
    private String estado;
	private String anotaciones;
    
    
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

	public String getAnotaciones() {return anotaciones;}
	public void setAnotaciones(String anotaciones) {this.anotaciones = anotaciones;}
}
