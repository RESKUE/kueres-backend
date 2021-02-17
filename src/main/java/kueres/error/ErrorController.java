package kueres.error;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ErrorController {

	public static final String ROUTE = "/error";
	
	@ExceptionHandler
    public ResponseEntity<String> error(Exception e) throws ResponseStatusException {
		
		String statusCode = "400";
		String exceptionType = "Bad request";
		String message = "No message available";
		String requestUri = "/api";
		if (e instanceof ResponseStatusException) {
			throw (ResponseStatusException) e;
		}
		
		String response = "{\n";
		response += "\t\"timestamp\": \"" + new Timestamp(System.currentTimeMillis()).toInstant() + "\",\n";
		response += "\t\"status\": " + statusCode + ",\n";
		response += "\t\"error\": \"" + exceptionType + "\",\n";
		response += "\t\"message\": \"" + message + "\",\n";
		response += "\t\"path\": \"" + requestUri + "\"\n";
		response += "}";
		
		return ResponseEntity.status(HttpStatus.valueOf(statusCode)).contentType(MediaType.APPLICATION_JSON).body(response);
		
    }
	
}
