package kueres.media;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kueres.base.BaseController;
import kueres.base.BaseEntity;
import kueres.utility.Utility;

@RestController
@RequestMapping(BaseController.API_ENDPOINT + MediaController.ROUTE)
public class MediaController {

	public static final String ROUTE = "/media";
	
	@Autowired
	private MediaService service;
	
	@PostMapping()
	@RolesAllowed({"administrator", "helper"})
    public ResponseEntity<Long> upload(@Valid @RequestBody MultipartFile file) {
		
		Utility.LOG.trace("MediaController.upload called");
		
		MediaEntity media = service.save(file);
		if (media != null) {
			return ResponseEntity.ok().body(media.getId());
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1L);
		}
		
    }
	
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
