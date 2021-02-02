package kueres.event;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import kueres.base.BaseEntity;
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
	
	@SuppressWarnings("unchecked")
	@RabbitListener(queues = EventController.ROUTE)
	public void receiveMessage(
			@Payload BaseEntity<?> entity,
			@Header("senderIdentifier") String senderIdentifier,
			@Header("message") String message,
			@Header("type") int type
			) throws UnsupportedEncodingException {
		
		EventEntity event = new EventEntity();
		event.setMessage(message);
		event.setType(type);
		event.setSender(senderIdentifier);
		event.setEntityType((Class<? extends BaseEntity<?>>) entity.getClass());
		event.setSendAt(new Date());
		this.create(event);
		
	}
	
	public EventEntity sendEvent(EventEntity event) {
		
		EventEntity populated = new EventEntity();
		populated.applyPatch(event);
		
		this.sendEvent(
				populated.getMessage(), 
				populated.getType(), 
				populated.getSender(),
				event);
		
		return populated;
		
	}

}
