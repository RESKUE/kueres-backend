package kueres.event;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	public ResponseEntity<EventEntity> create(HttpServletRequest request, HttpServletResponse response) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		
		Utility.LOG.trace("EventController.create called");
		
		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));		
		EventEntity event = EventEntity.createEntityFromJSON(body, new EventEntity().getUpdateableFields(), EventEntity.class);
		
		EventConsumer.sendEvent(event);
		
		return ResponseEntity.ok().body(event);
		
	}
	
	/**
	 * EventEntities can not be updated manually.
	 */
	@Override
	@PutMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public ResponseEntity<EventEntity> update(
			@PathVariable(value = BaseEntity.ID) long id,
			HttpServletRequest request, 
			HttpServletResponse response) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		Utility.LOG.error("EventEntities can not be updated");
		throw new UnsupportedOperationException("EventEntities can not be updated!");
		
	}
	
}
