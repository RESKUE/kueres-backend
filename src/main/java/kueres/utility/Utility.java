package kueres.utility;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import kueres.event.EventEntity;
import kueres.eventbus.EventSubscriber;

/*
 * ToDo: update receive message to json string
 */

@Service
public class Utility extends EventSubscriber {

	public static final Logger LOG = (Logger) LoggerFactory.getLogger("logger");
	private final Logger eventLog = (Logger) LoggerFactory.getLogger("event");
	
	private ReceiveMessageExecutor receiveMessageExecutor = 
			(EventEntity event) -> defaultReceiveMessageExecutor(event);

	@Override
	@PostConstruct
	public void init() {
		
		this.identifier = "eventLogger";
		this.routingKey = "eventLogger";
		this.startReceivingEvents();
		
	}
	
	@RabbitListener(queues = "eventLogger")
	public void receiveMessage(@Payload String eventJSON) throws JsonMappingException, JsonProcessingException {
		
		Utility.LOG.trace("Utility.receiveMessage called");
		
		EventEntity event = getEntityFromJSON(eventJSON);
		this.receiveMessageExecutor.execute(event);
		
	}
	
	public void setReceiveMessageExecutor(ReceiveMessageExecutor receiveMessageExecutor) {
		
		this.receiveMessageExecutor = receiveMessageExecutor;
		
	}
	
	private void defaultReceiveMessageExecutor(EventEntity event) {
		
		this.eventLog.info("\nsender: {}\nmessage: {}\nevent: {}\n\n", event.getSender(), event.getMessage(), event);
		
	}
	
	private EventEntity getEntityFromJSON(String json) throws JsonMappingException, JsonProcessingException  {
		
		return new ObjectMapper().readValue(json, EventEntity.class);
		
	}
	
}
