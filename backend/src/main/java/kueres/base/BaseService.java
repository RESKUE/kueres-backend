package kueres.base;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import kueres.eventbus.EventSubscriber;
import kueres.eventbus.RabbitMQConfiguration;
import kueres.query.EntitySpecification;

public abstract class BaseService<E extends BaseEntity<E>, R extends BaseRepository<E>> extends EventSubscriber {

	@Autowired
	protected R repository;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	protected void sendEvent(Object object) {
		rabbitTemplate.convertAndSend(
				RabbitMQConfiguration.topicExchange,
				RabbitMQConfiguration.queue, 
				object,
				m -> {
				    m.getMessageProperties().getHeaders().put("senderIdentifier", this.identifier);
				    m.getMessageProperties().getHeaders().put("message", "");
				    m.getMessageProperties().getHeaders().put("type", 0);
				    m.getMessageProperties().getHeaders().put("receivers", new String[0]);
				    return m;
				});
	}
	
	protected void sendEvent(String message, int type, String sender, String[] receivers, E entity) {
		rabbitTemplate.convertAndSend(
				RabbitMQConfiguration.topicExchange,
				RabbitMQConfiguration.queue,
				entity,
				m -> {
				    m.getMessageProperties().getHeaders().put("senderIdentifier", sender);
				    m.getMessageProperties().getHeaders().put("message", message);
				    m.getMessageProperties().getHeaders().put("type", type);
				    m.getMessageProperties().getHeaders().put("receivers", receivers);
				    return m;
				});
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
