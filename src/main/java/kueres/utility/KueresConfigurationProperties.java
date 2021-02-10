package kueres.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * ToDo: add config properties for location service
 * ToDo: remove rabbit config properties
 */

@ConfigurationProperties(prefix = "kueres")
public class KueresConfigurationProperties {

	private String mediaDir;
	private String topicExchange;
	private String defaultQueue;
	
	public String getMediaDir() { return this.mediaDir; }
	public String getTopicExchange() { return this.topicExchange; }
	public String getDefaultQueue() { return this.defaultQueue; }
	
	public void setMediaDir(String mediaDir) { this.mediaDir = mediaDir; }
	public void setTopicExchange(String topicExchange) { this.topicExchange = topicExchange; }
	public void setDefaultQueue(String defaultQueue) { this.defaultQueue = defaultQueue; }
	
}
