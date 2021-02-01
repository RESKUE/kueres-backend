package kueres.media;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import kueres.utility.Utility;

@Service
public class MediaService {
	
	@Autowired
	private MediaRepository mediaRepository;
	
	@Autowired
	private FileSystemRepository fileSystemRepository;
	
	public MediaEntity save(MultipartFile multipartFile) {
		MediaEntity media = new MediaEntity();
		media.setLocation("UPLOADING");
		media = mediaRepository.save(media);
		
		try {
			
			String location = fileSystemRepository.save(media.getId(), multipartFile.getBytes());
			media.setLocation(location);
			return mediaRepository.save(media);
			
		} catch (IOException e) {
			Utility.LOG.error(e.getMessage());
			mediaRepository.delete(media);
			return null;
		}
	}
	
	public FileSystemResource getFileById(long id) {
		MediaEntity media = mediaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return fileSystemRepository.findByLocation(media.getLocation());
	}
	
	public void setFileSystemRepository(FileSystemRepository fileSystemRepository) {
		this.fileSystemRepository = fileSystemRepository;
	}

}
