package kueres.event;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import kueres.base.BaseEntity;
import kueres.utility.Utility;

/**
 * 
 * The EventEntity is representation of events from the eventbus.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0.0
 * @since Apr 26, 2021
 *
 */

@Entity
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id",
		scope = EventEntity.class)
public class EventEntity extends BaseEntity<EventEntity> {
	
	@Override
	public String[] getUpdateableFields() {
		return new String[] {
			EventEntity.MESSAGE,
			EventEntity.TYPE,
			EventEntity.SENDER,
			EventEntity.ENTITY_JSON
		};
	}
	
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
	@Column(name = "sentAt", nullable = false)
	private Date sentAt = new Date();
	public static final String SENT_AT = "sentAt";
	public Date getSentAt() { return this.sentAt; }
	public void setSentAt(Date sentAt) { this.sentAt = sentAt; }
	
	@Override
	public void applyPatch(String json) {
		
		Utility.LOG.error("EventEntities can not be updated");
		throw new UnsupportedOperationException("EventEntities can not be updated!");
		
	}

}
