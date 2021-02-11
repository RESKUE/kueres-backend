package kueres.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import kueres.base.BaseController;
import kueres.base.BaseEntity;
import kueres.query.EntitySpecification;
import kueres.query.SearchCriteria;
import kueres.query.SortBuilder;
import kueres.utility.Utility;

/*
 * ToDo: add auth
 */

@RestController
@RequestMapping(BaseController.API_ENDPOINT + EventController.ROUTE)
public class EventController {

	public static final String ROUTE = "/event";
	
	@Autowired
	protected EventService service;
	
	@PostMapping("/sendEvent")
	@RolesAllowed({"administrator"})
	public Map<String, Boolean> sendEvent(@Valid @RequestBody EventEntity event) {
		
		try {
			service.sendEvent(event);
		} catch (AmqpException | JsonProcessingException e) {
			Utility.LOG.error("Could not send event: {}", e.getStackTrace().toString());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("send", true);
		return response;
		
	}
	
	@GetMapping()
	@RolesAllowed({"administrator", "helper"})
	public Page<EventEntity> findAll(
			@RequestParam Optional<String> filter,
			@RequestParam Optional<String[]> sort,
			@RequestParam Optional<Integer> page,
			@RequestParam Optional<Integer> size
			) {
		
		EntitySpecification<EventEntity> specification = null;
		if (filter.isPresent()) {
			String[] filters = filter.get().split(",");
			specification = new EntitySpecification<EventEntity>();
			for (String searchFilter : filters) {
				specification.add(new SearchCriteria(searchFilter));
			}
		}
		
		Sort sorting = Sort.unsorted();
		if (sort.isPresent()) {
			sorting = SortBuilder.buildSort(sort.get());
		}
		
		Pageable pageable = Pageable.unpaged();
		if (page.isPresent() && size.isPresent()) {
			pageable = PageRequest.of(page.get(), size.get());
		}
		
//		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);
		
		return service.findAll(specification, pageable);
		
	}
	
	@GetMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed({"administrator", "helper"})
	public ResponseEntity<EventEntity> findById(
			@PathVariable(value = BaseEntity.ID) long id
			) {
		
		EventEntity entity = service.findById(id);
		return ResponseEntity.ok().body(entity);
		
	}
	
	@DeleteMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public Map<String, Boolean> delete(
			@PathVariable(value = BaseEntity.ID) long id
			) {
		
		service.delete(id);
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("deleted", true);
		return response;
		
	}
	
}
