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

/**
 * 
 * The EventConsumer receives all events and distributes them to EventSubscribers.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0.0
 * @since Apr 26, 2021
 *
 */

@Component
public class EventConsumer implements MessageListener {

	private OnMessageExecutor onMessageExecutor = (Message message,
			Map<String, String> subscribers) -> defaultOnMessageExecutor(message, subscribers);

	private static RabbitTemplate RABBIT_TEMPLATE;
	@Autowired
	private void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		EventConsumer.RABBIT_TEMPLATE = rabbitTemplate;
	}
	
	private static ObjectWriter OBJECT_WRITER = new ObjectMapper().writer().withDefaultPrettyPrinter();

	
	private Map<String, String> subscribers = new HashMap<String, String>();

	/**
	 * Register an EventSubscriber by providing his identifier and routing key.
	 * @param identifier - the EventSubscribers' identifier
	 * @param routingKey - the EventSubscribers' routing key
	 */
	public void subscribe(String identifier, String routingKey) {

		Utility.LOG.trace("EventConsumer.subscribe called");
		
		this.subscribers.put(identifier, routingKey);

	}

	/**
	 * Unregister an EventSubscriber by his identifier.
	 * @param identifier - the EventSubscribers' identifier
	 */
	public void unsubscribe(String identifier) {

		Utility.LOG.trace("EventConsumer.unsubscribe called");
		
		this.subscribers.remove(identifier);

	}

	/**
	 * This method gets called when the EventConsumer receives an event.
	 */
	@Override
	public void onMessage(Message message) {

		Utility.LOG.trace("EventConsumer.onMessage called");
		
		this.onMessageExecutor.execute(message, this.subscribers);

	}

	/**
	 * Provide a custom implementation to customize how events are distributed to EventSubscribers.
	 * @param onMessageExecutor - the custom implementation of the OnMessageExecutor
	 */
	public void setOnMessageExecutor(OnMessageExecutor onMessageExecutor) {
		
		Utility.LOG.trace("EventConsumer.setOnMessageExecutor called");

		this.onMessageExecutor = onMessageExecutor;

	}

	/**
	 * Send an event to the EventConsumer.
	 * @param message - the events message
	 * @param type - the event type: see kueres.event.EventType for all types
	 * @param sender - the identifier of the event sender
	 * @param entityJSON - JSON representation of the entity that was affected by the event
	 */
	public static void sendEvent(String message, int type, String sender, String entityJSON) {

		Utility.LOG.trace("EventConsumer.sendEvent called.");

		EventEntity event = new EventEntity();
		event.setMessage(message);
		event.setType(type);
		event.setSender(sender);
		event.setEntityJSON(entityJSON);

		try {
			RABBIT_TEMPLATE.convertAndSend(RabbitMQConfiguration.TOPIC_EXCHANGE, RabbitMQConfiguration.DEFAULT_QUEUE,
					OBJECT_WRITER.writeValueAsString(event));
		} catch (AmqpException | JsonProcessingException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	/**
	 * Send an event to the EventConsumer.
	 * @param event - the event to be sent
	 */
	public static void sendEvent(EventEntity event) {
		
		Utility.LOG.trace("EventConsumer.sendEvent called");
		
		try {
			RABBIT_TEMPLATE.convertAndSend(RabbitMQConfiguration.TOPIC_EXCHANGE, RabbitMQConfiguration.DEFAULT_QUEUE,
					OBJECT_WRITER.writeValueAsString(event));
		} catch (AmqpException | JsonProcessingException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * Serialize an object to JSON.
	 * @param o - the object to be serialized
	 * @return The objects JSON representation.
	 */
	public static String writeObjectAsJSON(Object o) {
		
		try {
			return OBJECT_WRITER.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			Utility.LOG.error(e.getMessage());
			return "";
		}
		
	}

	private void defaultOnMessageExecutor(Message message, Map<String, String> subscribers) {

		subscribers.entrySet().forEach((Entry<String, String> entry) -> {
			RABBIT_TEMPLATE.convertAndSend(RabbitMQConfiguration.TOPIC_EXCHANGE, entry.getValue(), message);
		});

	}

}
