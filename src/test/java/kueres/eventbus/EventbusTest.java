package kueres.eventbus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import kueres.KueresTestInitializer;
import kueres.event.EventController;
import kueres.event.EventEntity;
import kueres.event.EventService;

@SpringBootTest
@ContextConfiguration(initializers = KueresTestInitializer.class)
@TestPropertySource(locations="classpath:test.properties")
@TestInstance(Lifecycle.PER_CLASS)
public class EventbusTest {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private EventConsumer eventConsumer;
	
	@Test
	public void getIdentifierAndRoutingKey() {
	
		assertThat(eventService.getRoutingKey()).isEqualTo(EventController.ROUTE);
		assertThat(eventService.getIdentifier()).isEqualTo(EventController.ROUTE);
		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void startAndStopReceivingEvents() {
		
		Map<String, String> subscribers = (Map<String, String>) ReflectionTestUtils.getField(eventConsumer, "subscribers");
		assertThat(subscribers.containsKey(eventService.getIdentifier())).isTrue();
		
		ReflectionTestUtils.invokeMethod(eventService, "stopReceivingEvents");
		subscribers = (Map<String, String>) ReflectionTestUtils.getField(eventConsumer, "subscribers");
		assertThat(subscribers.containsKey(eventService.getIdentifier())).isFalse();
		
		ReflectionTestUtils.invokeMethod(eventService, "startReceivingEvents");
		subscribers = (Map<String, String>) ReflectionTestUtils.getField(eventConsumer, "subscribers");
		assertThat(subscribers.containsKey(eventService.getIdentifier())).isTrue();
		
	}
	
	@Test
	public void sendEntityAsEvent() {
		
		EventEntity event = new EventEntity();
		EventConsumer.sendEvent(event);
		
	}
	
}
