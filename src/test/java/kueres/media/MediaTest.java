//package kueres.media;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.Map;
//
//import org.apache.http.entity.ContentType;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.util.FileSystemUtils;
//import org.springframework.web.server.ResponseStatusException;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//
//@SpringBootTest
//@TestInstance(Lifecycle.PER_CLASS)
//public class MediaTest {
//
//	@Autowired
//	private MediaController controller;
//	
//	@Autowired
//	private MediaService service;
//	
//	@Autowired
//	private DefaultFileSystemRepository repository;
//
//	private File logoFile;
//	private byte[] logoContent;
//	private MockMultipartFile logoMultipart;
//
//	@BeforeAll
//	public void setup() throws IOException {
//
//		this.logoFile = new File(this.getClass().getClassLoader().getResource("logo.png").getFile());
//		this.logoContent = Files.readAllBytes(this.logoFile.toPath());
//		this.logoMultipart = new MockMultipartFile(this.logoFile.getName(), this.logoFile.getName(),
//				ContentType.IMAGE_PNG.toString(), this.logoContent);
//
//	}
//	
//	@Test
//	@WithMockUser(roles = { "administrator" })
//	public void crud() throws IOException {
//
//		ResponseEntity<Long> responseUpload = this.controller.upload(this.logoMultipart);
//		assertThat(responseUpload.getStatusCode()).isEqualTo(HttpStatus.OK);
//		assertThat(responseUpload.getBody()).isNotNull();
//
//		long id = responseUpload.getBody();
//
//		ResponseEntity<FileSystemResource> responseDownload = this.controller.download(id);
//		assertThat(responseDownload.getStatusCode()).isEqualTo(HttpStatus.OK);
//		assertThat(responseDownload.getBody()).isNotNull();
//
//		FileSystemResource resource = responseDownload.getBody();
//		File downloadedFile = resource.getFile();
//		assertThat(downloadedFile.exists()).isTrue();
//		System.out.println("Path: " + downloadedFile.getPath());
//
//		byte[] downloadedContent = Files.readAllBytes(downloadedFile.toPath());
//		assertThat(downloadedContent).isEqualTo(this.logoContent);
//		
//		Map<String, Boolean> responseDelete = this.controller.delete(id);
//		assertThat(responseDelete.containsKey("deleted")).isTrue();
//		assertThat(responseDelete.get("deleted")).isTrue();
//		
//		HttpStatus status = assertThrows(ResponseStatusException.class, () -> {
//			this.controller.download(id);
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
//	@Test
//	public void applyPatchUnsupported() {
//		
//		MediaEntity media = new MediaEntity();
//		MediaEntity patch = new MediaEntity();
//		
//		assertThrows(UnsupportedOperationException.class, () -> {
//			media.applyPatch(patch);
//		});
//		
//	}
//	
//	@Test
//	public void createsMediaDir() throws IOException {
//		
//		File testMediaDir = new File("src/test/resources/test-media-dir");
//		System.out.println("path: " + testMediaDir.getAbsolutePath());
//		
//		String originalValue = (String) ReflectionTestUtils.getField(repository, "MEDIA_DIR");
//		ReflectionTestUtils.setField(repository, "MEDIA_DIR", testMediaDir.getAbsolutePath());
//		assertThat((String) ReflectionTestUtils.getField(repository, "MEDIA_DIR")).isEqualTo(testMediaDir.getAbsolutePath());
//		
//		if (testMediaDir.exists()) {
//			FileSystemUtils.deleteRecursively(testMediaDir);
//		}
//		
//		assertThat(testMediaDir.exists()).isFalse();
//		
//		this.repository.save(0, this.logoContent);
//		
//		assertThat(testMediaDir.exists()).isTrue();
//		assertThat(testMediaDir.isDirectory()).isTrue();
//		
//		FileSystemUtils.deleteRecursively(testMediaDir);
//		assertThat(testMediaDir.exists()).isFalse();
//		
//		ReflectionTestUtils.setField(repository, "MEDIA_DIR", originalValue);
//		assertThat((String) ReflectionTestUtils.getField(repository, "MEDIA_DIR")).isEqualTo(originalValue);
//		
//	}
//	
//	@Test
//	public void invalidLocation() {
//		
//		HttpStatus status = assertThrows(ResponseStatusException.class, () -> {
//			repository.findByLocation("");
//		}).getStatus();
//		assertThat(status).isEqualTo(HttpStatus.NOT_FOUND);
//		
//	}
//	
//	@Test
//	public void mediaDirNotADirectory() {
//		
//		String originalValue = (String) ReflectionTestUtils.getField(repository, "MEDIA_DIR");
//		ReflectionTestUtils.setField(repository, "MEDIA_DIR", "");
//		assertThat((String) ReflectionTestUtils.getField(repository, "MEDIA_DIR")).isEqualTo("");
//		
//		HttpStatus status = assertThrows(ResponseStatusException.class, () -> {
//			this.repository.save(0, this.logoContent);
//		}).getStatus();
//		assertThat(status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
//		
//		ReflectionTestUtils.setField(repository, "MEDIA_DIR", originalValue);
//		assertThat((String) ReflectionTestUtils.getField(repository, "MEDIA_DIR")).isEqualTo(originalValue);
//		
//	}
//	
//}
