package kueres.query;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import kueres.base.BaseEntity;
import kueres.utility.Utility;

/**
 * 
 * Construct a Predicate from search criteria.
 * This EntitySpecification can be directly passed to repository requests.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

public class EntitySpecification<E extends BaseEntity<E>> implements Specification<E> {

	private static final long serialVersionUID = -5534647128088932132L;
	
	private List<SearchCriteria> params;
	
	/**
	 * Construct an empty entity specification.
	 */
	public EntitySpecification() {
		
		params = new ArrayList<SearchCriteria>();
		
	}

	/**
	 * Construct an entity specification from a list of filter query parameters.
	 * @param filter - the filter query parameters
	 */
	public EntitySpecification(String[] filter) {
		
		params = new ArrayList<SearchCriteria>();
		for (String searchFilter : filter) {
			this.add(new SearchCriteria(searchFilter));
		}
		
	}
	
	/**
	 * Add a search criterium to the entity specification.
	 * @param criteria - the search criterium to be added
	 */
	public void add(SearchCriteria criteria) {
		
		this.params.add(criteria);
		
	}
	
	/**
	 * Get a java.util.function.Predicate from an EntitySpecification.
	 * This can be used to filter lists.
	 * @param <T> - the type of entity
	 * @param clazz - the class of the entity
	 * @return a java.util.function.Predicate representing an EntitySpecification.
	 */
	public <T extends BaseEntity<T>> java.util.function.Predicate<T> toPredicate(Class<T> clazz) {
		
		return entity -> {
			AtomicReference<Boolean> resultAggregate = new AtomicReference<Boolean>();
			resultAggregate.set(true);
			this.params.forEach((SearchCriteria param) -> {
				
				String field = param.getKey();
				PropertyDescriptor descriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(clazz, field);
				try {
					
					Class<?> fieldClass = descriptor.getPropertyType();
					Object fieldValue = descriptor.getReadMethod().invoke(entity);
					Utility.LOG.info("class: {}", fieldClass);
					boolean result = true;
					switch (param.getOperation()) {
					case GREATER_THAN:
						if (Integer.class.isAssignableFrom(fieldClass)) {
							Integer fieldValueInteger = (Integer) fieldClass.cast(fieldValue);
							Integer paramValueInteger = (Integer) fieldClass.cast(param.getValue());
							result = fieldValueInteger > paramValueInteger;
						} else if (Double.class.isAssignableFrom(fieldClass)) {
							Double fieldValueDouble = (Double) fieldClass.cast(fieldValue);
							Double paramValueDouble = Double.parseDouble((String) param.getValue());
							result = fieldValueDouble > paramValueDouble;
						} else if (Long.class.isAssignableFrom(fieldClass)) {
							Long fieldValueLong = (Long) fieldClass.cast(fieldValue);
							Long paramValueLong = Long.parseLong((String) param.getValue());
							result = fieldValueLong > paramValueLong;
						} else {
							Utility.LOG.info("bad filter");
							throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
						}
						break;
					case LESS_THAN:
						if (Integer.class.isAssignableFrom(fieldClass)) {
							Integer fieldValueInteger = (Integer) fieldClass.cast(fieldValue);
							Integer paramValueInteger = (Integer) fieldClass.cast(param.getValue());
							result = fieldValueInteger < paramValueInteger;
						} else if (Double.class.isAssignableFrom(fieldClass)) {
							Double fieldValueDouble = (Double) fieldClass.cast(fieldValue);
							Double paramValueDouble = Double.parseDouble((String) param.getValue());
							result = fieldValueDouble < paramValueDouble;
						} else if (Long.class.isAssignableFrom(fieldClass)) {
							Long fieldValueLong = (Long) fieldClass.cast(fieldValue);
							Long paramValueLong = Long.parseLong((String) param.getValue());
							result = fieldValueLong < paramValueLong;
						} else {
							Utility.LOG.info("bad filter");
							throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
						}
						break;
					case NOT_EQUAL:
						if (Integer.class.isAssignableFrom(fieldClass)) {
							Integer fieldValueInteger = (Integer) fieldClass.cast(fieldValue);
							Integer paramValueInteger = (Integer) fieldClass.cast(param.getValue());
							result = fieldValueInteger != paramValueInteger;
						} else if (Double.class.isAssignableFrom(fieldClass)) {
							Double fieldValueDouble = (Double) fieldClass.cast(fieldValue);
							Double paramValueDouble = Double.parseDouble((String) param.getValue());
							result = fieldValueDouble != paramValueDouble;
						} else if (String.class.isAssignableFrom(fieldClass)) {
							String fieldValueString = (String) fieldClass.cast(fieldValue);
							String paramValueString = (String) fieldClass.cast(param.getValue());
							result = fieldValueString != paramValueString;
						} else if (Long.class.isAssignableFrom(fieldClass)) {
							Long fieldValueLong = (Long) fieldClass.cast(fieldValue);
							Long paramValueLong = Long.parseLong((String) param.getValue());
							result = fieldValueLong != paramValueLong;
						} else {
							Utility.LOG.info("bad filter");
							throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
						}
						break;
					case EQUAL:
						if (Integer.class.isAssignableFrom(fieldClass)) {
							Integer fieldValueInteger = (Integer) fieldClass.cast(fieldValue);
							Integer paramValueInteger = (Integer) fieldClass.cast(param.getValue());
							result = fieldValueInteger != paramValueInteger;
						} else if (Double.class.isAssignableFrom(fieldClass)) {
							Double fieldValueDouble = (Double) fieldClass.cast(fieldValue);
							Double paramValueDouble = Double.parseDouble((String) param.getValue());
							result = fieldValueDouble == paramValueDouble;
						} else if (String.class.isAssignableFrom(fieldClass)) {
							String fieldValueString = (String) fieldClass.cast(fieldValue);
							String paramValueString = (String) fieldClass.cast(param.getValue());
							result = fieldValueString == paramValueString;
						} else if (Long.class.isAssignableFrom(fieldClass)) {
							Long fieldValueLong = (Long) fieldClass.cast(fieldValue);
							Long paramValueLong = Long.parseLong((String) param.getValue());
							result = fieldValueLong == paramValueLong;
						} else {
							Utility.LOG.info("bad filter");
							throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
						}
						break;
					case MATCH:
						if (String.class.isAssignableFrom(fieldClass)) {
							String fieldValueString = (String) fieldClass.cast(fieldValue);
							String paramValueString = (String) fieldClass.cast(param.getValue());
							result = fieldValueString.contains(paramValueString);
						} else {
							Utility.LOG.info("bad filter");
							throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
						}
						break;
					}
					resultAggregate.set(resultAggregate.get() && result);
					
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					Utility.LOG.info("bad filter");
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
				}
				
			});
			return resultAggregate.get();
		};
		
	}
	
	@Override
	public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		
		Predicate[] predicates = new Predicate[this.params.size()];
		for (int i = 0; i < this.params.size(); i++) {
			switch (this.params.get(i).getOperation()) {
			case GREATER_THAN:
				predicates[i] = criteriaBuilder.greaterThan(root.get(this.params.get(i).getKey()), this.params.get(i).getValue().toString());
				break;
			case LESS_THAN:
				predicates[i] = criteriaBuilder.lessThan(root.get(this.params.get(i).getKey()), this.params.get(i).getValue().toString());
				break;
			case NOT_EQUAL:
				predicates[i] = criteriaBuilder.notEqual(root.get(this.params.get(i).getKey()), this.params.get(i).getValue());
				break;
			case EQUAL:
				predicates[i] = criteriaBuilder.equal(root.get(this.params.get(i).getKey()), this.params.get(i).getValue());
				break;
			case MATCH:
				predicates[i] = criteriaBuilder.like(root.get(this.params.get(i).getKey()), "%" + this.params.get(i).getValue().toString() + "%");
				break;
			}
		}
		
		return criteriaBuilder.and(predicates);
		
	}
	
}
