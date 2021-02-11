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
//import org.springframework.web.server.ResponseStatusException;
//
//@SpringBootTest
//@TestInstance(Lifecycle.PER_CLASS)
//public class MediaControllerTest {
//
//	@Autowired
//	private MediaController controller;
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
//}
