package kueres.media;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import kueres.utility.Utility;

/**
 * 
 * The default implementation of the FileSystemRepository interface.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0.0
 * @since Apr 26, 2021
 *
 */

@Repository
public class DefaultFileSystemRepository implements FileSystemRepository {

	/**
	 * The path for the media directory where all files will be stored.
	 */
	@Value("${kueres.media-dir}")
	private String MEDIA_DIR;
	
	@Override
	public String save(Long id, byte[] content) {
		
		Utility.LOG.trace("DefaultFileSystemRepository.save called");
		
		File mediaDir = new File(this.MEDIA_DIR);
		if (!mediaDir.exists()) {
			
			boolean success = mediaDir.mkdir();
			if (!success) {
				
				Utility.LOG.info("media dir did not exist and could not be created: {}", this.MEDIA_DIR);
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
				
			}
			
		} else if (!mediaDir.isDirectory()) {
			
			Utility.LOG.info("media dir is not a directory: {}", this.MEDIA_DIR);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		try {
			
			Path newFile = Paths.get(MEDIA_DIR + "/" + new Date().getTime() + "-" + id);
			Files.createDirectories(newFile.getParent());
			Files.write(newFile, content);
			return newFile.toAbsolutePath().toString();
			
		} catch (IOException e) {
			
			Utility.LOG.error("File could not be saved: {}", e.getStackTrace().toString());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}

	@Override
	public FileSystemResource findByLocation(String location) {
		
		Utility.LOG.trace("DefaultFileSystemRepository.findByLocation called");
		
		File file = new File(location);
		if (file.exists()) {
			
			return new FileSystemResource(file.toPath());
			
		} else {
			
			Utility.LOG.error("No file found at location: {}", location);
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	        
		}
		
	}
	
	@Override
	public boolean delete(String location) {
		
		Utility.LOG.trace("DefaultFileSystemRepository.delete called");
		
		FileSystemResource file = findByLocation(location);
		file.getFile().delete();
		return true;
		
	}

}
