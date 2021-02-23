package kueres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * The main class for KUERES.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0 
 * @since Feb 22, 2021
 *
 */

@SpringBootApplication
public class Application {
	
	/**
	 * The main entrypoint for KUERES.
	 * @param args - arguments for Spring Boot
	 */
	public static void main(String[] args) {
		
		SpringApplication.run(Application.class, args);
		
	}
	
}
