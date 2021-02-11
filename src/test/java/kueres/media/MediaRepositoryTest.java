//package kueres.media;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace=Replace.NONE)
//public class MediaRepositoryTest {
//	
//	@Autowired
//	private MediaRepository repository;
//	
//	@Test
//	public void crud() {
//		
//		MediaEntity media = new MediaEntity();
//		media.setLocation("somewhere");
//		media.setMimeType("image/png");
//		media.setAltText("some image");
//		
//		MediaEntity persisted = this.repository.save(media);
//		assertThat(persisted.getId()).isNotEqualTo(0);
//		
//		Optional<MediaEntity> foundOptional = this.repository.findById(persisted.getId());
//		assertThat(foundOptional).isPresent();
//		
//		MediaEntity found = foundOptional.get();
//		assertThat(found.getLocation()).isEqualTo(media.getLocation());
//		assertThat(found.getMimeType()).isEqualTo(media.getMimeType());
//		assertThat(found.getAltText()).isEqualTo(media.getAltText());
//		
//		this.repository.delete(found);
//		
//		Optional<MediaEntity> deleted = this.repository.findById(found.getId());
//		assertThat(deleted).isNotPresent();
//		
//	}
//	
//}
