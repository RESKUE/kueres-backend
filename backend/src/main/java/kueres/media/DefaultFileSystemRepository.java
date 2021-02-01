package kueres.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultFileSystemRepository implements FileSystemRepository {

	@Value("${kueres.media-dir}")
	private String MEDIA_DIR;
	
	@Override
	public String save(long id, byte[] content) throws IOException {
		Path newFile = Paths.get(MEDIA_DIR + new Date().getTime() + "-" + id);
		Files.createDirectories(newFile.getParent());
		Files.write(newFile, content);
		return newFile.toAbsolutePath().toString();
	}

	@Override
	public FileSystemResource findByLocation(String location) {
		try {
	        return new FileSystemResource(Paths.get(location));
	    } catch (Exception e) {
	        throw new ResourceNotFoundException(location + " not found in filesystem");
	    }
	}

}
