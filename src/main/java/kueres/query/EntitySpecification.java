package kueres.query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import kueres.base.BaseEntity;

/*
 * ToDo: test search operations
 */

@SuppressWarnings("serial")
public class EntitySpecification<E extends BaseEntity<E>> implements Specification<E> {

	private List<SearchCriteria> params;

	public EntitySpecification() {
		params = new ArrayList<SearchCriteria>();
	}

	public void add(SearchCriteria criteria) {
		this.params.add(criteria);
	}
	
	@Override
	public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		for (SearchCriteria criteria : this.params) {
			switch (criteria.getOperation()) {
			case GREATER_THAN:
				predicates.add(criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString()));
				break;
			case LESS_THAN:
				predicates.add(criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString()));
				break;
			case NOT_EQUAL:
				predicates.add(criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue().toString()));
				break;
			case EQUAL:
				predicates.add(criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue().toString()));
				break;
			case MATCH:
				predicates.add(criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%"));
				break;
			}
		}
		
		return criteriaBuilder.and((Predicate[]) predicates.toArray());
		
	}

}
