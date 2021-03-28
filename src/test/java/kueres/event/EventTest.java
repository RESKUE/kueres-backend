package kueres.event;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import kueres.KueresTestInitializer;

@SpringBootTest
@ContextConfiguration(initializers = KueresTestInitializer.class)
@TestPropertySource(locations="classpath:test.properties")
@TestInstance(Lifecycle.PER_CLASS)
public class EventTest {

	@Autowired
	private EventController eventController;
	
	@Test
	public void getUpdateableFields() {
		
		EventEntity event = new EventEntity();
		String[] updateableFields = event.getUpdateableFields();
		
		for (String field : updateableFields) {
			ReflectionTestUtils.getField(event, field);
		}
		
	}
	
	@Test
	@WithMockUser(roles = { "administrator" })
	public void eventNotUpdateable() {
		
		assertThrows(UnsupportedOperationException.class, () -> {
			
			EventEntity event = new EventEntity();
			event.applyPatch("");
			
		});
		
		assertThrows(UnsupportedOperationException.class, () -> {
			
			eventController.update(0L, null, null);
			
		});
		
	}
	
}
