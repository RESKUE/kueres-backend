package kueres.query;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum SearchOperation {

	GREATER_THAN,
    LESS_THAN,
    NOT_EQUAL,
    EQUAL,
    MATCH,
    IN,
    NOT_IN;
    
    public static final String[] OPERATION_SET = {
    		">",
    		"<",
    		"!",
    		"=",
    		"~",
    		"+",
    		"-"};
    
    public static String getOperationSetRegex() {
    	return Arrays.stream(OPERATION_SET).collect(Collectors.joining("|"));
    }
    
	public static SearchOperation getOperation(char operation) {
		switch (operation) {
			case '>': return GREATER_THAN;
			case '<': return LESS_THAN;
			case '!': return NOT_EQUAL;
			case '=': return EQUAL;
			case '~': return MATCH;
			case '+': return IN;
			case '-': return NOT_IN;
			default: return null;
		}
	}
	
}
