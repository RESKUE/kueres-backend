package kueres.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kueres.base.BaseController;

@RestController
@RequestMapping(BaseController.API_ENDPOINT + MediaController.ROUTE)
public class MediaController {

	public static final String ROUTE = "/media";
	
	@Autowired
	private MediaService service;
	
	@PostMapping
    public Long upload(@RequestParam MultipartFile file) {
        
		MediaEntity media = service.save(file);
		if (media != null) {
			return media.getId();
		} else {
			return -1L;
		}
		
    }
	
	@GetMapping(value = "/{" + MediaEntity.ID + "}", produces = MediaType.ALL_VALUE)
	public FileSystemResource download(@PathVariable Long id) {
		
		return service.getFileById(id);
	    
	}
	
}
