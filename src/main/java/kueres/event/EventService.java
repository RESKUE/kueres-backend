package kueres.event;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import kueres.base.BaseService;

@Service
public class EventService extends BaseService<EventEntity, EventRepository> {
	
	@Override
	@PostConstruct
	public void init() {
		this.identifier = EventController.ROUTE;
		this.routingKey = EventController.ROUTE;
		this.startReceivingEvents();
	}
	
	@RabbitListener(queues = EventController.ROUTE)
	public void receiveMessage(@Payload String eventJSON) throws JsonMappingException, JsonProcessingException {
		
		EventEntity event = getEntityFromJSON(eventJSON);
		this.create(event);
		
	}

}
