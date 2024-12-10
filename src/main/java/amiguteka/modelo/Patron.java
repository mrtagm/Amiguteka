package amiguteka.modelo;

import com.google.cloud.firestore.annotation.DocumentId;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import com.google.cloud.firestore.annotation.ServerTimestamp;

public class Patron {
	@DocumentId
	private String id;
	private String name;
	private String autor;
	private String descripcion;
	private String materiales;
	private String enlace;
	private String imagenPortada;
	@ServerTimestamp
	private Date fechaSubida;

	// Constructor, getters y setters
	public Patron() {
	}

	public Patron(String name) {
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
	public String getImagenPortada() {
		return imagenPortada;
	}

	public void setImagenPortada(String imagenPortada) {
		this.imagenPortada = imagenPortada;
	}

	public Date getFechaSubida() {
		return fechaSubida;
	}

	public void setFechaSubida(Date fechaSubida) {
		this.fechaSubida = fechaSubida;
	}


	@Override
	public String toString() {
		return "Patron [id=" + id + ", name=" + name + ", autor=" + autor + "]";
	}



}
