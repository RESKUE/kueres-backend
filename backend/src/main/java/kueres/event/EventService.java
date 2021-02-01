package kueres.event;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import kueres.base.BaseService;

public class EventService extends BaseService<EventEntity, EventRepository> {

	@Autowired
	private MessageConverter messageConverter;
	
	@Override
	@PostConstruct
	public void init() {
		this.identifier = EventController.ROUTE;
		this.routingKey = EventController.ROUTE;
		this.startReceivingEvents();
	}
	
	@RabbitListener(queues = EventController.ROUTE)
	public void receiveMessage(
			Message messageObject,
			@Payload String message,
			@Header("senderIdentifier") String senderIdentifier
			) throws UnsupportedEncodingException {
		
		Object entity = messageConverter.fromMessage(messageObject);
		
		EventEntity event = new EventEntity();
		event.setMessage(message);
		event.setType(0);
		event.setSender(senderIdentifier);
		event.setEntity(entity);
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
				new String[0], 
				event);
		
		return populated;
		
	}

}
