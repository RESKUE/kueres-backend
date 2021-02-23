package kueres.utility;

import kueres.event.EventEntity;

/**
 * 
 * 
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 22, 2021
 *
 */

public interface ReceiveMessageExecutor {

	public void execute(EventEntity event);
	
}
