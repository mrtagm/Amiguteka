package amiguteka.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import amiguteka.modelo.Login;
import amiguteka.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;


@Service
public class UsuarioService {

	@Autowired
	private Firestore firestore;

	private static final String COLLECTION_NAME = "usuarios";

	public void nuevoUsuario(Usuario usuario) throws InterruptedException, ExecutionException {
		CollectionReference usuarioCollection = firestore.collection(COLLECTION_NAME);
		usuario.setConfirmado(false);
		usuario.setPass(this.encryptPassword(usuario.getPass()));
		DocumentReference docRef = usuarioCollection.add(usuario).get();
		usuario.setId(docRef.getId());

	}

	public List<Usuario> getAllUsuarios() throws InterruptedException, ExecutionException {
		CollectionReference usuarios = firestore.collection(COLLECTION_NAME);
		ApiFuture<QuerySnapshot> querySnapshot = usuarios.get();

		List<Usuario> usuarioList = new ArrayList<>();
		for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
			Usuario usuario = document.toObject(Usuario.class);
			usuarioList.add(usuario);
		}

		return usuarioList;
	}

	public void borrarUsuario(String id) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);

		// Elimina el documento
		ApiFuture<WriteResult> future = docRef.delete();

		try {
			WriteResult result = future.get();
			System.out.println("usuario eliminado en: " + result.getUpdateTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void editarUsuario(Usuario usuario) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(usuario.getId());

		// Crea un mapa con los campos que deseas actualizar
		Map<String, Object> updates = new HashMap<>();
		updates.put("name", usuario.getName());

		// Ejecuta la actualización
		ApiFuture<WriteResult> future = docRef.update(updates);

		try {
			WriteResult result = future.get();
			System.out.println("Usuario actualizado en: " + result.getUpdateTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Usuario getUsuarioById(String id) {
		try {
			// Referencia al documento en Firestore
			DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);

			// Obtener el documento de manera asíncrona
			ApiFuture<DocumentSnapshot> future = docRef.get();

			// Bloquear hasta que el futuro se complete y obtener el snapshot del documento
			DocumentSnapshot document = future.get();

			if (document.exists()) {
				// Convertir el documento a un objeto Usuario
				return document.toObject(Usuario.class);
			} else {
				System.out.println("No se encontró el usuario con ID: " + id);
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Usuario getUsuarioByName(String name) {
		try {
			// Referencia a la colección de usuarios
			CollectionReference users = firestore.collection(COLLECTION_NAME);

			// Crear una consulta para encontrar el usuario por username
			Query query = users.whereEqualTo("name", name);

			// Ejecutar la consulta de manera asíncrona
			ApiFuture<QuerySnapshot> querySnapshot = query.get();

			// Obtener los documentos que coinciden con la consulta
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

			if (!documents.isEmpty()) {
				// Convertir el primer documento en un objeto User
				return documents.get(0).toObject(Usuario.class);
			} else {
				System.out.println("Usuario no encontrado con el name: " + name);
				return null;
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Usuario getUsuarioByEmail(String email) {
		try {
			CollectionReference users = firestore.collection(COLLECTION_NAME);

			Query query = users.whereEqualTo("email", email);
			ApiFuture<QuerySnapshot> querySnapshot = query.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
			if (!documents.isEmpty()) {
				return documents.get(0).toObject(Usuario.class);

			}else {
				System.out.println("Usuario no encontrado con el email: " + email);
				return null;
			}

		}catch (InterruptedException | ExecutionException e){
			e.printStackTrace();
			return null;
		}


	}
	public boolean actualizarContrasena(String userId, String nuevaContrasena) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);

		Map<String, Object> updates = new HashMap<>();
		updates.put("pass", encryptPassword(nuevaContrasena));

		ApiFuture<WriteResult> future = docRef.update(updates);

		try {
			WriteResult result = future.get();
			System.out.println("Contraseña actualizada en: " + result.getUpdateTime());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * 
	 * @param login
	 * @return
	 */
	public boolean loguear (Login login) {
		Usuario user = getUsuarioByEmail(login.getMail());
		if (login != null  && login.getPass() != null && user != null && user.getPass() != null && user.isConfirmado())
			return checkPassword(login.getPass(), user.getPass());
		return false;
	}

	public boolean checkPassword(String plainPassword, String hashedPassword) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.matches(plainPassword, hashedPassword);  // Compara la contraseña
	}

	public void confirmarUsuario(String usuarioId) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(usuarioId);
		Map<String, Object> updates = new HashMap<>();
		updates.put("confirmado", true);
		docRef.update(updates);
	}

	public void crearCodigoAutentificacion(String userId, String codigoAutentificacion) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);

		Map<String, Object> updates = new HashMap<>();
		updates.put("codigoAutentificacion", codigoAutentificacion);
		docRef.update(updates);
	}

	public Usuario getUsuarioByCodigoAutentificacion(String codigoAutentificacion) {
		try {
			CollectionReference users = firestore.collection(COLLECTION_NAME);

			Query query = users.whereEqualTo("codigoAutentificacion", codigoAutentificacion);
			ApiFuture<QuerySnapshot> querySnapshot = query.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
			if (!documents.isEmpty()) {
				return documents.get(0).toObject(Usuario.class);

			} else {
				System.out.println("Usuario no encontrado con el codigoAutentificacion: " + codigoAutentificacion);
				return null;
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
	public  String encryptPassword(String pass) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String codificado = encoder.encode(pass);
		System.out.println(codificado);
		return codificado;
	}
}
