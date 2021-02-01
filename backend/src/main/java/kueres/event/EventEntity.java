package kueres.event;

import java.util.Date;

import javax.persistence.Column;

import kueres.base.BaseEntity;

public class EventEntity extends BaseEntity<EventEntity> {
	
	@Column(name = "message", nullable = false)
	private String message;
	public static final String MESSAGE = "message";
	public String getMessage() { return this.message; }
	public void setMessage(String message) { this.message = message; }
	
	@Column(name = "type", nullable = false)
	private int type = 0;
	public static final String TYPE = "type";
	public int getType() { return this.type; }
	public void setType(int type) { this.type = type; }
	
	@Column(name = "sender", nullable = false)
	private String sender;
	public static final String SENDER = "sender";
	public String getSender() { return this.sender; }
	public void setSender(String sender) { this.sender = sender; }
	
	@Column(name = "entity", nullable = false)
	private Object entity;
	public static final String ENTITY = "entity";
	public Object getEntity() { return this.entity; }
	public void setEntity(Object entity) { this.entity = entity; }
	
	@Column(name = "sendAt", nullable = false)
	private Date sendAt;
	public static final String SEND_AT = "sendAt";
	public Date getSendAt() { return this.sendAt; }
	public void setSendAt(Date sendAt) { this.sendAt = sendAt; }
	
	@Override
	public void applyPatch(EventEntity details) {
		String message = details.getMessage();
		int type = details.getType();
		String sender = details.getSender();
		Object entity = details.getEntity();
		Date sendAt = details.getSendAt();
		if (message != null) {
			this.setMessage(message);
		}
		this.setType(type);
		if (sender != null) {
			this.setSender(sender);
		}
		if (entity != null) {
			this.setEntity(entity);
		}
		if (sendAt != null) {
			this.setSendAt(sendAt);
		}
	}

}
