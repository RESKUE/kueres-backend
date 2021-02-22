package kueres.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import kueres.query.EntitySpecification;
import kueres.query.SortBuilder;
import kueres.utility.Utility;

public abstract class BaseController<E extends BaseEntity<E>, R extends BaseRepository<E>, S extends BaseService<E, R>> {

	public static final String API_ENDPOINT = "/api";
	
	@Autowired
	protected S service;
	
	@GetMapping()
	@RolesAllowed({"administrator", "helper"})
	public Page<E> findAll(
			@RequestParam Optional<String[]> filter,
			@RequestParam Optional<String[]> sort,
			@RequestParam Optional<Integer> page,
			@RequestParam Optional<Integer> size
			) {
		
		Utility.LOG.trace("BaseController.findAll called.");
		
		EntitySpecification<E> specification = null;
		if (filter.isPresent()) {
			specification = new EntitySpecification<E>(filter.get());
		}
		
		Pageable pageable = SortBuilder.buildPageable(sort, page, size);
	
		return service.findAll(specification, pageable);
		
	}
	
	@GetMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed({"administrator", "helper"})
	public ResponseEntity<E> findById(@PathVariable(value = BaseEntity.ID) long id) {
		
		Utility.LOG.trace("BaseController.findById called.");
		
		E entity = service.findById(id);
		return ResponseEntity.ok().body(entity);
		
	}
	
	@PostMapping()
	@RolesAllowed("administrator")
	public E create(@Valid @RequestBody E entity) {
		
		Utility.LOG.trace("BaseController.create called.");
		
		return service.create(entity);
		
	}
	
	@PutMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public ResponseEntity<E> update(@PathVariable(value = BaseEntity.ID) long id, @Valid @RequestBody E details) {
		
		Utility.LOG.trace("BaseController.update called.");
		
		E updatedEntity = service.update(id, details);
		return ResponseEntity.ok().body(updatedEntity);
		
	}
	
	@DeleteMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public Map<String, Boolean> delete(@PathVariable(value = BaseEntity.ID) long id) {
		
		Utility.LOG.trace("BaseController.delete called.");
		
		service.delete(id);
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("deleted", true);
		return response;
		
	}
	
	
	
}
