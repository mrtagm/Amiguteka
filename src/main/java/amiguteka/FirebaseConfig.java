package amiguteka;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {
	@Bean
	public Firestore firestore() {
		try {
			// Carga el archivo JSON desde el classpath
			InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("patron.json");
			if (serviceAccount == null) {
				throw new IllegalArgumentException("No se encontró el archivo patron.json en resources");
			}

			// Inicializa Firebase
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

			FirebaseApp.initializeApp(options);
			// Crea el objeto Firestore
			return FirestoreClient.getFirestore();
		} catch (Exception e) {

			throw new RuntimeException("Error al inicializar Firestore: " + e.getMessage(), e);
		}
	}

}
