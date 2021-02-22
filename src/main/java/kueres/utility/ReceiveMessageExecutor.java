package kueres.utility;

import kueres.event.EventEntity;

public interface ReceiveMessageExecutor {

	public void execute(EventEntity event);
	
}
