package kueres.error;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kueres.utility.Utility;

@Controller
@RequestMapping(ErrorController.ROUTE)
public class ErrorController {

	public static final String ROUTE = "/error";
	
	@GetMapping()
    public ResponseEntity<String> error(HttpServletRequest request) {
		Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		Object exceptionType = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
		Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		Object servletName = request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME);
		Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		Utility.LOG.info("\n"
				+ "Exception: {}\n"
				+ "ExceptionType: {}\n"
				+ "Message: {}\n"
				+ "RequestUri: {}\n"
				+ "ServletName: {}\n"
				+ "StatusCode: {}\n", exception, exceptionType, message, requestUri, servletName, statusCode);
        return new ResponseEntity<>("An error occurred. Error Code: " + statusCode, HttpStatus.BAD_REQUEST);
    }
	
}
