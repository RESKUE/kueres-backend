package kueres.event;

import java.util.Date;

import javax.persistence.Column;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import kueres.base.BaseEntity;
import kueres.base.BaseRepository;

public class EventEntity extends BaseEntity<EventEntity> {
	
	@Column(name = "message", nullable = false)
	private String message;
	public static final String MESSAGE = "message";
	public String getMessage() { return this.message; }
	public void setMessage(String message) { this.message = message; }
	
	@Column(name = "type", nullable = false)
	private int type;
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
		// TODO Auto-generated method stub
		
	}

}
