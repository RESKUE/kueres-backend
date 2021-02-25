package kueres.event;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kueres.base.BaseController;
import kueres.base.BaseEntity;
import kueres.eventbus.EventConsumer;
import kueres.utility.Utility;

/**
 * 
 * The EventController provides API functions for EventEntities.
 * These functions are:
 * - sending events to the eventbus
 * - finding all events
 * - finding a specific event
 * - deleting an event
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

@RestController
@RequestMapping(BaseController.API_ENDPOINT + EventController.ROUTE)
public class EventController extends BaseController<EventEntity, EventRepository, EventService> {

	/**
	 * The API route for EventEntities.
	 */
	public static final String ROUTE = "/event";
	
	@Autowired
	protected EventService service;
	
	/**
	 * Send an event to the eventbus.
	 */
	@Override
	@PostMapping()
	@RolesAllowed("administrator")
	public ResponseEntity<EventEntity> create(@Valid @RequestBody EventEntity event) {
		
		Utility.LOG.trace("EventController.create called");
		
		EventConsumer.sendEvent(event);
		
		return ResponseEntity.ok().body(event);
		
	}
	
	/**
	 * EventEntities can not be updated manually.
	 */
	@Override
	@PutMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public ResponseEntity<EventEntity> update(@PathVariable(value = BaseEntity.ID) long id, @Valid @RequestBody EventEntity details) {
		
		Utility.LOG.error("EventEntities can not be updated");
		throw new UnsupportedOperationException("EventEntities can not be updated!");
		
	}
	
}
