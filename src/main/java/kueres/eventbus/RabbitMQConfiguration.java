package kueres.eventbus;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * Configuration for the connection between the Spring Boot server and the RabbitMQ service.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0.0
 * @since Apr 26, 2021
 *
 */

@Configuration
public class RabbitMQConfiguration {

	/**
	 * The RabbitMQ topic exchange used by the eventbus.
	 */
	public static final String TOPIC_EXCHANGE = "kueres-events";
	
	/**
	 * The RabbitMQ queue used by the eventbus.
	 */
	public static final String DEFAULT_QUEUE = "event-consumer";
	
	@Bean
	public Queue queue() {
		return new Queue(RabbitMQConfiguration.DEFAULT_QUEUE, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(RabbitMQConfiguration.TOPIC_EXCHANGE);
	}

	/**
	 * Bind the eventbus topic exchange to the eventbus queue.
	 * @param queue - the eventbus queue
	 * @param exchange - eventbus topic exchange
	 * @return The binding between topic exchange and queue.
	 */
	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(RabbitMQConfiguration.DEFAULT_QUEUE);
	}

	/**
	 * Set the EventConsumer as the eventbus' listener.
	 * @param connectionFactory - the ConnectionFactory used to connect to RabbitMQ
	 * @param consumer - the EventConsumer
	 * @return A MessageListenerContainer.
	 */
	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, EventConsumer consumer) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(RabbitMQConfiguration.DEFAULT_QUEUE);
		container.setMessageListener(consumer);
		return container;
	}
	
}
