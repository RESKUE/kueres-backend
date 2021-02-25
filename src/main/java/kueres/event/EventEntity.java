package kueres.event;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import kueres.base.BaseEntity;
import kueres.utility.Utility;

/**
 * 
 * The EventEntity is representation of events from the eventbus.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

@Entity
public class EventEntity extends BaseEntity<EventEntity> {
	
	/**
	 * The events message.
	 */
	@Column(name = "message", nullable = false, columnDefinition="TEXT")
	private String message = "";
	public static final String MESSAGE = "message";
	public String getMessage() { return this.message; }
	public void setMessage(String message) { this.message = message; }
	
	/**
	 * The type of event. See kueres.event.EventType for all types.
	 */
	@Column(name = "type", nullable = false)
	private int type = 0;
	public static final String TYPE = "type";
	public int getType() { return this.type; }
	public void setType(int type) { this.type = type; }
	
	/**
	 * The identifier of the event sender.
	 */
	@Column(name = "sender", nullable = false)
	private String sender = "";
	public static final String SENDER = "sender";
	public String getSender() { return this.sender; }
	public void setSender(String sender) { this.sender = sender; }
	
	/**
	 * JSON representation of the entity affected by the event.
	 */
	@Column(name = "entityJSON", nullable = false, columnDefinition="TEXT")
	private String entityJSON = "";
	public static final String ENTITY_JSON = "entityJSON";
	public String getEntityJSON() { return this.entityJSON; }
	public void setEntityJSON(String entityJSON) { this.entityJSON = entityJSON; }
	
	/**
	 * A timestamp when the event was send.
	 */
	@Column(name = "sendAt", nullable = false)
	private Date sendAt = new Date();
	public static final String SEND_AT = "sendAt";
	public Date getSendAt() { return this.sendAt; }
	public void setSendAt(Date sendAt) { this.sendAt = sendAt; }
	
	@Override
	public void applyPatch(EventEntity details) {
		
		Utility.LOG.error("EventEntities can not be updated");
		throw new UnsupportedOperationException("EventEntities can not be updated!");
		
	}

}
