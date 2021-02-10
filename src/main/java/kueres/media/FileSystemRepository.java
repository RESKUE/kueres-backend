package kueres.media;

import org.springframework.core.io.FileSystemResource;

public interface FileSystemRepository {

	public String save(long id, byte[] content);
	
	public FileSystemResource findByLocation(String location);
	
	public boolean delete(String location);
	
}
