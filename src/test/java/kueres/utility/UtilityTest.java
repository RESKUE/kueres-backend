//package kueres.utility;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.util.ReflectionTestUtils;
//
//@SpringBootTest
//@TestInstance(Lifecycle.PER_CLASS)
//@ContextConfiguration(classes = KueresConfigurationProperties.class)
//public class UtilityTest {
//
//	@Autowired
//	private KueresConfigurationProperties config;
//	
//	@Test
//	public void configGettersAndSetters() {
//		
//		String originalMediaDir = (String) ReflectionTestUtils.getField(this.config, "mediaDir");
//		String originalTopicExchange = (String) ReflectionTestUtils.getField(this.config, "topicExchange");
//		String originalDefaultQueue = (String) ReflectionTestUtils.getField(this.config, "defaultQueue");
//		
//		String getMediaDir = config.getMediaDir();
//		String getTopicExchange = config.getTopicExchange();
//		String getDefaultQueue = config.getDefaultQueue();
//		
//		assertThat(getMediaDir).isEqualTo(originalMediaDir);
//		assertThat(getTopicExchange).isEqualTo(originalTopicExchange);
//		assertThat(getDefaultQueue).isEqualTo(originalDefaultQueue);
//		
//		String testValue = "testValue";
//		
//		config.setMediaDir(testValue);
//		config.setTopicExchange(testValue);
//		config.setDefaultQueue(testValue);
//		
//		String setMediaDir = (String) ReflectionTestUtils.getField(this.config, "mediaDir");
//		String setTopicExchange = (String) ReflectionTestUtils.getField(this.config, "topicExchange");
//		String setDefaultQueue = (String) ReflectionTestUtils.getField(this.config, "defaultQueue");
//		
//		assertThat(setMediaDir).isEqualTo(testValue);
//		assertThat(setTopicExchange).isEqualTo(testValue);
//		assertThat(setDefaultQueue).isEqualTo(testValue);
//		
//		config.setMediaDir(originalMediaDir);
//		config.setTopicExchange(originalTopicExchange);
//		config.setDefaultQueue(originalDefaultQueue);
//		
//	}
//	
//}
