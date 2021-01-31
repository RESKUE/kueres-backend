package kueres.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BaseRepository<E extends BaseEntity<E>> extends JpaRepository<E, Long>, JpaSpecificationExecutor<E>{
	
}
