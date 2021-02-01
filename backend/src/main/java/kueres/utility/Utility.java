package kueres.utility;

import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;
import kueres.eventbus.EventSubscriber;

@Service
public class Utility extends EventSubscriber {

	public static final Logger LOG = (Logger) LoggerFactory.getLogger("logger");
	private final Logger LOG_EVENT = (Logger) LoggerFactory.getLogger("event");
	
	private ReceiveMessageExecutor receiveMessageExecutor = 
			(Message messageObject, String message, String senderIdentifier) -> defaultReceiveMessageExecutor(messageObject, message, senderIdentifier);

	@Override
	@PostConstruct
	public void init() {
		this.identifier = "eventLogger";
		this.routingKey = "eventLogger";
		this.startReceivingEvents();
	}
	
	@RabbitListener(queues = "eventLogger")
	public void receiveMessage(
			Message messageObject,
			@Payload String message,
			@Header("senderIdentifier") String senderIdentifier
			) throws UnsupportedEncodingException {
		this.receiveMessageExecutor.execute(messageObject, message, senderIdentifier);
	}
	
	public void setReceiveMessageExecutor(ReceiveMessageExecutor receiveMessageExecutor) {
		this.receiveMessageExecutor = receiveMessageExecutor;
	}
	
	private void defaultReceiveMessageExecutor(Message messageObject, String message, String senderIdentifier) {
		this.LOG_EVENT.info("\nsender: {}\nmessage: {}\nmessageObject: {}\n\n", senderIdentifier, message, messageObject);
	}
	
}
