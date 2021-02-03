package kueres.media;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MediaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	public static final String ID = "id";
	public long getId() { return this.id; }
	
	@Column(name = "location", nullable = false)
	private String location = "";
	public static final String LOCATION = "location";
	public String getLocation() { return this.location; }
	public void setLocation(String location) { this.location = location; }
	
	@Column(name = "mimeType", nullable = false)
	private String mimeType = "";
	public static final String MIME_TYPE = "mimeType";
	public String getMimeType() { return this.mimeType; }
	public void setMimeType(String mimeType) { this.mimeType = mimeType; }
	
	@Column(name = "altText", nullable = false)
	private String altText = "";
	public static final String ALT_TEXT = "altText";
	public String getAltText() { return this.altText; }
	public void setAltText(String altText) { this.altText = altText; }
	
}
