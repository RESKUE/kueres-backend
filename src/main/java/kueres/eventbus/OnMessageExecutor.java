package kueres.eventbus;

import java.util.Map;

import org.springframework.amqp.core.Message;

/**
 * 
 * This executor is used to customize how events are distributed to EventSubscribers.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

public interface OnMessageExecutor {

	/**
	 * Implement this to customize how events are distributed to EventSubscribers.
	 * @param message - the incoming message
	 * @param subscribers - all subscribers
	 */
	public void execute(Message message, Map<String, String> subscribers);
	
}
