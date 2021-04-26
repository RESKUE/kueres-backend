package kueres.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * Provide all custom configuration options that can be set in application.properties.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0.0
 * @since Apr 26, 2021
 *
 */

@ConfigurationProperties(prefix = "kueres")
public class KueresConfigurationProperties {

	/**
	 * The local path for the media directory.
	 */
	private String mediaDir;
	
	/**
	 * The URL to a Nominatim server.
	 */
	private String nominatimUrl;
	
	/**
	 * The URL to the local FROST service.
	 */
	private String frostUrl;
	
	public String getMediaDir() { return this.mediaDir; }
	public String getNominatimUrl() { return this.nominatimUrl; }
	public String getFrostUrl() { return this.frostUrl; }
	
	public void setMediaDir(String mediaDir) { this.mediaDir = mediaDir; }
	public void setNominatimUrl(String nominatimUrl) { this.nominatimUrl = nominatimUrl; }
	public void setFrostUrl(String frostUrl) { this.frostUrl = frostUrl; }
	
}
