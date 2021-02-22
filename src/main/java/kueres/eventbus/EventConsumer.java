package kueres.eventbus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import kueres.event.EventEntity;
import kueres.utility.Utility;

@Component
public class EventConsumer implements MessageListener {

	private OnMessageExecutor onMessageExecutor = (Message message,
			Map<String, String> subscribers) -> defaultOnMessageExecutor(message, subscribers);

	@Autowired
	public static RabbitTemplate rabbitTemplate;

	public static ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

	private Map<String, String> subscribers = new HashMap<String, String>();

	public void subscribe(String identifier, String routingKey) {

		Utility.LOG.trace("EventConsumer.subscribe called");
		
		this.subscribers.put(identifier, routingKey);

	}

	public void unsubscribe(String identifier) {

		Utility.LOG.trace("EventConsumer.unsubscribe called");
		
		this.subscribers.remove(identifier);

	}

	@Override
	public void onMessage(Message message) {

		Utility.LOG.trace("EventConsumer.onMessage called");
		
		this.onMessageExecutor.execute(message, this.subscribers);

	}

	public void setOnMessageExecutor(OnMessageExecutor onMessageExecutor) {
		
		Utility.LOG.trace("EventConsumer.setOnMessageExecutor called");

		this.onMessageExecutor = onMessageExecutor;

	}

	public static void sendEvent(String message, int type, String sender, String entityJSON) {

		Utility.LOG.trace("EventConsumer.sendEvent called.");

		EventEntity event = new EventEntity();
		event.setMessage(message);
		event.setType(type);
		event.setSender(sender);
		event.setEntityJSON(entityJSON);

		try {
			rabbitTemplate.convertAndSend(RabbitMQConfiguration.TOPIC_EXCHANGE, RabbitMQConfiguration.DEFAULT_QUEUE,
					objectWriter.writeValueAsString(event));
		} catch (AmqpException | JsonProcessingException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	public static void sendEvent(EventEntity event) {
		
		Utility.LOG.trace("EventConsumer.sendEvent called");
		
		try {
			rabbitTemplate.convertAndSend(RabbitMQConfiguration.TOPIC_EXCHANGE, RabbitMQConfiguration.DEFAULT_QUEUE,
					objectWriter.writeValueAsString(event));
		} catch (AmqpException | JsonProcessingException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	public static String writeObjectAsJSON(Object o) {
		
		try {
			return objectWriter.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			Utility.LOG.error(e.getMessage());
			return "";
		}
		
	}

	private void defaultOnMessageExecutor(Message message, Map<String, String> subscribers) {

		subscribers.entrySet().forEach((Entry<String, String> entry) -> {
			rabbitTemplate.convertAndSend(RabbitMQConfiguration.TOPIC_EXCHANGE, entry.getValue(), message);
		});

	}

}
