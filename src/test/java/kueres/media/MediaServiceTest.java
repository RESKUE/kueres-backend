//package kueres.media;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//
//import org.apache.http.entity.ContentType;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.HttpStatus;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//
//@SpringBootTest
//@TestInstance(Lifecycle.PER_CLASS)
//public class MediaServiceTest {
//	
//	@Autowired
//	private MediaService service;
//	
//	@Value("${kueres.media-dir}")
//	private String MEDIA_DIR;
//	
//	private File mediaDirectory;
//	
//	private File logoFile;
//	private byte[] logoContent;
//	private MultipartFile logoMultipart;
//	
//	@BeforeAll
//	public void setup() throws IOException {
//		
//		this.mediaDirectory = new File(this.MEDIA_DIR);
//		
//		this.logoFile = new File(this.getClass().getClassLoader().getResource("logo.png").getFile());
//		
//		this.logoContent = Files.readAllBytes(this.logoFile.toPath());
//		this.logoMultipart = new MockMultipartFile(
//				this.logoFile.getName(),
//				this.logoFile.getName(),
//				"image/png",
//				this.logoContent);
//		
//	}
//	
//	@Test
//	public void init() {
//		
//		assertThat(service.getIdentifier()).isEqualTo(MediaController.ROUTE);
//		assertThat(service.getRoutingKey()).isEqualTo(MediaController.ROUTE);
//		
//		assertThat(this.MEDIA_DIR).isNotEmpty();
//		assertThat(this.mediaDirectory.exists()).isTrue();
//		
//	}
//	
//	@Test
//	public void crud() throws IOException {
//		
//		//Save + findById
//		MediaEntity media = this.service.save(this.logoMultipart);
//		String locationPattern = this.MEDIA_DIR + "/[0-9]*-" + media.getId();
//		assertThat(media.getId()).isGreaterThan(0);
//		assertThat(media.getLocation()).matches(locationPattern);
//		assertThat(media.getMimeType()).isEqualTo(this.logoMultipart.getContentType());
//		assertThat(media.getAltText()).isEqualTo(this.logoMultipart.getOriginalFilename());
//		
//		MediaEntity found = this.service.findById(media.getId());
//		assertThat(found.getId()).isEqualTo(media.getId());
//		assertThat(found.getLocation()).isEqualTo(media.getLocation());
//		assertThat(found.getMimeType()).isEqualTo(media.getMimeType());
//		assertThat(found.getAltText()).isEqualTo(media.getAltText());
//		
//		File localFile = new File(media.getLocation());
//		assertThat(localFile.exists()).isTrue();
//		
//		byte[] localFileContent = Files.readAllBytes(localFile.toPath());
//		assertThat(localFileContent).isEqualTo(this.logoContent);
//		
//		//getFileById
//		FileSystemResource foundResource = this.service.getFileById(media.getId());
//		File foundFile = foundResource.getFile();
//		assertThat(foundFile.exists()).isTrue();
//		
//		byte[] foundFileContent = Files.readAllBytes(foundFile.toPath());
//		assertThat(foundFileContent).isEqualTo(this.logoContent);
//		
//		//Delete
//		this.service.delete(media.getId());
//		assertThat(localFile.exists()).isFalse();
//		HttpStatus status = assertThrows(ResponseStatusException.class, () -> {
//			this.service.findById(media.getId());
//		}).getStatus();
//		assertThat(status).isEqualTo(HttpStatus.NOT_FOUND);
//		
//	}
//	
//	@Test
//	public void getEntityFromJSON() throws JsonProcessingException {
//		
//		MediaEntity media = new MediaEntity();
//		media.setLocation("somewhere");
//		media.setMimeType(ContentType.IMAGE_PNG.toString());
//		media.setAltText("some image");
//		
//		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
//		
//		String mediaJSON = objectWriter.writeValueAsString(media);
//		
//		MediaEntity parsedMedia = this.service.getEntityFromJSON(mediaJSON);
//		assertThat(parsedMedia.getId()).isEqualTo(media.getId());
//		assertThat(parsedMedia.getLocation()).isEqualTo("");
//		assertThat(parsedMedia.getMimeType()).isEqualTo(media.getMimeType());
//		assertThat(parsedMedia.getAltText()).isEqualTo(media.getAltText());
//		
//	}
//	
//	@Test
//	public void setFileSystemRepository() {
//		
//		FileSystemRepository repository = (FileSystemRepository) ReflectionTestUtils.getField(this.service, "fileSystemRepository");
//		assertThat(repository).isNotNull();
//		
//		this.service.setFileSystemRepository(null);
//		
//		FileSystemRepository repositoryAfterSet = (FileSystemRepository) ReflectionTestUtils.getField(this.service, "fileSystemRepository");
//		assertThat(repositoryAfterSet).isNull();
//		
//		this.service.setFileSystemRepository(repository);
//		
//		FileSystemRepository originalRepository = (FileSystemRepository) ReflectionTestUtils.getField(this.service, "fileSystemRepository");
//		assertThat(originalRepository).isEqualTo(repository);
//		
//	}
//	
//}
