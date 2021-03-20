package kueres.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import kueres.event.EventType;
import kueres.eventbus.EventConsumer;
import kueres.eventbus.EventSubscriber;
import kueres.query.EntitySpecification;
import kueres.utility.Utility;

/**
 * 
 * The BaseService provides the services needed by a BaseController.
 * To operate correctly a BaseEntity and its BaseRepository are needed.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 22, 2021
 *
 */

public abstract class BaseService<E extends BaseEntity<E>, R extends BaseRepository<E>> extends EventSubscriber {
	
	/**
	 * The BaseRepository of the services' BaseEntity.
	 */
	@Autowired
	protected R repository;
	
	/**
	 * Find all entities of the services' BaseEntity-type.
	 * The result can filtered, sorted and paged.
	 * @param specification - filter for the result.
	 * @param pageable - sort and pagination for the result.
	 * @return The result as a page.
	 */
	public Page<E> findAll(EntitySpecification<E> specification, Pageable pageable) {
		
		Utility.LOG.trace("BaseService.findAll called.");
		
		Page<E> page = repository.findAll(specification, pageable);
		
		EventConsumer.sendEvent("BaseService.findAll", EventType.READ.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(page));
		
		return page;
		
	}
	
	/**
	 * Find an entity of the services' BaseEntity-type by its identifier.
	 * @param id - the entity's identifier.
	 * @return The entity with the given identifier.
	 * @throws ResourceNotFoundException if there is no entity with the specified identifier.
	 */
	public E findById(long id) {
		
		Utility.LOG.trace("BaseService.findById called.");
		
		E entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		EventConsumer.sendEvent("BaseService.findById", EventType.READ.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(entity));
		
		return entity;
		
	}
	
	/**
	 * Create an entity of the services' BaseEntity-type.
	 * @param entity - the entity that should be created. This entity can not have an identifier.
	 * @return The created entity. This contains the entity's identifier.
	 */
	public E create(E entity) {
		
		Utility.LOG.trace("BaseService.create called.");
			
		E savedEntity = repository.save(entity);
		
		EventConsumer.sendEvent("BaseService.create", EventType.CREATE.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(savedEntity));
		
		return savedEntity;
		
	}
	
	/**
	 * Update an entity of the services' BaseEntity-type by its identifier.
	 * Fields that are not populated in the updated data will not be changed.
	 * @param id - the identifier of the entity that should be updated.
	 * @param details - the updated data.
	 * @return The updated entity.
	 * @throws JsonProcessingException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws JsonMappingException 
	 * @throws ResourceNotFoundException if there is no entity with the specified identifier.
	 */
	public E update(long id, String detailsJSON) throws JsonMappingException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
		
		Utility.LOG.trace("BaseService.update called.");
		
		E entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		entity.applyPatch(detailsJSON);
		final E updatedEntity = repository.save(entity);
		
		EventConsumer.sendEvent("BaseService.update", EventType.UPDATE.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(updatedEntity));
		
		return updatedEntity;
		
	}
	
	/**
	 * Delete an entity of the services' BaseEntity-type by its identifier.
	 * @param id - the identifier of the entity that should be deleted.
	 * @return The entity that was deleted.
	 * @throws ResourceNotFoundException if there is no entity with the specified identifier.
	 */
	public E delete(long id) {
		
		Utility.LOG.trace("BaseService.delete called.");
		
		E entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		repository.delete(entity);
		
		EventConsumer.sendEvent("BaseService.delete", EventType.DELETE.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(entity));
		
		return entity;
		
	}
	
	@SuppressWarnings("unchecked")
	public Class<E> getEntityClass() {
		return (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
}
