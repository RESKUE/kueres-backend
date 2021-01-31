package kueres.eventbus;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

	public static String topicExchange;
	@Value("${kueres.topic-exchange}")
	public void setTopicExchange(String topicExchange) { RabbitMQConfiguration.topicExchange = topicExchange; }
	
	public static String queue;
	@Value("${kueres.default-queue}")
	public void setDefaultQueue(String defaultQueue) { RabbitMQConfiguration.queue = defaultQueue; }
	
	@Bean
	public Queue queue() {
		return new Queue(RabbitMQConfiguration.queue, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(RabbitMQConfiguration.topicExchange);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(RabbitMQConfiguration.queue);
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, EventConsumer consumer) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(RabbitMQConfiguration.queue);
		container.setMessageListener(consumer);
		return container;
	}
	
}
