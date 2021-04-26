package kueres.event;

/**
 * 
 * All event types used by the eventbus.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0.0
 * @since Apr 26, 2021
 *
 */

public enum EventType {

	/**
	 * The type of the event is not defined.
	 */
	UNDEFINED(0),
	
	/**
	 * An entity was created.
	 */
	CREATE(1),
	
	/**
	 * An entity was read.
	 */
	READ(2),
	
	/**
	 * And entity was updated.
	 */
	UPDATE(3),
	
	/**
	 * An entity was deleted.
	 */
	DELETE(4);
	
	public final int type;
	
	private EventType(int type) {
		this.type = type;
	}
	
}
