package kueres.event;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import kueres.base.BaseService;

/**
 * 
 * The EventService provides services needed by the EventController.
 * It also persists all events.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

@Service
public class EventService extends BaseService<EventEntity, EventRepository> {
	
	/**
	 * Set this EventSubscribers identifier and routing
	 * and start receiving events.
	 */
	@Override
	@PostConstruct
	public void init() {
		this.identifier = EventController.ROUTE;
		this.routingKey = EventController.ROUTE;
		this.startReceivingEvents();
	}
	
	/**
	 * Receive all events and persist them as EventEntities.
	 * @param eventJSON - JSON representation of the received event
	 * @throws JsonMappingException when the JSON string could not be deserialized.
	 * @throws JsonProcessingException when the JSON string could not be deserialized.
	 */
	@RabbitListener(queues = EventController.ROUTE)
	public void receiveMessage(@Payload String eventJSON) throws JsonMappingException, JsonProcessingException {
		
		EventEntity event = new EventEntity().getEntityFromJSON(eventJSON);
		this.repository.save(event);
		
	}

}
