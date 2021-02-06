package kueres.eventbus;

import java.util.Map;

import org.springframework.amqp.core.Message;

public interface OnMessageExecutor {

	public void execute(Message message, Map<String, String> subscribers);
	
}
