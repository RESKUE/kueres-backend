package kueres.query;

import kueres.utility.Utility;

public class SearchCriteria {

	private String key;
	private Object value;
	private SearchOperation operation;
	
	public SearchCriteria(String filter) {
		String[] keyAndValue = filter.split(SearchOperation.getOperationSetRegex());
		if (keyAndValue.length == 2) {
			this.key = keyAndValue[0];
			this.value = keyAndValue[1];
			this.operation = SearchOperation.getOperation(filter.charAt(this.key.length()));
			Utility.LOG.info("key: {}, value: {}, operation: {}", key, value, operation);
			if (operation == null) {
				throw new IllegalArgumentException("Incorrect filter format");
			}
		} else {
			throw new IllegalArgumentException("Incorrect filter format");
		}
	}
	
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
