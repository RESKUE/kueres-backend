package kueres;

import java.io.IOException;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.palantir.docker.compose.DockerComposeExtension;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

public class KueresTestInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

	public static DockerComposeExtension compose = DockerComposeExtension.builder()
			.file("src/test/resources/docker-compose.test.yml")
			.waitingForService("keycloak", HealthChecks.toRespondOverHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT/auth/realms/reskue")))
			.waitingForService("rabbitmq", HealthChecks.toRespondOverHttp(15672, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT")))
			.waitingForService("frost", HealthChecks.toRespondOverHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT/FROST-Server/v1.0")))
			.build();
	
	@Override
	public void initialize(ConfigurableWebApplicationContext applicationContext) {
		
		try {
			
			compose.dockerCompose().up();
			
			System.out.println("STARTING COMPOSE");
			
			boolean keycloakUp = compose.containers().container("keycloak").portIsListeningOnHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT/auth/realms/reskue")).succeeded();
			boolean rabbitmqUp = compose.containers().container("rabbitmq").portIsListeningOnHttp(15672, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT")).succeeded();
			boolean frostUp = compose.containers().container("frost").portIsListeningOnHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT/FROST-Server/v1.0")).succeeded();
			
			while (!keycloakUp || !rabbitmqUp || !frostUp) {
				
				System.out.println("WAITING FOR SERVICES");
				
				Thread.sleep(1000);
				
				keycloakUp = compose.containers().container("keycloak").portIsListeningOnHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT/auth/realms/reskue")).succeeded();
				rabbitmqUp = compose.containers().container("rabbitmq").portIsListeningOnHttp(15672, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT")).succeeded();
				frostUp = compose.containers().container("frost").portIsListeningOnHttp(8080, (port) -> port.inFormat("http://$HOST:$EXTERNAL_PORT/FROST-Server/v1.0")).succeeded();
				
			}
			
			applicationContext.getBeanFactory().registerSingleton("compose", compose);
			
			System.out.println("ALL SERVICES STARTED");
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
