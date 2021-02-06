package kueres.event;

import javax.annotation.PostConstruct;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import kueres.base.BaseService;
import kueres.media.MediaService;

@Service
public class EventService extends BaseService<EventEntity, EventRepository> {
	
	@Autowired
	MediaService mediaService;
	
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
	
	public void sendEvent(EventEntity event) throws AmqpException, JsonProcessingException {	
		
		this.sendEvent(
				event.getMessage(), 
				event.getType(), 
				event.getSender(),
				event.getEntityJSON());
		
	}

}
