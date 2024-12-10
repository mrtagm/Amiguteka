package amiguteka.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import amiguteka.converter.PatronConverter;
import amiguteka.modelo.Patron;
import amiguteka.modelo.PatronDTO;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
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

@Service
public class PatronService {

	@Autowired
	private Firestore firestore;
	@Autowired
	private PatronConverter patronConverter;

	private static final String COLLECTION_NAME = "amigurumis";

	/**
	 * 
	 * @param amigurumi
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void nuevoPatron(Patron amigurumi) throws ExecutionException, InterruptedException {
		CollectionReference amigurumiCollection = firestore.collection(COLLECTION_NAME);
		DocumentReference docRef = amigurumiCollection.add(amigurumi).get(); // Agregar documento
		amigurumi.setId(docRef.getId()); // Establecer el ID del documento en el objeto
	}

	/**
	 * 
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public List<PatronDTO> getAllPatrones() throws ExecutionException, InterruptedException {
		CollectionReference amigurumis = firestore.collection(COLLECTION_NAME);
		ApiFuture<QuerySnapshot> querySnapshot = amigurumis.orderBy("fechaSubida", Query.Direction.DESCENDING).get();

		List<PatronDTO> amigurumiList = new ArrayList<>();
		for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
			Patron patron = document.toObject(Patron.class);
			amigurumiList.add(patronConverter.convertToDTO(patron));
		}

		return amigurumiList;
	}

	/**
	 * 
	 * @param patron
	 */
	public void editarPatron(Patron patron) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(patron.getId());

		// Crea un mapa con los campos que deseas actualizar
		Map<String, Object> updates = new HashMap<>();
		updates.put("name", patron.getName());
		updates.put("descripcion", patron.getDescripcion());
		updates.put("materiales", patron.getMateriales());
		updates.put("enlace", patron.getEnlace());
		updates.put("imagenPortada", patron.getImagenPortada());

		// Solo actualiza la imagen si tiene un valor
		if (patron.getImagenPortada() != null && !patron.getImagenPortada().isEmpty()) {
			updates.put("imagenPortada", patron.getImagenPortada());
		}
		// Ejecuta la actualización
		ApiFuture<WriteResult> future = docRef.update(updates);

		try {
			WriteResult result = future.get();
			System.out.println("patron actualizado en: " + result.getUpdateTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param amigurumiId
	 */
	public void borrarPatron(String amigurumiId) {
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(amigurumiId);

		// Elimina el documento
		ApiFuture<WriteResult> future = docRef.delete();

		try {
			WriteResult result = future.get();
			System.out.println("amigurumi eliminado en: " + result.getUpdateTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public PatronDTO getPatronById(String id) {
		try {
			// Referencia al documento en Firestore
			DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);

			// Obtener el documento de manera asíncrona
			ApiFuture<DocumentSnapshot> future = docRef.get();

			// Bloquear hasta que el futuro se complete y obtener el snapshot del documento
			DocumentSnapshot document = future.get();

			if (document.exists()) {
				// Convertir el documento a un objeto Amigurumi
				return patronConverter.convertToDTO(document.toObject(Patron.class));
			} else {
				System.out.println("No se encontró el amigurumi con ID: " + id);
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param autor
	 * @return
	 */
	public List<Patron> getPatronByAutor(String autor) {
		CollectionReference amigurumisRef = firestore.collection(COLLECTION_NAME);

		// Crea una consulta para encontrar todos los amigurumis con el color
		// especificado
		Query query = amigurumisRef.whereEqualTo("autor", autor);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();

		List<Patron> amigurumiList = new ArrayList<>();

		try {
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
			if (!documents.isEmpty()) {
				for (QueryDocumentSnapshot document : documents) {
					Patron amigurumi = document.toObject(Patron.class);
					amigurumiList.add(amigurumi);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amigurumiList;
	}

}
