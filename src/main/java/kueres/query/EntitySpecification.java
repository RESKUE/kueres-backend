package kueres.query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import kueres.base.BaseEntity;

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
			case IN:
				predicates[i] = criteriaBuilder.isMember(this.params.get(i).getValue(), root.get(this.params.get(i).getKey()));
				break;
			case NOT_IN:
				predicates[i] = criteriaBuilder.isNotMember(this.params.get(i).getValue(), root.get(this.params.get(i).getKey()));
				break;
			}
		}
		
		return criteriaBuilder.and(predicates);
		
	}
	
}
