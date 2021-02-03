package kueres.eventbus;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

	public static final String TOPIC_EXCHANGE = "kueres-events";
	public static final String DEFAULT_QUEUE = "event-consumer";
	
	@Bean
	public Queue queue() {
		return new Queue(RabbitMQConfiguration.DEFAULT_QUEUE, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(RabbitMQConfiguration.TOPIC_EXCHANGE);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(RabbitMQConfiguration.DEFAULT_QUEUE);
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, EventConsumer consumer) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(RabbitMQConfiguration.DEFAULT_QUEUE);
		container.setMessageListener(consumer);
		return container;
	}
	
}
