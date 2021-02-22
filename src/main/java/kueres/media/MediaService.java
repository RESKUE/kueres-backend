package kueres.media;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kueres.event.EventType;
import kueres.eventbus.EventConsumer;
import kueres.eventbus.EventSubscriber;
import kueres.utility.Utility;

@Service
public class MediaService extends EventSubscriber {
	
	@Autowired
	private MediaRepository mediaRepository;
	
	@Autowired
	private FileSystemRepository fileSystemRepository;
	
	@PostConstruct
	@Override
	public void init() {
		
		this.identifier = MediaController.ROUTE;
		this.routingKey = MediaController.ROUTE;
		
	}
	
	public MediaEntity save(MultipartFile multipartFile) {
		
		Utility.LOG.trace("MediaService.save called");
		
		MediaEntity media = new MediaEntity();
		media.setLocation("UPLOADING");
		media.setMimeType(multipartFile.getContentType());
		media.setAltText(multipartFile.getOriginalFilename());
		media = mediaRepository.save(media);
		
		try {
			
			String location = fileSystemRepository.save(media.getId(), multipartFile.getBytes());
			media.setLocation(location);
			MediaEntity savedMedia = mediaRepository.save(media);
			
			EventConsumer.sendEvent("MediaService.save", EventType.CREATE.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(savedMedia));
			
			return savedMedia;
			
		} catch (IOException e) {
			
			Utility.LOG.error("Could not save media: {}", e.getStackTrace().toString());
			mediaRepository.delete(media);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}
	
	public FileSystemResource getFileById(long id) {
		
		Utility.LOG.trace("MediaService.getFileById called");
		
		MediaEntity media = findById(id);
		
		return fileSystemRepository.findByLocation(media.getLocation());
		
	}
	
	public MediaEntity findById(long id) {
		
		Utility.LOG.trace("MediaService.findById called");
		
		MediaEntity media = mediaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		EventConsumer.sendEvent("MediaService.findById", EventType.READ.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(media));
		
		return media;
		
	}
	
	public boolean delete(long id) {
		
		Utility.LOG.trace("MediaService.delete called");
		
		MediaEntity media = findById(id);
		boolean fileDeleted = fileSystemRepository.delete(media.getLocation());
		if (fileDeleted) {
			mediaRepository.delete(media);
			
			EventConsumer.sendEvent("MediaService.delete", EventType.DELETE.type, this.getIdentifier(), EventConsumer.writeObjectAsJSON(media));
			
			return true;
		}
		return false;
		
	}
	
	public void setFileSystemRepository(FileSystemRepository fileSystemRepository) {
		
		Utility.LOG.trace("MediaService.setFileSystemRepository called");
		
		this.fileSystemRepository = fileSystemRepository;
		
	}
	
	public MediaEntity getEntityFromJSON(String json) throws JsonMappingException, JsonProcessingException  {
		
		Utility.LOG.trace("MediaService.getEntityFromJSON called");
		
		return new ObjectMapper().readValue(json, MediaEntity.class);
		
	}

}
