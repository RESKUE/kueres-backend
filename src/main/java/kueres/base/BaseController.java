package kueres.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import kueres.query.EntitySpecification;
import kueres.query.SearchCriteria;
import kueres.query.SortBuilder;

public abstract class BaseController<E extends BaseEntity<E>, R extends BaseRepository<E>, S extends BaseService<E, R>> {

	public static final String API_ENDPOINT = "/api";
	
	@Autowired
	protected S service;
	
	@GetMapping()
	@RolesAllowed({"administrator", "helper"})
	public Page<E> findAll(
			@RequestParam Optional<String> filter,
			@RequestParam Optional<String[]> sort,
			@RequestParam Optional<Integer> page,
			@RequestParam Optional<Integer> size
			) {
		
		EntitySpecification<E> specification = null;
		if (filter.isPresent()) {
			String[] filters = filter.get().split(",");
			specification = new EntitySpecification<E>();
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
		
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);
		
		return service.findAll(specification, pageable);
		
	}
	
	@GetMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed({"administrator", "helper"})
	public ResponseEntity<E> findById(@PathVariable(value = BaseEntity.ID) long id) {
		
		E entity = service.findById(id);
		return ResponseEntity.ok().body(entity);
		
	}
	
	@PostMapping()
	@RolesAllowed("administrator")
	public E create(@Valid @RequestBody E entity) {
		
		return service.create(entity);
		
	}
	
	@PutMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public ResponseEntity<E> update(@PathVariable(value = BaseEntity.ID) long id, @Valid @RequestBody E details) {
		
		E updatedEntity = service.update(id, details);
		return ResponseEntity.ok().body(updatedEntity);
		
	}
	
	@DeleteMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public Map<String, Boolean> delete(@PathVariable(value = BaseEntity.ID) long id) {
		
		service.delete(id);
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("deleted", true);
		return response;
		
	}
	
	
	
}
