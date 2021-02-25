package kueres.utility;

import kueres.event.EventEntity;

/**
 * 
 * This executor is used to customize how events are logged.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 22, 2021
 *
 */

public interface ReceiveMessageExecutor {

	/**
	 * Implement this to customize how events are logged.
	 * @param event - the event that should be logged.
	 */
	public void execute(EventEntity event);
	
}
