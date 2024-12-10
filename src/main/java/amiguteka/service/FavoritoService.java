package amiguteka.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import amiguteka.converter.FavoritoConverter;
import amiguteka.modelo.FavoritoDTO;
import amiguteka.modelo.Patron;
import org.springframework.beans.factory.annotation.Autowired;
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

import amiguteka.modelo.Favorito;

@Service
public class FavoritoService {

	@Autowired
	private Firestore firestore;

	@Autowired
	private PatronService patronService;

	@Autowired
	private FavoritoConverter favoritoConverter;

	private static final String COLLECTION_NAME = "favoritos";

	public void nuevoFavorito(Favorito favorito) throws InterruptedException, ExecutionException {
		CollectionReference favoritoCollection = firestore.collection(COLLECTION_NAME);
		DocumentReference docRef = favoritoCollection.add(favorito).get();
		favorito.setId(docRef.getId());
	}

	public void borrarFavorito(String id) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
		// Elimina el documento
		ApiFuture<WriteResult> future = docRef.delete();

		try {
			WriteResult result = future.get();
			System.out.println("favorito ("+id+") eliminado en: " + result.getUpdateTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public FavoritoDTO getFavoritoById(String id) {
		try {
			// Referencia al documento en Firestore
			DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);

			// Obtener el documento de manera asíncrona
			ApiFuture<DocumentSnapshot> future = docRef.get();

			// Bloquear hasta que el futuro se complete y obtener el snapshot del documento
			DocumentSnapshot document = future.get();

			if (document.exists()) {
				// Convertir el documento a un objeto Favorito
				return favoritoConverter.convertToDTO(document.toObject(Favorito.class));
			} else {
				System.out.println("No se encontró el favorito con ID: " + id);
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void editarFavorito(Favorito favorito) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(favorito.getId());

		// Crea un mapa con los campos que deseas actualizar
		Map<String, Object> updates = new HashMap<>();
		updates.put("estado", favorito.getEstado());
		updates.put("amigurumiId", favorito.getAmigurumiId());
		updates.put("usuarioId", favorito.getUsuarioId());
		updates.put("anotaciones", favorito.getAnotaciones());

		// Ejecuta la actualización
		ApiFuture<WriteResult> future = docRef.update(updates);

		try {
			WriteResult result = future.get();
			System.out.println("Favorito actualizado en: " + result.getUpdateTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<FavoritoDTO> getFavoritoByIdUsuario(String idUsuario) {
		CollectionReference favoritosRef = firestore.collection(COLLECTION_NAME);

		// Crea una consulta para encontrar todos los amigurumis con el color
		// especificado
		Query query = favoritosRef.whereEqualTo("usuarioId", idUsuario);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();

		List<Favorito> favoritoList = new ArrayList<>();

		try {
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
			if (!documents.isEmpty()) {
				for (QueryDocumentSnapshot document : documents) {
					Favorito favoritos = document.toObject(Favorito.class);
					favoritoList.add(favoritos);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return favoritoConverter.convertToDTO(favoritoList);
	}

	public List<Favorito> getFavoritos() throws InterruptedException, ExecutionException {
		CollectionReference favoritos = firestore.collection(COLLECTION_NAME);
		ApiFuture<QuerySnapshot> querySnapshot = favoritos.get();

		List<Favorito> favoritoList = new ArrayList<>();
		for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
			Favorito favorito = document.toObject(Favorito.class);
			favoritoList.add(favorito);
		}

		return favoritoList;
	}

	public void nuevoFavorito(String amigurumiId, String usuarioId) throws InterruptedException, ExecutionException {

		Favorito favorito = new Favorito();
		favorito.setAmigurumiId(amigurumiId);
		favorito.setUsuarioId(usuarioId);
		favorito.setEstado("Pendiente");

		nuevoFavorito(favorito);
	}

	public FavoritoDTO getFavoritoByIdUsuarioAndAmigurumi(String idUsuario, String idAmigurumi) {

		CollectionReference favoritosRef = firestore.collection(COLLECTION_NAME);

		Query query = favoritosRef.whereEqualTo("usuarioId", idUsuario).whereEqualTo("amigurumiId", idAmigurumi);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();


		try {
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
			if (!documents.isEmpty()) {
				for (QueryDocumentSnapshot document : documents) {
					return favoritoConverter.convertToDTO(document.toObject(Favorito.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public void borrarFavoritoPatron(String amigurumiId) {

		List<Favorito> favoritos = this.getFavoritoByIdAmigurumi(amigurumiId);

		for (Favorito favorito : favoritos) {
			this.borrarFavorito(favorito.getId());
		}
    }

	private List<Favorito> getFavoritoByIdAmigurumi(String amigurumiId) {
		CollectionReference favoritosRef = firestore.collection(COLLECTION_NAME);

		Query query = favoritosRef.whereEqualTo("amigurumiId", amigurumiId);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();

		List<Favorito> favoritoList = new ArrayList<>();

		try {
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
			if (!documents.isEmpty()) {
				for (QueryDocumentSnapshot document : documents) {
					Favorito favoritos = document.toObject(Favorito.class);
					favoritoList.add(favoritos);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return favoritoList;
	}

	public int contarFavoritos(String amigurumiId) {
		// Acceder a la colección "favoritos" en Firestore
		CollectionReference favoritosRef = firestore.collection("favoritos");
		Query query = favoritosRef.whereEqualTo("amigurumiId", amigurumiId);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();

		try {
			QuerySnapshot snapshot = querySnapshot.get();
			return snapshot.getDocuments().size(); // Contar los documentos (favoritos) encontrados
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return 0; // En caso de error, retornar 0
		}
	}
}
