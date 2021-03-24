package kueres.media;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kueres.base.BaseController;
import kueres.base.BaseEntity;
import kueres.utility.Utility;

/**
 * 
 * The MediaController provides all API functions related to media.
 * These functions are:
 * - uploading media
 * - downloading media
 * - deleting media
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

@RestController
@RequestMapping(BaseController.API_ENDPOINT + MediaController.ROUTE)
public class MediaController {

	/**
	 * The API route for media.
	 */
	public static final String ROUTE = "/media";
	
	@Autowired
	private MediaService service;
	
	/**
	 * Upload a file.
	 * @param file - the file to be uploaded
	 * @return The identifier of the uploaded file.
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@RolesAllowed({"administrator", "helper"})
    public ResponseEntity<Long> upload(
    		//@Valid @RequestBody MultipartFile file
    		@RequestPart MultipartFile file,
    		@RequestPart String altText
    		) {
		
		Utility.LOG.trace("MediaController.upload called");
		
		Utility.LOG.info("altText: {}", altText);
		
		MediaEntity media = service.save(file, altText);
		if (media != null) {
			return ResponseEntity.ok().body(media.getId());
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1L);
		}
		
    }
	
	/**
	 * Download a file by its identifier.
	 * @param id - the files identifier
	 * @return The file specified by the identifier.
	 */
	@GetMapping(value = "/{" + MediaEntity.ID + "}", produces = MediaType.ALL_VALUE)
	@RolesAllowed({"administrator", "helper"})
	public ResponseEntity<FileSystemResource> download(@PathVariable(value = BaseEntity.ID) long id) {
		
		Utility.LOG.trace("MediaController.download called");
		
		MediaEntity media = service.findById(id);
		FileSystemResource file = service.getFileById(id);
		
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(media.getMimeType()))
				.body(file);
	    
	}
	
	/**
	 * Delete a file by its identifier.
	 * @param id - the files identifier
	 * @return If the file was successfully deleted.
	 */
	@DeleteMapping("/{" + BaseEntity.ID + "}")
	@RolesAllowed("administrator")
	public Map<String, Boolean> delete(@PathVariable(value = BaseEntity.ID) long id) {
		
		Utility.LOG.trace("MediaController.delete called");
		
		boolean deletedFile = service.delete(id);
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("deleted", deletedFile);
		return response;
		
	}
	
}
