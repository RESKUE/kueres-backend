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
import kueres.media.MediaEntity;
import kueres.media.MediaService;
import kueres.utility.Utility;

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
		Utility.LOG.info("received event: {}", eventJSON);
		EventEntity event = getEntityFromJSON(eventJSON);
		Utility.LOG.info("converted to event entity: {}", event);
		this.create(event);
	}
	
	public void sendEvent(EventEntity event) throws AmqpException, JsonProcessingException {	
		Utility.LOG.info("event entity @service: {}", event);
		
		String entityJSON = event.getEntityJSON();
		MediaEntity media = mediaService.getEntityFromJSON(entityJSON);
		Utility.LOG.info("media: id: {}, location: {}, mimeType: {}, altText: {}", media.getId(), media.getLocation(), media.getMimeType(), media.getAltText());
		
		this.sendEvent(
				event.getMessage(), 
				event.getType(), 
				event.getSender(),
				event.getEntityJSON());
		
	}

}
