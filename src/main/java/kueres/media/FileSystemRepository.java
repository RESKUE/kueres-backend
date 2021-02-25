package kueres.media;

import org.springframework.core.io.FileSystemResource;

/**
 * 
 * An interface for all functions needed by the media system that interact with storage.
 * A custom implementation of this interface can be created to customize how files are stored.
 * An example would be to provide an implementation that connects to an Amazon S3 bucket.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

public interface FileSystemRepository {

	/**
	 * Save a file by its MediaEntitys identifier.
	 * @param id - the files MediaEntitys identifier
	 * @param content - the byte content of the file
	 * @return The path of the file.
	 */
	public String save(long id, byte[] content);
	
	/**
	 * Find a file by its path.
	 * @param location - the path of the file
	 * @return The file specified by the path.
	 */
	public FileSystemResource findByLocation(String location);
	
	/**
	 * Delete a file by its path.
	 * @param location - the path of the file
	 * @return If the file was deleted successfully.
	 */
	public boolean delete(String location);
	
}
