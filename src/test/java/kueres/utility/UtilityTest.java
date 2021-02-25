package kueres.utility;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import kueres.KueresTestInitializer;
import kueres.KueresTestTerminator;

@SpringBootTest
@ContextConfiguration(initializers = KueresTestInitializer.class, classes = KueresConfigurationProperties.class)
@Import(KueresTestTerminator.class)
@TestPropertySource(locations="classpath:test.properties")
@TestInstance(Lifecycle.PER_CLASS)
public class UtilityTest {
	
	@Autowired
	private KueresConfigurationProperties config;
	
	@Test
	public void configGettersAndSetters() {
		
		String originalMediaDir = (String) ReflectionTestUtils.getField(this.config, "mediaDir");
		String originalNominatimUrl = (String) ReflectionTestUtils.getField(this.config, "nominatimUrl");
		String originalFrostUrl = (String) ReflectionTestUtils.getField(this.config, "frostUrl");
		
		String getMediaDir = config.getMediaDir();
		String getNominatimUrl = config.getNominatimUrl();
		String getFrostUrl = config.getFrostUrl();
		
		assertThat(getMediaDir).isEqualTo(originalMediaDir);
		assertThat(getNominatimUrl).isEqualTo(originalNominatimUrl);
		assertThat(getFrostUrl).isEqualTo(originalFrostUrl);
		
		String testValue = "testValue";
		
		config.setMediaDir(testValue);
		config.setNominatimUrl(testValue);
		config.setFrostUrl(testValue);
		
		String setMediaDir = (String) ReflectionTestUtils.getField(this.config, "mediaDir");
		String setNominatimUrl = (String) ReflectionTestUtils.getField(this.config, "nominatimUrl");
		String setFrostUrl = (String) ReflectionTestUtils.getField(this.config, "frostUrl");
		
		assertThat(setMediaDir).isEqualTo(testValue);
		assertThat(setNominatimUrl).isEqualTo(testValue);
		assertThat(setFrostUrl).isEqualTo(testValue);
		
		config.setMediaDir(originalMediaDir);
		config.setNominatimUrl(originalNominatimUrl);
		config.setFrostUrl(originalFrostUrl);
		
	}
	
}
