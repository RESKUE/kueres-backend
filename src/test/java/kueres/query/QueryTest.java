package kueres.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import kueres.KueresTestInitializer;
import kueres.event.EventEntity;

@SpringBootTest
@ContextConfiguration(initializers = KueresTestInitializer.class)
@TestPropertySource(locations="classpath:test.properties")
@TestInstance(Lifecycle.PER_CLASS)
public class QueryTest {
	
	@Test
	public void queryOperationMapping() {
		
		String[] operationSet = SearchOperation.OPERATION_SET;
		
		assertThat(operationSet.length).isEqualTo(SearchOperation.values().length);
		
		for (int i = 0; i < operationSet.length; i++) {
			
			assertThat(operationSet[i].length()).isEqualTo(1);
			
			char symbol = operationSet[i].charAt(0);
			SearchOperation operation = SearchOperation.getOperation(symbol);
			
			assertThat(SearchOperation.values()[i]).isEqualTo(operation);
			
		}
		
	}
	
	@Test
	public void operationSetRegex() {
		
		String operationSetRegex = SearchOperation.getOperationSetRegex();
		
		String[] operationSet = SearchOperation.OPERATION_SET;
		
		assertThat(operationSetRegex.length()).isEqualTo(2 * operationSet.length - 1);
		
		int charCounter = 0;
		for (int i = 0; i < operationSetRegex.length(); i++) {
			
			if ((i % 2) == 0) {
				
				assertThat(operationSet[charCounter].length()).isGreaterThan(0);
				assertThat(operationSetRegex.charAt(i)).isEqualTo(operationSet[charCounter].charAt(0));
				charCounter++;
				
			} else {
				
				assertThat(operationSetRegex.charAt(i)).isEqualTo('|');
				
			}
			
		}
		
	}
	
	@Test
	public void constructSearchCriteria() {
		
		assertThat(SearchOperation.values().length).isGreaterThan(0);
		
		String key = "test";
		Object value = "0";
		SearchOperation operation = SearchOperation.values()[0];
		
		SearchCriteria criteria = new SearchCriteria(key, value, operation);
		
		String constructedKey = (String) ReflectionTestUtils.getField(criteria, "key");
		Object constructedValue = ReflectionTestUtils.getField(criteria, "value");
		SearchOperation constructedOperation = (SearchOperation) ReflectionTestUtils.getField(criteria, "operation");
		
		assertThat(constructedKey).isEqualTo(key);
		assertThat(constructedValue).isEqualTo(value);
		assertThat(constructedOperation).isEqualTo(operation);
		
		String[] operationSet = SearchOperation.OPERATION_SET;
		
		assertThat(operationSet.length).isEqualTo(SearchOperation.values().length);
		
		for (int i = 0; i < operationSet.length; i++) {
			
			String filter = key + operationSet[i] + value;
			SearchCriteria criteriaFromFilter = new SearchCriteria(filter);
			
			constructedKey = (String) ReflectionTestUtils.getField(criteriaFromFilter, "key");
			constructedValue = ReflectionTestUtils.getField(criteriaFromFilter, "value");
			constructedOperation = (SearchOperation) ReflectionTestUtils.getField(criteriaFromFilter, "operation");
			
			assertThat(constructedKey).isEqualTo(key);
			assertThat(constructedValue).isEqualTo(value);
			assertThat(constructedOperation).isEqualTo(SearchOperation.values()[i]);
			
		}
		
	}
	
	@Test
	public void searchCriteriaGetterSetter() {
		
		assertThat(SearchOperation.values().length).isGreaterThan(1);
		
		String key = "test";
		Object value = "0";
		SearchOperation operation = SearchOperation.values()[0];
		
		SearchCriteria criteria = new SearchCriteria(key, value, operation);
		
		String constructedKey = (String) ReflectionTestUtils.getField(criteria, "key");
		Object constructedValue = ReflectionTestUtils.getField(criteria, "value");
		SearchOperation constructedOperation = (SearchOperation) ReflectionTestUtils.getField(criteria, "operation");
		
		String getKey = criteria.getKey();
		Object getValue = criteria.getValue();
		SearchOperation getOperation = criteria.getOperation();
		
		assertThat(getKey).isEqualTo(constructedKey);
		assertThat(getValue).isEqualTo(constructedValue);
		assertThat(getOperation).isEqualTo(constructedOperation);
		
		String newKey = "key";
		Object newValue = 1;
		SearchOperation newOperation = SearchOperation.values()[1];
		
		criteria.setKey(newKey);
		criteria.setValue(newValue);
		criteria.setOperation(newOperation);
		
		getKey = criteria.getKey();
		getValue = criteria.getValue();
		getOperation = criteria.getOperation();
		
		assertThat(getKey).isEqualTo(newKey);
		assertThat(getValue).isEqualTo(newValue);
		assertThat(getOperation).isEqualTo(newOperation);
		
	}
	
	@Test
	@Disabled("not finished")
	public void sortBuilderPageable() {
		
		Pageable defaultPageable = SortBuilder.buildPageable(Optional.ofNullable(null), Optional.ofNullable(null), Optional.ofNullable(null));
		
		assertThat(defaultPageable.getPageNumber()).isEqualTo(0);
		assertThat(defaultPageable.getPageSize()).isEqualTo(25);
		assertThat(defaultPageable.getSort()).isEqualTo(Sort.unsorted());
		
		String field = "a";
		Sort.Direction direction = Sort.Direction.ASC;
		String[] sort = new String[] {field + ";" + direction};
		int pageNumber = 1;
		int pageSize = 10;
		
		Pageable pageable = SortBuilder.buildPageable(Optional.of(sort), Optional.of(pageNumber), Optional.of(pageSize));
		
		assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
		assertThat(pageable.getPageSize()).isEqualTo(pageSize);
		
		assertThat(pageable.getSort()).isNotEqualTo(Sort.unsorted());
		Sort sortObject = pageable.getSort();
		assertThat(sortObject.getOrderFor(field)).isEqualTo(direction);
		
	}
	
	@Test
	@Disabled("not finished")
	public void sortBuilderArray() {
		
		String field1 = "a";
		Sort.Direction direction1 = Sort.Direction.ASC;
		
		String field2 = "b";
		Sort.Direction direction2 = Sort.Direction.DESC;
		
		String[] sort = new String[] {field1 + ";" + direction1, field2 + ";" + direction2};
		
		Sort sortObject = SortBuilder.buildSort(sort);
		
		assertThat(sortObject.getOrderFor(field1).isAscending()).isEqualTo(direction1.isAscending());
		assertThat(sortObject.getOrderFor(field2).isDescending()).isEqualTo(direction2.isDescending());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void entitySpecification() {
		
		EntitySpecification<EventEntity> specification = new EntitySpecification<EventEntity>();

		List<SearchCriteria> params = (List<SearchCriteria>) ReflectionTestUtils.getField(specification, "params");
		
		assertThat(params).isEmpty();
		
		assertThat(SearchOperation.values().length).isGreaterThan(0);
		assertThat(SearchOperation.OPERATION_SET.length).isEqualTo(SearchOperation.values().length);
		
		String key = "test";
		Object value = "0";
		SearchOperation operation = SearchOperation.values()[0];
		String operationSymbol = SearchOperation.OPERATION_SET[0];
		String[] filter = new String[] {key + operationSymbol + value};
		specification = new EntitySpecification<EventEntity>(filter);
		
		params = (List<SearchCriteria>) ReflectionTestUtils.getField(specification, "params");
		
		assertThat(params.size()).isEqualTo(1);
		
		SearchCriteria criteria = params.get(0);
		
		assertThat(criteria.getKey()).isEqualTo(key);
		assertThat(criteria.getValue()).isEqualTo(value);
		assertThat(criteria.getOperation()).isEqualTo(operation);
		
		specification.add(criteria);
		
		params = (List<SearchCriteria>) ReflectionTestUtils.getField(specification, "params");
		
		assertThat(params.size()).isEqualTo(2);
		assertThat(params.get(1)).isEqualTo(criteria);
		
	}
	
}