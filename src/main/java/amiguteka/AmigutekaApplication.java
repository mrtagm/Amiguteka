package amiguteka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AmigutekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmigutekaApplication.class, args);
	}

}
