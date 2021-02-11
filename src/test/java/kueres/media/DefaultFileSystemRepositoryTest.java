package kueres.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class DefaultFileSystemRepositoryTest {

	@Autowired
	private DefaultFileSystemRepository repository;
	
	private File logoFile;
	private byte[] logoContent;

	@BeforeAll
	public void setup() throws IOException {

		this.logoFile = new File(this.getClass().getClassLoader().getResource("logo.png").getFile());
		this.logoContent = Files.readAllBytes(this.logoFile.toPath());

	}
	
	@Test
	public void createsMediaDir() throws IOException {
		
		File testMediaDir = new File("src/test/resources/test-media-dir");
		System.out.println("path: " + testMediaDir.getAbsolutePath());
		
		String originalValue = (String) ReflectionTestUtils.getField(repository, "MEDIA_DIR");
		ReflectionTestUtils.setField(repository, "MEDIA_DIR", testMediaDir.getAbsolutePath());
		assertThat((String) ReflectionTestUtils.getField(repository, "MEDIA_DIR")).isEqualTo(testMediaDir.getAbsolutePath());
		
		if (testMediaDir.exists()) {
			FileSystemUtils.deleteRecursively(testMediaDir);
		}
		
		assertThat(testMediaDir.exists()).isFalse();
		
		this.repository.save(0, this.logoContent);
		
		assertThat(testMediaDir.exists()).isTrue();
		assertThat(testMediaDir.isDirectory()).isTrue();
		
		FileSystemUtils.deleteRecursively(testMediaDir);
		assertThat(testMediaDir.exists()).isFalse();
		
		ReflectionTestUtils.setField(repository, "MEDIA_DIR", originalValue);
		assertThat((String) ReflectionTestUtils.getField(repository, "MEDIA_DIR")).isEqualTo(originalValue);
		
	}
	
	@Test
	public void invalidLocation() {
		
		HttpStatus status = assertThrows(ResponseStatusException.class, () -> {
			repository.findByLocation("");
		}).getStatus();
		assertThat(status).isEqualTo(HttpStatus.NOT_FOUND);
		
	}
	
	@Test
	public void mediaDirNotADirectory() {
		
		String originalValue = (String) ReflectionTestUtils.getField(repository, "MEDIA_DIR");
		ReflectionTestUtils.setField(repository, "MEDIA_DIR", "");
		assertThat((String) ReflectionTestUtils.getField(repository, "MEDIA_DIR")).isEqualTo("");
		
		HttpStatus status = assertThrows(ResponseStatusException.class, () -> {
			this.repository.save(0, this.logoContent);
		}).getStatus();
		assertThat(status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		
		ReflectionTestUtils.setField(repository, "MEDIA_DIR", originalValue);
		assertThat((String) ReflectionTestUtils.getField(repository, "MEDIA_DIR")).isEqualTo(originalValue);
		
	}
	
}
