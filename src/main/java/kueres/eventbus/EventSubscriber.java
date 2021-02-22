package kueres.eventbus;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;

import kueres.utility.Utility;

public abstract class EventSubscriber {

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	private EventConsumer eventConsumer;

	private Queue queue;
	private Binding binding;

	protected String identifier;
	protected String routingKey;

	public abstract void init();

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

	protected void stopReceivingEvents() {
		
		Utility.LOG.trace("EventSubscriber.stopReceivingEvents called");

		this.eventConsumer.unsubscribe(this.identifier);

		amqpAdmin.removeBinding(this.binding);
		amqpAdmin.deleteQueue(this.queue.getName());

	}

	public String getIdentifier() { return this.identifier; }

	public String getRoutingKey() { return this.routingKey; }

}
