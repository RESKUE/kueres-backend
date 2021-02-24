package kueres;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.palantir.docker.compose.DockerComposeExtension;

@TestConfiguration
public class KueresTestTerminator {

	@Autowired
	private DockerComposeExtension compose;
	
	private static boolean stopping = false;
	
	@Bean
    public KueresTestTerminator getKueresTestTerminator() {
        return new KueresTestTerminator();
    }
	
    @PreDestroy
    public void onDestroy() throws Exception {
    	if (!stopping) {
    		stopping = true;
    		try {
        		System.out.println("STOPPING SERVICES");
    			compose.dockerCompose().down();
    			System.out.println("ALL SERVICES STOPPED");
    		} catch (IOException | InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    }
	
}