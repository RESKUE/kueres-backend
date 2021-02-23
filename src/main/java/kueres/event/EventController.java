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

@RestController
@RequestMapping(BaseController.API_ENDPOINT + EventController.ROUTE)
public class EventController extends BaseController<EventEntity, EventRepository, EventService> {

	public static final String ROUTE = "/event";
	
	@Autowired
	protected EventService service;
	
	@Override
	@PostMapping()
	@RolesAllowed("administrator")
	public ResponseEntity<EventEntity> create(@Valid @RequestBody EventEntity event) {
		
		Utility.LOG.trace("EventController.create called");
		
		EventConsumer.sendEvent(event);
		
		return ResponseEntity.ok().body(event);
		
	}
	
	@Override
	@PutMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public ResponseEntity<EventEntity> update(@PathVariable(value = BaseEntity.ID) long id, @Valid @RequestBody EventEntity details) {
		
		Utility.LOG.error("EventEntities can not be updated");
		throw new UnsupportedOperationException("EventEntities can not be updated!");
		
	}
	
}
