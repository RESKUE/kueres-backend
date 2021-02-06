package kueres.utility;

import org.springframework.amqp.core.Message;

public interface ReceiveMessageExecutor {

	public void execute(Message messageObject, String message, String senderIdentifier);
	
}
