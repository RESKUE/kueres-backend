package kueres.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import kueres.base.BaseController;
import kueres.base.BaseEntity;
import kueres.query.EntitySpecification;
import kueres.query.SearchCriteria;
import kueres.query.SortBuilder;
import kueres.utility.Utility;

@RestController
@RequestMapping(BaseController.API_ENDPOINT + EventController.ROUTE)
public class EventController {

	public static final String ROUTE = "/event";
	
	@Autowired
	protected EventService service;
	
	@PostMapping()
	public Map<String, Boolean> sendEvent(@Valid @RequestBody EventEntity event) throws JsonProcessingException {
		Utility.LOG.info("event entity @controller: {}", event.getEntityJSON());
		service.sendEvent(event);
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("send", true);
		return response;
		
	}
	
	@GetMapping()
	@RolesAllowed({"administrator", "helper"})
	public List<EventEntity> findAll(
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
		
		Pageable pagination = Pageable.unpaged();
		if (page.isPresent() && size.isPresent()) {
			pagination = PageRequest.of(page.get(), size.get());
		}
		
		return service.findAll(specification, sorting, pagination);
		
	}
	
	@GetMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed({"administrator", "helper"})
	public ResponseEntity<EventEntity> findById(
			@PathVariable(value = BaseEntity.ID) long id
			) throws ResourceNotFoundException {
		
		EventEntity entity = service.findById(id);
		return ResponseEntity.ok().body(entity);
		
	}
	
	@DeleteMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public Map<String, Boolean> delete(
			@PathVariable(value = BaseEntity.ID) long id
			) throws Exception {
		
		service.delete(id);
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("deleted", true);
		return response;
		
	}
	
}
