package kueres.query;

import kueres.utility.Utility;

/**
 * 
 * The class representation of a search criteria.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

public class SearchCriteria {

	/**
	 * The name of the entities field used for the search operation.
	 */
	private String key;
	
	/**
	 * The value used by the search operation.
	 */
	private Object value;
	
	/**
	 * The search operation.
	 */
	private SearchOperation operation;
	
	/**
	 * Construct a search criterium from a filter query parameter.
	 * @param filter - the query parameter
	 */
	public SearchCriteria(String filter) {
		
		String[] keyAndValue = filter.split(SearchOperation.getOperationSetRegex());
		if (keyAndValue.length == 2) {
			this.key = keyAndValue[0];
			this.value = keyAndValue[1];
			this.operation = SearchOperation.getOperation(filter.charAt(this.key.length()));
			Utility.LOG.info("key: {}", key);
			Utility.LOG.info("value: {}", value);
			Utility.LOG.info("operation: {}", operation);
			if (operation == null) {
				throw new IllegalArgumentException("Incorrect filter format");
			}
		} else {
			throw new IllegalArgumentException("Incorrect filter format");
		}
		
	}
	
	/**
	 * Construct a search criterium.
	 * @param key - the search criteriums key
	 * @param value - the search criteriums value
	 * @param operation - the search criteriums operation
	 */
	public SearchCriteria(String key, Object value, SearchOperation operation) {
		
		this.key = key;
		this.value = value;
		this.operation = operation;
		
	}
	
	public String getKey() { return this.key; }
	public Object getValue() { return this.value; }
	public SearchOperation getOperation() { return this.operation; }
	
	public void setKey(String key) { this.key = key; }
	public void setValue(Object value) { this.value = value; }
	public void setOperation(SearchOperation operation) { this.operation = operation; }
	
}
