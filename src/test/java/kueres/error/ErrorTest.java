package kueres.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import kueres.KueresTestInitializer;

@SpringBootTest
@ContextConfiguration(initializers = KueresTestInitializer.class)
@TestPropertySource(locations="classpath:test.properties")
@TestInstance(Lifecycle.PER_CLASS)
public class ErrorTest {

	@Autowired
	private ErrorController errorController;
	
	@Test
	public void correctErrorResponse() {
		
		Exception exception = new Exception("");
		
		ResponseEntity<String> errorResponse = errorController.error(exception);
		String error = errorResponse.getBody();

		String[] parts = error.split(",\n\t");
		for (int i = 0; i < (parts.length - 0); i++) {
			if (i == 1) {
				assertThat(parts[i]).isEqualTo("\"status\": 400");
			} else if (i == 2) {
				assertThat(parts[i]).isEqualTo("\"error\": \"Bad request\"");
			} else if (i == 3) {
				assertThat(parts[i]).isEqualTo("\"message\": \"No message available\"");
			} else if (i == 4) {
				assertThat(parts[i]).startsWith("\"path\": \"/api\"");
			}
			
		}
		
	}
	
	@Test
	public void expectResponseStatusException() {
		
		assertThrows(ResponseStatusException.class, () -> {
			errorController.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
		});
		
	}
	
}
