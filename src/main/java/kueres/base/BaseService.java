package kueres.base;

import java.lang.reflect.ParameterizedType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kueres.event.EventType;
import kueres.eventbus.EventConsumer;
import kueres.eventbus.EventSubscriber;
import kueres.query.EntitySpecification;
import kueres.utility.Utility;

public abstract class BaseService<E extends BaseEntity<E>, R extends BaseRepository<E>> extends EventSubscriber {
	
	@Autowired
	protected R repository;
	
	@SuppressWarnings("unchecked")
	public E getEntityFromJSON(String json) throws JsonMappingException, JsonProcessingException  {
		
		Utility.LOG.trace("BaseService.getEntityFromJSON called.");
		
		return new ObjectMapper().readValue(
				json, 
				(Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		
	};
	
	public Page<E> findAll(EntitySpecification<E> specification, Pageable pageable) {
		
		Utility.LOG.trace("BaseService.findAll called.");
		
		Page<E> page = repository.findAll(specification, pageable);
		
		EventConsumer.sendEvent("BaseService.findAll", EventType.READ.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(page));
		
		return page;
		
	}
	
	public E findById(long id) throws ResourceNotFoundException {
		
		Utility.LOG.trace("BaseService.findById called.");
		
		E entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		EventConsumer.sendEvent("BaseService.findById", EventType.READ.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(entity));
		
		return entity;
		
	}
	
	public E create(E entity) {
		
		Utility.LOG.trace("BaseService.create called.");
		
		E savedEntity = repository.save(entity);
		
		EventConsumer.sendEvent("BaseService.create", EventType.CREATE.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(savedEntity));
		
		return savedEntity;
		
	}
	
	public E update(long id, E details) throws ResourceNotFoundException {
		
		Utility.LOG.trace("BaseService.update called.");
		
		E entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		entity.applyPatch(details);
		final E updatedEntity = repository.save(entity);
		
		EventConsumer.sendEvent("BaseService.update", EventType.UPDATE.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(updatedEntity));
		
		return updatedEntity;
		
	}
	
	public E delete(long id) throws ResourceNotFoundException {
		
		Utility.LOG.trace("BaseService.delete called.");
		
		E entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		repository.delete(entity);
		
		EventConsumer.sendEvent("BaseService.delete", EventType.DELETE.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(entity));
		
		return entity;
		
	}
	
}
