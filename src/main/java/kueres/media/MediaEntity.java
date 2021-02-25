package kueres.media;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kueres.base.BaseEntity;
import kueres.utility.Utility;

/**
 * 
 * A MediaEntity contains data about a file.
 * location: The path of the file. This property is hidden.
 * mimeType: The file type
 * altText: An alternative text - normally the files name
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */
@Entity
public class MediaEntity extends BaseEntity<MediaEntity> {
	
	/**
	 * The path of the file.
	 */
	@JsonIgnore
	@Column(name = "location", nullable = false)
	private String location = "";
	public static final String LOCATION = "location";
	public String getLocation() { return this.location; }
	public void setLocation(String location) { this.location = location; }
	
	/**
	 * The file type.
	 */
	@Column(name = "mimeType", nullable = false)
	private String mimeType = "";
	public static final String MIME_TYPE = "mimeType";
	public String getMimeType() { return this.mimeType; }
	public void setMimeType(String mimeType) { this.mimeType = mimeType; }
	
	/**
	 * An alternative text like the files name.
	 */
	@Column(name = "altText", nullable = false, columnDefinition="TEXT")
	private String altText = "";
	public static final String ALT_TEXT = "altText";
	public String getAltText() { return this.altText; }
	public void setAltText(String altText) { this.altText = altText; }
	
	@Override
	public void applyPatch(MediaEntity details) {
		
		Utility.LOG.error("MediaEntities can not be updated");
		throw new UnsupportedOperationException("MediaEntities can not be updated!");
		
	}
	
}
