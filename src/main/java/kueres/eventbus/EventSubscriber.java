package kueres.eventbus;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;

import kueres.utility.Utility;

/**
 * 
 * Super class for all classes that want to receive events.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

public abstract class EventSubscriber {

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	private EventConsumer eventConsumer;

	private Queue queue;
	private Binding binding;

	/**
	 * The subscribers identifier.
	 */
	protected String identifier;
	
	/**
	 * The subscribers RabbitMQ routing key.
	 */
	protected String routingKey;

	/**
	 * Implement this method with @PostConstruct
	 * to set the identifier and routing
	 * and to start listening to events.
	 */
	public abstract void init();

	/**
	 * Start receiving events.
	 */
	protected void startReceivingEvents() {

		Utility.LOG.trace("EventSubscriber.startReceivingEvents called");
		
		this.queue = new Queue(this.identifier, false, false, false);
		this.binding = new Binding(
				this.identifier, 
				Binding.DestinationType.QUEUE, 
				RabbitMQConfiguration.TOPIC_EXCHANGE,
				this.routingKey, 
				null);
		amqpAdmin.declareQueue(queue);
		amqpAdmin.declareBinding(binding);

		this.eventConsumer.subscribe(this.identifier, this.routingKey);

	}

	/**
	 * Stop receiving events.
	 */
	protected void stopReceivingEvents() {
		
		Utility.LOG.trace("EventSubscriber.stopReceivingEvents called");

		this.eventConsumer.unsubscribe(this.identifier);

		amqpAdmin.removeBinding(this.binding);
		amqpAdmin.deleteQueue(this.queue.getName());

	}

	public String getIdentifier() { return this.identifier; }

	public String getRoutingKey() { return this.routingKey; }

}
