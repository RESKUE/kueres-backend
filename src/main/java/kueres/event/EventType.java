package kueres.event;

public enum EventType {

	UNDEFINED(0),
	CREATE(1),
	READ(2),
	UPDATE(3),
	DELETE(4);
	
	public final int type;
	
	private EventType(int type) {
		this.type = type;
	}
	
}
