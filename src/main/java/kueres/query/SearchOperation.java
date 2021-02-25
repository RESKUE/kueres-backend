package kueres.query;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 
 * A list of all supported search/filter operations
 * and their symbols.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

public enum SearchOperation {

	/**
	 * Search entities where a field is greater than some value.
	 */
	GREATER_THAN,
	
	/**
	 * Search entities where a field is less than some value.
	 */
    LESS_THAN,
    
    /**
     * Search entities where a field is not equal to some value.
     */
    NOT_EQUAL,
    
    /**
     * Search entities where a field is equal to some value.
     */
    EQUAL,
    
    /**
     * Search entities where a String field contains some other String.
     */
    MATCH,
    
    /**
     * Search entities where a Collection field contains some value.
     */
    IN,
    
    /**
     * Search entities where a Collection field does not contain some value.
     */
    NOT_IN;
    
	/**
	 * The symbols for each search operation-
	 */
    public static final String[] OPERATION_SET = {
    		">",
    		"<",
    		"!",
    		"=",
    		"~",
    		"§",
    		"%"};
    
    public static String getOperationSetRegex() {
    	
    	return Arrays.stream(OPERATION_SET).collect(Collectors.joining("|"));
    	
    }
    
    /**
     * The mapping between search operations and their symbols.
     * GREATER_THAN: {@literal >}
     * LESS_THAN: {@literal <}
     * NOT_EQUAL: !
     * EQUAL: =
     * MATCH: ~
     * IN: §
     * NOT_IN: %
     * @param operation - the symbol that should be mapped
     * @return The search operation for the symbol.
     */
	public static SearchOperation getOperation(char operation) {
		
		switch (operation) {
			case '>': return GREATER_THAN;
			case '<': return LESS_THAN;
			case '!': return NOT_EQUAL;
			case '=': return EQUAL;
			case '~': return MATCH;
			case '§': return IN;
			case '%': return NOT_IN;
			default: return null;
		}
		
	}
	
}
