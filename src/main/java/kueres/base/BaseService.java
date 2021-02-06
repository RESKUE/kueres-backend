package kueres.base;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import kueres.event.EventEntity;
import kueres.eventbus.EventSubscriber;
import kueres.eventbus.RabbitMQConfiguration;
import kueres.query.EntitySpecification;

public abstract class BaseService<E extends BaseEntity<E>, R extends BaseRepository<E>> extends EventSubscriber {

	@Autowired
	protected RabbitTemplate rabbitTemplate;
	
	protected ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	@Autowired
	protected R repository;
	
	@SuppressWarnings("unchecked")
	public E getEntityFromJSON(String json) throws JsonMappingException, JsonProcessingException  {
		return new ObjectMapper().readValue(
				json, 
				(Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	};
	
	protected void sendEvent(String message, int type, String sender, String entityJSON) throws AmqpException, JsonProcessingException {
		
		EventEntity event = new EventEntity();
		event.setMessage(message);
		event.setType(type);
		event.setSender(sender);
		event.setEntityJSON(entityJSON);
		
		rabbitTemplate.convertAndSend(
				RabbitMQConfiguration.TOPIC_EXCHANGE,
				RabbitMQConfiguration.DEFAULT_QUEUE,
				this.objectWriter.writeValueAsString(event));
	}
	
	public List<E> findAll(EntitySpecification<E> specification, Sort sort, Pageable pageable) {
		
		if (sort != Sort.unsorted() && pageable != Pageable.unpaged()) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}
		
		if (pageable != Pageable.unpaged()) {
			return repository.findAll(specification, pageable).getContent();			
		} else if (sort != Sort.unsorted()) {
			return repository.findAll(specification, sort);
		}
		return repository.findAll(specification);
		
	}
	
	public E findById(long id) throws ResourceNotFoundException {
		
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found: " + id));
		
	}
	
	public E create(E entity) {
		
		return repository.save(entity);
		
	}
	
	public E update(long id, E details) throws ResourceNotFoundException {
		
		E entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found: " + id));
		entity.applyPatch(details);
		final E updatedEntity = repository.save(entity);
		return updatedEntity;
		
	}
	
	public E delete(long id) throws ResourceNotFoundException {
		
		E entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found: " + id));
		repository.delete(entity);
		return entity;
		
	}
	
}
