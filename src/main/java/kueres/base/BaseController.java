package kueres.base;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kueres.query.EntitySpecification;
import kueres.query.SortBuilder;
import kueres.utility.Utility;

/**
 * 
 * The BaseController provides basic CRUD functionality for a BaseEntity.
 * To operate correctly it needs a BaseEntity and its BaseRepository and BaseService.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 22, 2021
 *
 */

public abstract class BaseController<E extends BaseEntity<E>, R extends BaseRepository<E>, S extends BaseService<E, R>> {

	/**
	 * The prefix for all BaseController routes.
	 */
	public static final String API_ENDPOINT = "/api";
	
	/**
	 * The service of the controllers BaseEntity-type.
	 */
	@Autowired
	protected S service;
	
	/**
	 * Find all entities of the controllers BaseEntity-type.
	 * The result can filtered, sorted and paged.
	 * <p>
	 * See kueres.query.SearchCriteria for filter syntax.
	 * <p>
	 * See kueres.query.SortBuilder for sort syntax.
	 * 
	 * @param filter - the filter options.
	 * @param sort - the sort options.
	 * @param page - the number of the page used for pagination.
	 * @param size - the size of the page used for pagination.
	 * @return The result as a page.
	 */
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
	
	/**
	 * Find an entity of the controllers BaseEntity-type by its identifier.
	 * @param id - the entity's identifier.
	 * @return The entity with the given identifier.
	 */
	@GetMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed({"administrator", "helper"})
	public ResponseEntity<E> findById(@PathVariable(value = BaseEntity.ID) long id) {
		
		Utility.LOG.trace("BaseController.findById called.");
		
		E entity = service.findById(id);
		return ResponseEntity.ok().body(entity);
		
	}
	
	/**
	 * Create an entity of the controllers BaseEntity-type.
	 * @param request - the HTTP request received by the servlet
	 * @param response - the HTTP response object for the servlet
	 * @return The created entity. This contains the entity's identifier.
	 * @throws SecurityException if the JSON string can not be processed
	 * @throws NoSuchMethodException if the JSON string can not be processed
	 * @throws InvocationTargetException if the JSON string can not be processed
	 * @throws IllegalArgumentException if the JSON string can not be processed
	 * @throws IllegalAccessException if the JSON string can not be processed
	 * @throws InstantiationException if the JSON string can not be processed
	 * @throws IOException if the JSON string can not be processed
	 */
	@PostMapping()
	@RolesAllowed("administrator")
	public ResponseEntity<E> create(HttpServletRequest request, HttpServletResponse response) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		
		Utility.LOG.trace("BaseController.create called.");
		
		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		
		Class<E> entityClass = this.service.getEntityClass();
		E entity = entityClass.getDeclaredConstructor().newInstance();
		entity = BaseEntity.createEntityFromJSON(body, entity.getUpdateableFields(), entityClass);
		
		E created = this.service.create(entity);
		return ResponseEntity.ok().body(created);
		
	}
	
	/**
	 * Update an entity of the controllers BaseEntity-type by its identifier.
	 * Fields that are not populated in the updated data will not be changed.
	 * @param id - the identifier of the entity that should be updated.
	 * @param request - the HTTP request received by the servlet
	 * @param response - the HTTP response object for the servlet
	 * @return The updated entity.
	 * @throws IOException if the JSON string can not be processed
	 * @throws SecurityException if the JSON string can not be processed
	 * @throws NoSuchMethodException if the JSON string can not be processed
	 * @throws InvocationTargetException if the JSON string can not be processed
	 * @throws IllegalArgumentException if the JSON string can not be processed
	 * @throws IllegalAccessException if the JSON string can not be processed
	 * @throws InstantiationException if the JSON string can not be processed
	 */
	@PutMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public ResponseEntity<E> update(
			@PathVariable(value = BaseEntity.ID) long id,
			HttpServletRequest request, 
			HttpServletResponse response) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		Utility.LOG.trace("BaseController.update called.");
		
		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		
		E updated = this.service.update(id, body);
		return ResponseEntity.ok().body(updated);
		
	}
	
	/**
	 * Delete an entity of the controllers BaseEntity-type by its identifier.
	 * @param id - the identifier of the entity that should be deleted.
	 * @return The entity that was deleted.
	 */
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
